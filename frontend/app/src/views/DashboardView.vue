<template>
  <div class="dashboard-page">
    <template v-if="isManager">
      <section class="dashboard-hero">
        <div class="dashboard-hero__content">
          <span class="dashboard-hero__eyebrow">Overview</span>
          <h2>校园社团运营总览</h2>
          <p>把社团、活动、审批和消息统一放在一张首页里，方便答辩时展示完整业务闭环。</p>
        </div>

        <div class="dashboard-hero__actions">
          <button
            v-for="action in visibleQuickActions"
            :key="action.path"
            type="button"
            class="dashboard-action"
            @click="router.push(action.path)"
          >
            <strong>{{ action.title }}</strong>
            <span>{{ action.description }}</span>
          </button>
        </div>
      </section>

      <section class="dashboard-metrics">
        <MetricOverviewCard v-for="card in summary?.cards ?? []" :key="card.key" :card="card" />
      </section>

      <section class="dashboard-grid">
        <article class="dashboard-panel dashboard-panel--chart">
          <div class="dashboard-panel__header">
            <div>
              <h3>近 30 天活动报名趋势</h3>
              <span>按活动报名总人数聚合，适合展示校园活动热度波动</span>
            </div>
          </div>
          <BaseChart v-if="signupsTrendOption" :option="signupsTrendOption" />
        </article>

        <article class="dashboard-panel">
          <div class="dashboard-panel__header">
            <div>
              <h3>Top 5 活跃社团</h3>
              <span>按成员规模与活动数综合排序</span>
            </div>
          </div>
          <ol class="dashboard-rank">
            <li v-for="item in summary?.activeClubRanking ?? []" :key="item.name">
              <div>
                <strong>{{ item.name }}</strong>
                <span>{{ item.extra }}</span>
              </div>
              <b>{{ item.value }}</b>
            </li>
          </ol>
        </article>

        <article class="dashboard-panel dashboard-panel--chart">
          <div class="dashboard-panel__header">
            <div>
              <h3>社团分类成员分布</h3>
              <span>展示各类社团吸纳成员情况</span>
            </div>
          </div>
          <BaseChart v-if="categoryOption" :option="categoryOption" />
        </article>

        <article class="dashboard-panel dashboard-panel--chart">
          <div class="dashboard-panel__header">
            <div>
              <h3>近 7 日审批趋势</h3>
              <span>用于展示审批工作量变化</span>
            </div>
          </div>
          <BaseChart v-if="approvalTrendOption" :option="approvalTrendOption" />
        </article>
      </section>

      <section class="dashboard-grid dashboard-grid--secondary">
        <article class="dashboard-panel">
          <div class="dashboard-panel__header">
            <div>
              <h3>待处理事项</h3>
              <span>适合在教师与管理员首页做消息提醒</span>
            </div>
          </div>
          <div class="dashboard-todo">
            <button
              v-for="item in summary?.pendingItems ?? []"
              :key="item.title"
              type="button"
              class="dashboard-todo__item"
              @click="router.push(item.path)"
            >
              <div>
                <strong>{{ item.title }}</strong>
                <span>{{ todoToneLabel(item.tone) }}</span>
              </div>
              <b>{{ item.value }}</b>
            </button>
          </div>
        </article>

        <article class="dashboard-panel dashboard-panel--chart">
          <div class="dashboard-panel__header">
            <div>
              <h3>活动完成情况</h3>
              <span>展示活动结束确认进度</span>
            </div>
          </div>
          <BaseChart v-if="completionOption" :option="completionOption" />
        </article>
      </section>
    </template>

    <template v-else>
      <div class="page-stack">
        <section class="section-panel student-home">
          <div class="student-home__hero">
            <h2 class="page-title">校园论坛</h2>
            <div class="toolbar">
              <el-button type="primary" @click="router.push('/forum')">进入论坛</el-button>
              <el-button plain @click="router.push('/activities')">查看活动</el-button>
            </div>
          </div>
        </section>

        <section class="grid-two">
          <article class="section-panel">
            <h2 class="page-title page-title--section">最新帖子</h2>
            <div class="student-feed">
              <article v-for="post in posts" :key="post.id" class="paper-list-card student-feed__card">
                <img :src="post.coverImage || pickDefaultCover(post.id)" :alt="post.title" />
                <div class="paper-list-card__body">
                  <div class="inline-meta">
                    <span class="meta-pill">{{ post.authorName }}</span>
                    <span class="meta-pill">{{ post.createTime }}</span>
                  </div>
                  <h3 class="paper-list-card__title">{{ post.title }}</h3>
                  <p class="paper-list-card__text">{{ post.content }}</p>
                </div>
              </article>
            </div>
          </article>

          <article class="section-panel">
            <h2 class="page-title page-title--section">可报名活动</h2>
            <div class="split-list">
              <div v-for="activity in activities" :key="activity.id" class="split-list__item">
                <div>
                  <strong>{{ activity.name }}</strong>
                  <span>{{ activity.activeTime }} · {{ activity.location }}</span>
                </div>
                <div class="toolbar">
                  <el-button text @click="router.push(`/activities/${activity.id}`)">查看</el-button>
                  <el-tag v-if="activity.joined" type="success">已报名</el-tag>
                  <el-button v-else type="primary" plain @click="signup(activity.id)">报名</el-button>
                </div>
              </div>
            </div>
          </article>
        </section>

        <section class="section-panel">
          <h2 class="page-title page-title--section">我的社团</h2>
          <div class="split-list">
            <div v-for="club in clubs" :key="club.id" class="split-list__item">
              <div>
                <strong>{{ club.name }}</strong>
                <span>{{ club.category }} · {{ club.totalMembers }} 人</span>
              </div>
              <div class="toolbar">
                <el-button text @click="router.push(`/clubs/${club.id}`)">社团详情</el-button>
                <el-button v-if="club.canEnterSpace" plain @click="router.push(`/clubs/${club.id}/space`)">社团空间</el-button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { EChartsOption } from 'echarts'
