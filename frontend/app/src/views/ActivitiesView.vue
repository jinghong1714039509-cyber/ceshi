<template>
  <div class="page-stack">
    <section class="metrics-grid">
      <article class="metric-card">
        <span class="metric-card__label">活动总数</span>
        <strong>{{ activities.length }}</strong>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">已报名</span>
        <strong>{{ joinedCount }}</strong>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">可管理</span>
        <strong>{{ manageableCount }}</strong>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">已确认结束</span>
        <strong>{{ completedCount }}</strong>
      </article>
    </section>

    <section class="section-panel">
      <div class="activity-filters">
        <el-input v-model="filters.keyword" clearable placeholder="搜索活动、地点、社团" />
        <el-select v-model="filters.clubId" clearable placeholder="筛选社团">
          <el-option v-for="club in clubOptions" :key="club.id" :label="club.name" :value="club.id" />
        </el-select>
        <el-select v-model="filters.status" placeholder="结束状态">
          <el-option label="全部状态" value="all" />
          <el-option label="进行中" value="0" />
          <el-option label="待教师确认" value="1" />
          <el-option label="已确认" value="2" />
          <el-option label="需补充" value="3" />
        </el-select>
        <el-select v-model="filters.signup" placeholder="报名状态">
          <el-option label="全部活动" value="all" />
          <el-option label="我已报名" value="joined" />
          <el-option label="未报名" value="not-joined" />
          <el-option label="我可管理" value="manageable" />
        </el-select>
        <el-button plain @click="resetFilters">重置</el-button>
      </div>

      <div class="card-footer">
        <span class="muted-text">共 {{ filteredActivities.length }} 场活动</span>
        <el-button v-if="canCreateActivity" type="primary" @click="creatorVisible = true">发布活动</el-button>
      </div>

      <div class="activity-grid">
        <article v-for="activity in filteredActivities" :key="activity.id" class="paper-list-card activity-entry">
          <img :src="coverFor(activity)" :alt="activity.name" />
          <div class="paper-list-card__body">
            <div class="inline-meta">
              <span class="meta-pill">{{ activity.clubName }}</span>
              <span class="meta-pill">{{ activity.location }}</span>
              <span class="meta-pill">{{ completionLabel(activity.completionStatus) }}</span>
            </div>
            <h2 class="paper-list-card__title">{{ activity.name }}</h2>
            <p class="paper-list-card__text">{{ activity.summary }}</p>
            <div class="activity-entry__meta">
              <span>{{ activity.activeTime }}</span>
              <span>{{ activity.total }}/{{ activity.capacity || '不限' }} 人</span>
            </div>

            <div v-if="activity.completionStatus > 0" class="activity-entry__completion">
              <strong>活动结束</strong>
              <span v-if="activity.completionSummary">{{ activity.completionSummary }}</span>
              <span v-if="activity.completionReviewComment">教师意见：{{ activity.completionReviewComment }}</span>
            </div>

            <div class="card-footer">
              <div class="toolbar">
                <el-tag v-if="activity.joined" type="success">已报名</el-tag>
                <el-tag v-if="activity.manageable" type="warning">可管理</el-tag>
              </div>
              <div class="toolbar">
                <el-button text @click="router.push(`/activities/${activity.id}`)">查看详情</el-button>
                <el-button v-if="activity.manageable" plain @click="openSignups(activity)">查看名单</el-button>
                <el-button v-if="activity.joined && !activity.manageable" type="danger" plain @click="cancelSignup(activity.id)">
                  取消报名
                </el-button>
                <el-button v-else-if="!activity.joined" type="primary" @click="signup(activity.id)">参加活动</el-button>
              </div>
            </div>
          </div>
        </article>
      </div>

      <div v-if="!filteredActivities.length" class="empty-state">当前筛选条件下没有活动。</div>
    </section>

    <el-dialog v-model="creatorVisible" title="发布活动" width="760px" @closed="resetForm">
      <el-form label-position="top" :model="form">
        <div class="form-grid">
          <el-form-item label="所属社团">
            <el-select v-model="form.clubId" placeholder="请选择社团">
              <el-option v-for="club in manageableClubs" :key="club.id" :label="club.name" :value="club.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="活动名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="活动时间">
            <el-input v-model="form.activeTime" placeholder="例如：2026-04-20 18:30:00" />
          </el-form-item>
          <el-form-item label="活动地点">
            <el-input v-model="form.location" placeholder="例如：综合楼中庭" />
          </el-form-item>
          <el-form-item label="人数上限">
            <el-input-number v-model="form.capacity" :min="1" :max="500" class="full-width" />
          </el-form-item>
          <el-form-item label="默认封面">
            <el-select v-model="form.coverImage" clearable placeholder="不选则自动使用默认校园封面">
              <el-option v-for="cover in defaultCoverImages" :key="cover" :label="cover" :value="cover" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="上传封面">
          <div class="cover-upload">
            <input ref="uploadInput" type="file" accept="image/*" class="cover-upload__input" @change="handleCoverChange" />
            <div class="toolbar">
              <el-button :loading="coverUploading" plain @click="openUploadDialog">上传图片</el-button>
              <el-button v-if="uploadedCoverUrl" plain @click="clearUploadedCover">清除上传</el-button>
            </div>
            <div v-if="coverPreview" class="cover-upload__preview">
              <img :src="coverPreview" alt="封面预览" />
              <span>{{ uploadedCoverName || '已选择封面' }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="活动简介">
          <el-input v-model="form.summary" />
        </el-form-item>
        <el-form-item label="活动详情">
          <el-input v-model="form.detail" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="参与要求">
          <el-input v-model="form.requirement" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="creatorVisible = false">取消</el-button>
        <el-button type="primary" @click="submitActivity">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="signupVisible" title="报名名单" width="720px">
      <el-table :data="signups">
        <el-table-column prop="userName" label="姓名" />
        <el-table-column prop="userGender" label="性别" />
        <el-table-column prop="userPhone" label="电话" />
        <el-table-column prop="createTime" label="报名时间" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelSignupActivity,
  createActivity,
  getActivities,
  getActivitySignups,
  signupActivity,
  uploadActivityCover,
  type ActivitySignup,
  type ActivitySummary,
} from '../api/activity'
import { getClubs, type ClubSummary } from '../api/club'
import { defaultCoverImages, pickDefaultCover } from '../content/default-covers'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const session = useSessionStore()
const uploadInput = ref<HTMLInputElement | null>(null)
const activities = ref<ActivitySummary[]>([])
const clubs = ref<ClubSummary[]>([])
const signups = ref<ActivitySignup[]>([])
const creatorVisible = ref(false)
const signupVisible = ref(false)
const coverUploading = ref(false)
const coverPreview = ref('')
const uploadedCoverUrl = ref('')
const uploadedCoverName = ref('')

