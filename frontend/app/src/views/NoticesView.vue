<template>
  <div class="page-stack">
    <section class="page-header">
      <div>
        <p class="page-eyebrow">Notice Center</p>
        <h1 class="page-title">公告中心</h1>
        <p class="page-subtitle">
          系统公告和社团公告统一展示。教师与社团管理员都可以向自己负责的社团发布通知。
        </p>
      </div>
      <div class="page-actions">
        <el-button v-if="session.role !== 'student'" type="primary" @click="dialogVisible = true">发布公告</el-button>
      </div>
    </section>

    <section class="metrics-grid">
      <article class="metric-card">
        <span class="metric-card__label">公告总数</span>
        <strong>{{ notices.length }}</strong>
        <span class="stat-note">当前账号可见的全部公告。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">系统公告</span>
        <strong>{{ systemNoticeCount }}</strong>
        <span class="stat-note">面向全平台成员发布的消息。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">社团公告</span>
        <strong>{{ teamNoticeCount }}</strong>
        <span class="stat-note">挂靠到具体社团的日常通知。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">可管理社团</span>
        <strong>{{ manageableClubs.length }}</strong>
        <span class="stat-note">当前账号可发布公告的社团数量。</span>
      </article>
    </section>

    <section class="section-panel">
      <div class="section-header">
        <div>
          <p>Feed</p>
          <h3>公告流</h3>
          <span>适合展示招新提醒、活动安排、值班通知与临时变更。</span>
        </div>
      </div>
      <div class="card-grid" style="margin-top: 18px;">
        <article v-for="notice in notices" :key="notice.id" class="data-card">
          <h3>{{ notice.title }}</h3>
          <div class="inline-meta">
            <span class="meta-pill">{{ notice.systemNotice ? '系统公告' : notice.teamName || '社团公告' }}</span>
            <span class="meta-pill">{{ notice.createTime }}</span>
          </div>
          <p>{{ notice.detail }}</p>
        </article>
        <div v-if="notices.length === 0" class="empty-state">暂无公告</div>
      </div>
    </section>

    <el-dialog v-model="dialogVisible" title="发布公告" width="640px">
      <el-form label-position="top" :model="form">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.detail" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item v-if="session.role !== 'student'" label="所属社团">
          <el-select v-model="form.teamId" clearable placeholder="系统管理员可留空，表示发布系统公告">
            <el-option v-for="club in manageableClubs" :key="club.id" :label="club.name" :value="club.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNotice">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createNotice, getNotices, type NoticeItem } from '../api/notice'
import { getClubs, type ClubSummary } from '../api/club'
import { useSessionStore } from '../stores/session'

const session = useSessionStore()
const notices = ref<NoticeItem[]>([])
const clubs = ref<ClubSummary[]>([])
const dialogVisible = ref(false)

const form = reactive({
  title: '',
  detail: '',
  teamId: '',
})

const manageableClubs = computed(() => {
  if (session.role === 'super-admin') return clubs.value
  return clubs.value.filter((club) => club.canManage)
})
const systemNoticeCount = computed(() => notices.value.filter((item) => item.systemNotice).length)
const teamNoticeCount = computed(() => notices.value.filter((item) => !item.systemNotice).length)

async function loadNotices() {
  try {
    const result = await getNotices()
    if (result.code !== 0) throw new Error(result.msg)
    notices.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载公告失败')
  }
}

async function loadClubs() {
  try {
    const result = await getClubs()
    if (result.code === 0) clubs.value = result.data
  } catch {
    // Ignore preload error.
  }
}

async function submitNotice() {
  try {
    const payload = {
      title: form.title,
      detail: form.detail,
      teamId: form.teamId || undefined,
    }
    const result = await createNotice(payload)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '发布成功')
    dialogVisible.value = false
    Object.assign(form, { title: '', detail: '', teamId: '' })
    await loadNotices()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '发布失败')
  }
}

onMounted(() => {
  void loadNotices()
  void loadClubs()
})
</script>