import { getActivities, signupActivity, type ActivitySummary } from '../api/activity'
import { getClubs, type ClubSummary } from '../api/club'
import { getDashboardSummary } from '../api/dashboard'
import { getForumPosts, type ForumPost } from '../api/forum'
import MetricOverviewCard from '../components/business/dashboard/MetricOverviewCard.vue'
import BaseChart from '../components/charts/BaseChart.vue'
import { pickDefaultCover } from '../content/default-covers'
import { useSessionStore } from '../stores/session'
import { hasPermission } from '../utils/access'
import type { DashboardSummary } from '../types/dashboard'

const router = useRouter()
const session = useSessionStore()
const posts = ref<ForumPost[]>([])
const activities = ref<ActivitySummary[]>([])
const clubs = ref<ClubSummary[]>([])
const summary = ref<DashboardSummary | null>(null)

const chartPalette = ['#5b8ff9', '#7bc8a4', '#f0b35a', '#ec7b65', '#7b88c6', '#70a1d7']

const isManager = computed(() => ['super-admin', 'club-admin', 'teacher-admin'].includes(session.role))
const visibleQuickActions = computed(() =>
  (summary.value?.quickActions ?? []).filter((item) => !item.permission || hasPermission(session.permissions, item.permission)),
)

const signupsTrendOption = computed<EChartsOption | null>(() => {
  if (!summary.value) return null

  return {
    color: ['#5b8ff9'],
    grid: { top: 20, right: 18, bottom: 26, left: 42 },
    tooltip: { trigger: 'axis' as const },
    xAxis: {
      type: 'category' as const,
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#d8e1eb' } },
      axisLabel: { color: '#6f7d90' },
      data: summary.value.signupsTrend.map((item) => item.label),
    },
    yAxis: {
      type: 'value' as const,
      splitLine: { lineStyle: { color: '#eef3f8' } },
      axisLabel: { color: '#6f7d90' },
    },
    series: [
      {
        type: 'line' as const,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        areaStyle: { color: 'rgba(91, 143, 249, 0.14)' },
        lineStyle: { width: 3 },
        data: summary.value.signupsTrend.map((item) => item.value),
      },
    ],
  }
})

