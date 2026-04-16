<template>
  <div class="activity-schedule-page">
    <section class="activity-schedule-page__top">
      <div>
        <h2>活动排期视图</h2>
        <span>以时间线展示活动当前所处阶段，并从右侧查看生命周期详情。</span>
      </div>
      <el-button plain @click="router.push('/activities')">返回活动列表</el-button>
    </section>

    <ActivityScheduleBoard :blocks="blocks" :days="days" @open-detail="openDrawer" />
    <ActivityScheduleDrawer v-model="drawerVisible" :block="selectedBlock" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ActivityScheduleBoard from '../components/schedule/ActivityScheduleBoard.vue'
import ActivityScheduleDrawer from '../components/schedule/ActivityScheduleDrawer.vue'
import { getActivities, type ActivitySummary } from '../api/activity'
import { buildLifecycleBlocks, type ActivityLifecycleBlock } from '../types/activity-view'

const router = useRouter()
const activities = ref<ActivitySummary[]>([])
const drawerVisible = ref(false)
const selectedBlock = ref<ActivityLifecycleBlock | null>(null)

const blocks = computed(() => buildLifecycleBlocks(activities.value))
const days = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const startOfWeek = new Date(today)
  startOfWeek.setDate(today.getDate() - today.getDay() + 1)

  return Array.from({ length: 28 }).map((_, index) => {
    const current = new Date(startOfWeek)
    current.setDate(startOfWeek.getDate() + index)
    return {
      key: current.toISOString().slice(0, 10),
      label: `${current.getMonth() + 1}/${current.getDate()}`,
    }
  })
})

function openDrawer(block: ActivityLifecycleBlock) {
  selectedBlock.value = block
  drawerVisible.value = true
}

onMounted(async () => {
  try {
    const result = await getActivities()
    if (result.code !== 0) throw new Error(result.message)
    activities.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载活动排期失败')
  }
})
</script>

<style scoped>
.activity-schedule-page {
  display: grid;
  gap: 20px;
}

.activity-schedule-page__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
  border: 1px solid rgba(208, 219, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 18px 36px rgba(20, 51, 93, 0.07);
}

.activity-schedule-page__top h2 {
  margin: 0 0 8px;
  color: #183962;
}

.activity-schedule-page__top span {
  color: #6f7d8c;
}

@media (max-width: 760px) {
  .activity-schedule-page__top {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
