<template>
  <div class="activities-page">
    <section class="activities-summary">
      <article class="activities-summary__card">
        <span>活动总数</span>
        <strong>{{ activities.length }}</strong>
      </article>
      <article class="activities-summary__card">
        <span>已报名</span>
        <strong>{{ joinedCount }}</strong>
      </article>
      <article class="activities-summary__card">
        <span>可管理</span>
        <strong>{{ manageableCount }}</strong>
      </article>
      <article class="activities-summary__card">
        <span>已确认结束</span>
        <strong>{{ completedCount }}</strong>
      </article>
    </section>

    <ActivityFiltersBar
      :filters="filters"
      :clubs="clubOptions"
      :columns="columns"
      :view-mode="viewMode"
      :sort-key="sortKey"
      :sort-order="sortOrder"
      @update:filters="filters = $event"
      @update:view-mode="viewMode = $event"
      @update:sort="handleSortSettings"
      @update:column="handleColumnChange"
      @reset="resetFilters"
    />

    <section class="activities-actions">
      <div class="activities-actions__left">
        <el-button plain @click="router.push('/activities/schedule')">查看排期</el-button>
        <span class="activities-actions__hint">当前展示 {{ filteredActivities.length }} 场活动</span>
      </div>
      <el-button
        v-if="canCreateActivity"
        v-permission="'activity:create'"
        type="primary"
        @click="creatorVisible = true"
      >
        发布活动
      </el-button>
    </section>

    <ActivityTablePanel
      v-if="viewMode === 'table'"
      ref="tablePanelRef"
      :activities="sortedActivities"
      :columns="columns"
      :selection="selection"
      @update:selection="selection = $event"
      @sort-change="handleTableSort"
      @open-drawer="openDrawer"
      @open-detail="openDetailPage"
      @open-signups="openSignups"
      @signup="signup"
      @cancel-signup="cancelSignup"
      @bulk-open-signups="openBulkSignups"
      @clear-selection="clearSelection"
    />

    <ActivityCardGrid
      v-else
      :activities="sortedActivities"
      @open-drawer="openDrawer"
      @open-detail="openDetailPage"
      @open-signups="openSignups"
      @signup="signup"
      @cancel-signup="cancelSignup"
    />

    <ActivityDetailDrawer
      v-model="drawerVisible"
      :activity="selectedActivity"
      @open-detail="openDetailPage"
      @open-signups="openSignups"
      @signup="signup"
      @cancel-signup="cancelSignup"
    />

    <ActivityCreatorDialog
      v-model="creatorVisible"
      :clubs="manageableClubs"
      @success="loadActivities"
      @closed="creatorVisible = false"
    />

    <el-dialog v-model="signupVisible" title="报名名单" width="760px">
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
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ActivityCardGrid from '../components/business/activity/ActivityCardGrid.vue'
import ActivityCreatorDialog from '../components/business/activity/ActivityCreatorDialog.vue'
import ActivityDetailDrawer from '../components/business/activity/ActivityDetailDrawer.vue'
import ActivityFiltersBar from '../components/business/activity/ActivityFiltersBar.vue'
import ActivityTablePanel from '../components/business/activity/ActivityTablePanel.vue'
import {
  cancelSignupActivity,
  getActivities,
  getActivitySignups,
  signupActivity,
  type ActivitySignup,
  type ActivitySummary,
} from '../api/activity'
import { getClubs, type ClubSummary } from '../api/club'
import type {
  ActivityColumnState,
  ActivityFilters,
  ActivitySortKey,
  ActivitySortOrder,
  ActivityViewMode,
} from '../types/activity-view'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const session = useSessionStore()
const tablePanelRef = ref<InstanceType<typeof ActivityTablePanel> | null>(null)
const activities = ref<ActivitySummary[]>([])
const clubs = ref<ClubSummary[]>([])
const signups = ref<ActivitySignup[]>([])
const selection = ref<ActivitySummary[]>([])
const selectedActivity = ref<ActivitySummary | null>(null)
const drawerVisible = ref(false)
const creatorVisible = ref(false)
const signupVisible = ref(false)
const viewMode = ref<ActivityViewMode>('table')
const sortKey = ref<ActivitySortKey>('activeTime')
const sortOrder = ref<ActivitySortOrder>(null)

const filters = ref<ActivityFilters>({
  keyword: '',
  clubId: '',
  status: 'all',
  signup: 'all',
})