const approvalTrendOption = computed<EChartsOption | null>(() => {
  if (!summary.value) return null

  return {
    color: ['#7bc8a4'],
    grid: { top: 20, right: 18, bottom: 26, left: 40 },
    tooltip: { trigger: 'axis' as const },
    xAxis: {
      type: 'category' as const,
      axisLine: { lineStyle: { color: '#d8e1eb' } },
      axisLabel: { color: '#6f7d90' },
      data: summary.value.approvalTrend.map((item) => item.label),
    },
    yAxis: {
      type: 'value' as const,
      splitLine: { lineStyle: { color: '#eef3f8' } },
      axisLabel: { color: '#6f7d90' },
    },
    series: [
      {
        type: 'bar' as const,
        barWidth: 18,
        itemStyle: { borderRadius: [8, 8, 0, 0] },
        data: summary.value.approvalTrend.map((item) => item.value),
      },
    ],
  }
})

const categoryOption = computed<EChartsOption | null>(() => {
  if (!summary.value) return null

  return {
    color: chartPalette,
    tooltip: { trigger: 'item' as const },
    legend: {
      bottom: 0,
      icon: 'circle',
      textStyle: { color: '#6f7d90' },
    },
    series: [
      {
        type: 'pie' as const,
        radius: ['46%', '72%'],
        center: ['50%', '44%'],
        label: { color: '#38506b', formatter: '{b}\n{d}%' },
        data: summary.value.clubCategoryDistribution.map((item) => ({
          name: item.name,
          value: item.value,
        })),
      },
    ],
  }
})

const completionOption = computed<EChartsOption | null>(() => {
  if (!summary.value) return null

  return {
    color: ['#70a1d7', '#f0b35a', '#7bc8a4', '#ec7b65'],
    tooltip: { trigger: 'item' as const },
    legend: {
      bottom: 0,
      icon: 'roundRect',
      textStyle: { color: '#6f7d90' },
    },
    series: [
      {
        type: 'pie' as const,
        radius: ['34%', '68%'],
        center: ['50%', '44%'],
        label: { color: '#38506b' },
        data: summary.value.completionOverview.map((item) => ({
          name: item.name,
          value: item.value,
        })),
      },
    ],
  }
})

function todoToneLabel(tone: string) {
  switch (tone) {
    case 'danger':
      return '需要优先处理'
    case 'warning':
      return '待审核'
    case 'success':
      return '待复盘'
    default:
      return '处理中'
  }
}

async function loadManagerOverview() {
  summary.value = await getDashboardSummary()
}

async function loadStudentHome() {
  const [postRes, activityRes, clubRes] = await Promise.all([getForumPosts(), getActivities(), getClubs()])
  if (postRes.code === 0) posts.value = postRes.data.slice(0, 4)
  if (activityRes.code === 0) activities.value = activityRes.data.slice(0, 5)
  if (clubRes.code === 0) clubs.value = clubRes.data.filter((club) => club.joined || club.canEnterSpace).slice(0, 5)
}

async function signup(activityId: string) {
  try {
    const result = await signupActivity(activityId)
    if (result.code !== 0) throw new Error(result.message)
    ElMessage.success(result.message || '报名成功')
    await loadStudentHome()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '报名失败')
  }
}

onMounted(async () => {
  try {
    if (isManager.value) {
      await loadManagerOverview()
    } else {
      await loadStudentHome()
    }
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '首页加载失败')
  }
})
</script>