const form = reactive({
  clubId: '',
  name: '',
  summary: '',
  detail: '',
  requirement: '',
  activeTime: '',
  location: '',
  capacity: 20,
  coverImage: '',
})

const filters = reactive({
  keyword: '',
  clubId: '',
  status: 'all',
  signup: 'all',
})

const canCreateActivity = computed(() => ['club-admin', 'teacher-admin', 'super-admin'].includes(session.role))
const manageableClubs = computed(() => clubs.value.filter((club) => club.canManage))
const clubOptions = computed(() => {
  const map = new Map<string, ClubSummary>()
  clubs.value.forEach((club) => map.set(club.id, club))
  return Array.from(map.values())
})
const joinedCount = computed(() => activities.value.filter((item) => item.joined).length)
const manageableCount = computed(() => activities.value.filter((item) => item.manageable).length)
const completedCount = computed(() => activities.value.filter((item) => item.completionStatus === 2).length)
const filteredActivities = computed(() => {
  const keyword = filters.keyword.trim().toLowerCase()
  return activities.value.filter((activity) => {
    const matchesKeyword = !keyword || [activity.name, activity.summary, activity.location, activity.clubName]
      .some((value) => value?.toLowerCase().includes(keyword))

    const matchesClub = !filters.clubId || activity.clubId === filters.clubId
    const matchesStatus = filters.status === 'all' || String(activity.completionStatus) === filters.status

    const matchesSignup = (() => {
      switch (filters.signup) {
        case 'joined':
          return activity.joined
        case 'not-joined':
          return !activity.joined
        case 'manageable':
          return activity.manageable
        default:
          return true
      }
    })()

    return matchesKeyword && matchesClub && matchesStatus && matchesSignup
  })
})