const columns = ref<ActivityColumnState>({
  clubName: true,
  activeTime: true,
  location: true,
  capacity: true,
  total: true,
  completionStatus: true,
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
  const keyword = filters.value.keyword.trim().toLowerCase()

  return activities.value.filter((activity) => {
    const matchesKeyword = !keyword || [activity.name, activity.summary, activity.location, activity.clubName]
      .some((value) => value?.toLowerCase().includes(keyword))

    const matchesClub = !filters.value.clubId || activity.clubId === filters.value.clubId
    const matchesStatus = filters.value.status === 'all' || String(activity.completionStatus) === filters.value.status

    const matchesSignup = (() => {
      switch (filters.value.signup) {
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

const sortedActivities = computed(() => {
  const list = [...filteredActivities.value]
  if (!sortOrder.value) return list

  const factor = sortOrder.value === 'ascending' ? 1 : -1
  return list.sort((left, right) => {
    const leftValue = left[sortKey.value]
    const rightValue = right[sortKey.value]

    if (sortKey.value === 'activeTime') {
      const leftTime = new Date(String(leftValue).replace(' ', 'T')).getTime()
      const rightTime = new Date(String(rightValue).replace(' ', 'T')).getTime()
      return (leftTime - rightTime) * factor
    }

    if (typeof leftValue === 'number' && typeof rightValue === 'number') {
      return (leftValue - rightValue) * factor
    }

    return String(leftValue).localeCompare(String(rightValue)) * factor
  })
})

async function loadActivities() {
  const [activityRes, clubRes] = await Promise.all([getActivities(), getClubs()])
  if (activityRes.code !== 0) throw new Error(activityRes.message)
  if (clubRes.code !== 0) throw new Error(clubRes.message)
  activities.value = activityRes.data
  clubs.value = clubRes.data
}

function handleSortSettings(payload: { key: ActivitySortKey; order: ActivitySortOrder }) {
  sortKey.value = payload.key
  sortOrder.value = payload.order
}

function handleTableSort(payload: { prop: string; order: ActivitySortOrder }) {
  if (!payload.prop) return
  sortKey.value = payload.prop as ActivitySortKey
  sortOrder.value = payload.order
}

function handleColumnChange(payload: { key: keyof ActivityColumnState; value: boolean }) {
  columns.value = {
    ...columns.value,
    [payload.key]: payload.value,
  }
}

function resetFilters() {
  filters.value = {
    keyword: '',
    clubId: '',
    status: 'all',
    signup: 'all',
  }
  sortKey.value = 'activeTime'
  sortOrder.value = null
}

function openDrawer(activity: ActivitySummary) {
  selectedActivity.value = activity
  drawerVisible.value = true
}

function openDetailPage(activityId: string) {
  router.push(`/activities/${activityId}`)
}

async function signup(activityId: string) {
  try {
    const result = await signupActivity(activityId)
    if (result.code !== 0) throw new Error(result.message)
    ElMessage.success(result.message || '报名成功')
    await loadActivities()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '报名失败')
  }
}

async function cancelSignup(activityId: string) {
  try {
    await ElMessageBox.confirm('确认取消报名吗？', '取消报名', { type: 'warning' })
    const result = await cancelSignupActivity(activityId)
    if (result.code !== 0) throw new Error(result.message)
    ElMessage.success(result.message || '已取消报名')
    await loadActivities()
  } catch (error: any) {
    if (error === 'cancel') return
    ElMessage.error(error?.response?.data?.msg || error?.message || '取消报名失败')
  }
}

async function openSignups(activity: ActivitySummary) {
  try {
    const result = await getActivitySignups(activity.id)
    if (result.code !== 0) throw new Error(result.message)
    signups.value = result.data
    signupVisible.value = true
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载报名名单失败')
  }
}

async function openBulkSignups() {
  const manageable = selection.value.find((item) => item.manageable)
  if (!manageable) {
    ElMessage.info('当前选择中没有可查看名单的活动')
    return
  }
  await openSignups(manageable)
}

function clearSelection() {
  selection.value = []
  tablePanelRef.value?.clearSelection()
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
.activities-page {
  display: grid;
  gap: 20px;
}

.activities-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.activities-summary__card,
.activities-actions {
  border: 1px solid rgba(208, 219, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 18px 36px rgba(20, 51, 93, 0.07);
}

.activities-summary__card {
  display: grid;
  gap: 10px;
  padding: 18px 20px;
}

.activities-summary__card span {
  color: #6f7d8c;
}

.activities-summary__card strong {
  font-size: 34px;
  line-height: 1;
  color: #183962;
}

.activities-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
}

.activities-actions__left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.activities-actions__hint {
  color: #6f7d8c;
}

@media (max-width: 1180px) {
  .activities-summary {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 760px) {
  .activities-summary {
    grid-template-columns: 1fr;
  }

  .activities-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