<style scoped>
.dashboard-page {
  display: grid;
  gap: 20px;
}

.dashboard-hero {
  display: grid;
  grid-template-columns: minmax(0, 0.9fr) minmax(360px, 1.1fr);
  gap: 18px;
  padding: 28px;
  border: 1px solid rgba(206, 218, 230, 0.92);
  border-radius: 24px;
  background:
    radial-gradient(circle at top left, rgba(123, 200, 164, 0.18), transparent 26%),
    radial-gradient(circle at top right, rgba(91, 143, 249, 0.18), transparent 28%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(244, 248, 252, 0.96));
  box-shadow: 0 24px 48px rgba(20, 51, 93, 0.08);
}

.dashboard-hero__content {
  display: grid;
  gap: 14px;
}

.dashboard-hero__eyebrow {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #5279c3;
}

.dashboard-hero__content h2 {
  margin: 0;
  font-size: clamp(34px, 5vw, 52px);
  line-height: 1.02;
  color: #14335d;
}

.dashboard-hero__content p {
  margin: 0;
  max-width: 56ch;
  line-height: 1.8;
  color: #59697a;
}

.dashboard-hero__actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.dashboard-action {
  display: grid;
  gap: 8px;
  padding: 18px;
  border: 1px solid rgba(210, 220, 230, 0.9);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
  text-align: left;
  cursor: pointer;
  transition: transform 140ms ease, border-color 140ms ease, box-shadow 140ms ease;
}

.dashboard-action:hover {
  transform: translateY(-2px);
  border-color: rgba(91, 143, 249, 0.55);
  box-shadow: 0 18px 32px rgba(91, 143, 249, 0.12);
}

.dashboard-action strong {
  color: #183962;
}

.dashboard-action span {
  color: #667789;
  line-height: 1.7;
}

.dashboard-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(320px, 0.75fr);
  gap: 18px;
}

.dashboard-grid--secondary {
  grid-template-columns: minmax(340px, 0.85fr) minmax(0, 1.15fr);
}

.dashboard-panel {
  display: grid;
  gap: 18px;
  min-height: 320px;
  padding: 22px;
  border: 1px solid rgba(207, 218, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 20px 40px rgba(20, 51, 93, 0.08);
}

.dashboard-panel--chart {
  min-height: 360px;
}

.dashboard-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 14px;
}

.dashboard-panel__header h3 {
  margin: 0 0 8px;
  font-size: 22px;
  color: #17355f;
}

.dashboard-panel__header span {
  color: #6a7888;
}

.dashboard-rank {
  display: grid;
  gap: 12px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.dashboard-rank li,
.dashboard-todo__item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 18px;
  border: 1px solid rgba(217, 225, 234, 0.95);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
}

.dashboard-rank strong,
.dashboard-todo__item strong {
  display: block;
  margin-bottom: 4px;
  color: #183962;
}

.dashboard-rank span,
.dashboard-todo__item span {
  color: #728091;
}

.dashboard-rank b,
.dashboard-todo__item b {
  font-size: 28px;
  color: #183962;
}

.dashboard-todo {
  display: grid;
  gap: 12px;
}

.dashboard-todo__item {
  width: 100%;
  text-align: left;
  cursor: pointer;
}

.page-title--section {
  font-size: 32px;
  margin-bottom: 18px;
}

.student-home__hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.student-feed {
  display: grid;
  gap: 16px;
}

.student-feed__card img {
  display: block;
  width: 100%;
  height: 220px;
  object-fit: cover;
}

@media (max-width: 1180px) {
  .dashboard-hero,
  .dashboard-grid,
  .dashboard-grid--secondary,
  .dashboard-metrics {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .dashboard-hero {
    padding: 22px;
  }

  .dashboard-hero__actions {
    grid-template-columns: 1fr;
  }

  .student-home__hero {
    flex-direction: column;
    align-items: start;
  }
}
</style>
