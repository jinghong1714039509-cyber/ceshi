<template>
  <div class="page-stack">
    <section class="page-header">
      <div>
        <p class="page-eyebrow">Club Spaces</p>
        <h1 class="page-title">社团空间</h1>
      </div>
    </section>

    <section class="metrics-grid">
      <article class="metric-card">
        <span class="metric-card__label">加入社团</span>
        <strong>{{ overview.myClubs.length }}</strong>
        <span class="stat-note">可直接进入对应的社团空间。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">已报名活动</span>
        <strong>{{ overview.myActivities.length }}</strong>
        <span class="stat-note">可以继续查看活动详情和时间安排。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">待处理申请</span>
        <strong>{{ pendingApplications }}</strong>
        <span class="stat-note">社团申请仍在审核中的数量。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">当前身份</span>
        <strong>{{ overview.displayName || session.displayName }}</strong>
        <span class="stat-note">{{ roleLabel }}</span>
      </article>
    </section>

    <section class="grid-two">
      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Spaces</p>
            <h3>可进入的社团空间</h3>
          </div>
        </div>
        <div class="split-list">
          <div v-for="club in overview.myClubs" :key="club.id" class="split-list__item">
            <div>
              <strong>{{ club.name }}</strong>
              <span>{{ club.category || '未分类' }}</span>
            </div>
            <div class="toolbar">
              <el-button plain @click="router.push(`/clubs/${club.id}`)">查看社团</el-button>
              <el-button type="primary" @click="router.push(`/clubs/${club.id}/space`)">进入群聊空间</el-button>
            </div>
          </div>
          <div v-if="overview.myClubs.length === 0" class="empty-state">暂时没有可进入的社团空间。</div>
        </div>
      </article>

      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Activities</p>
            <h3>我的活动记录</h3>
          </div>
        </div>
        <div class="split-list">
          <div v-for="activity in overview.myActivities" :key="activity.id" class="split-list__item">
            <div>
              <strong>{{ activity.name }}</strong>
              <span>{{ activity.activeTime }}</span>
            </div>
            <el-button text @click="router.push(`/activities/${activity.id}`)">查看详情</el-button>
          </div>
          <div v-if="overview.myActivities.length === 0" class="empty-state">暂时没有活动记录。</div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRoleLabel } from '../app-shell'
import { getProfileOverview } from '../api/profile'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const session = useSessionStore()

const overview = reactive({
  userId: '',
  userName: '',
  displayName: '',
  role: '',
  myClubs: [] as any[],
  myActivities: [] as any[],
  myApplications: [] as any[],
})

const pendingApplications = computed(() => overview.myApplications.filter((item) => item.status === 0).length)
const roleLabel = computed(() => getRoleLabel(overview.role || session.role))

async function loadOverview() {
  try {
    const result = await getProfileOverview()
    if (result.code !== 0) throw new Error(result.msg)
    Object.assign(overview, result.data)
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载社团空间失败')
  }
}

onMounted(() => {
  void loadOverview()
})
</script>