function coverFor(activity: ActivitySummary) {
  return activity.coverImage || pickDefaultCover(activity.id)
}

function completionLabel(status: number) {
  switch (status) {
    case 1:
      return '待教师确认'
    case 2:
      return '已确认'
    case 3:
      return '需补充'
    default:
      return '进行中'
  }
}

function resetFilters() {
  filters.keyword = ''
  filters.clubId = ''
  filters.status = 'all'
  filters.signup = 'all'
}

function resetForm() {
  Object.assign(form, {
    clubId: '',
    name: '',
    summary: '',
    detail: '',
    requirement: '',
    activeTime: '',
    location: '',
    capacity: 20,
    coverImage: '',
  })
  coverPreview.value = ''
  uploadedCoverUrl.value = ''
  uploadedCoverName.value = ''
}

async function loadActivities() {
  const [activityRes, clubRes] = await Promise.all([getActivities(), getClubs()])
  if (activityRes.code !== 0) throw new Error(activityRes.msg)
  if (clubRes.code !== 0) throw new Error(clubRes.msg)
  activities.value = activityRes.data
  clubs.value = clubRes.data
}

async function signup(activityId: string) {
  try {
    const result = await signupActivity(activityId)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '报名成功')
    await loadActivities()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '报名失败')
  }
}

async function cancelSignup(activityId: string) {
  try {
    await ElMessageBox.confirm('确认取消报名吗？', '取消报名', { type: 'warning' })
    const result = await cancelSignupActivity(activityId)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '已取消报名')
    await loadActivities()
  } catch (error: any) {
    if (error === 'cancel') return
    ElMessage.error(error?.response?.data?.msg || error?.message || '取消报名失败')
  }
}

async function openSignups(activity: ActivitySummary) {
  try {
    const result = await getActivitySignups(activity.id)
    if (result.code !== 0) throw new Error(result.msg)
    signups.value = result.data
    signupVisible.value = true
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载报名名单失败')
  }
}

function openUploadDialog() {
  uploadInput.value?.click()
}

function clearUploadedCover() {
  uploadedCoverUrl.value = ''
  uploadedCoverName.value = ''
  coverPreview.value = ''
}

async function handleCoverChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  try {
    coverUploading.value = true
    const result = await uploadActivityCover(file)
    if (result.code !== 0) throw new Error(result.msg)
    uploadedCoverUrl.value = result.data.url
    uploadedCoverName.value = result.data.name
    coverPreview.value = result.data.url
    ElMessage.success('封面上传成功')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '封面上传失败')
  } finally {
    coverUploading.value = false
    if (uploadInput.value) uploadInput.value.value = ''
  }
}

async function submitActivity() {
  try {
    const result = await createActivity({
      ...form,
      coverImage: uploadedCoverUrl.value || form.coverImage || undefined,
    })
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '发布成功')
    creatorVisible.value = false
    await loadActivities()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '发布活动失败')
  }
}

onMounted(async () => {
  try {
    await loadActivities()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载活动失败')
  }
})
</script>

<style scoped>
.activity-filters {
  display: grid;
  grid-template-columns: 1.4fr 1fr 1fr 1fr auto;
  gap: 12px;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(310px, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.activity-entry img {
  display: block;
  width: 100%;
  height: 220px;
  object-fit: cover;
}

.activity-entry__meta,
.activity-entry__completion,
.cover-upload__preview {
  display: grid;
  gap: 6px;
  color: var(--ink-soft);
}

.activity-entry__completion strong {
  color: var(--ink);
}

.cover-upload {
  display: grid;
  gap: 12px;
}

.cover-upload__input {
  display: none;
}

.cover-upload__preview img {
  width: 240px;
  height: 150px;
  object-fit: cover;
  border: 1px solid var(--line);
  border-radius: 10px;
}

@media (max-width: 1180px) {
  .activity-filters {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 760px) {
  .activity-filters {
    grid-template-columns: 1fr;
  }
}
</style>
