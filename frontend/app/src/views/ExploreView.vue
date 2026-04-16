<template>
  <div class="public-shell explore-shell">
    <section class="section-panel explore-hero">
      <div class="explore-hero__intro">
        <h1 class="page-title">先看活动，再决定是否加入</h1>
        <div class="page-actions">
          <template v-if="!session.isAuthenticated">
            <el-button type="primary" @click="router.push('/register')">注册账号</el-button>
            <el-button plain @click="router.push('/login')">登录查看详情</el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="router.push('/activities')">进入活动</el-button>
            <el-button plain @click="router.push('/forum')">进入论坛</el-button>
          </template>
        </div>
      </div>

      <div class="explore-hero__featured">
        <article
          v-for="activity in featuredActivities"
          :key="activity.id"
          class="paper-list-card explore-feature"
          @click="openActivity(activity.id)"
        >
          <img :src="coverFor(activity)" :alt="activity.name" />
          <div class="paper-list-card__body">
            <h2 class="paper-list-card__title">{{ activity.name }}</h2>
            <div class="inline-meta">
              <span class="meta-pill">{{ activity.clubName }}</span>
              <span class="meta-pill">{{ activity.location }}</span>
            </div>
          </div>
        </article>
      </div>
    </section>

    <section class="section-panel">
      <div class="explore-filters">
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
        <el-button plain @click="resetFilters">重置</el-button>
      </div>

      <div class="explore-grid">
        <article v-for="activity in filteredActivities" :key="activity.id" class="paper-list-card activity-card">
          <img :src="coverFor(activity)" :alt="activity.name" />
          <div class="paper-list-card__body">
            <div class="inline-meta">
              <span class="meta-pill">{{ activity.clubName }}</span>
              <span class="meta-pill">{{ completionLabel(activity.completionStatus) }}</span>
            </div>
            <h2 class="paper-list-card__title">{{ activity.name }}</h2>
            <p class="paper-list-card__text">{{ activity.summary }}</p>
            <div class="activity-card__meta">
              <span>{{ activity.activeTime }}</span>
              <span>{{ activity.location }}</span>
              <span>{{ activity.total }}/{{ activity.capacity || '不限' }} 人</span>
            </div>
            <div v-if="activity.completionStatus > 0" class="activity-card__completion">
              <strong>活动结束</strong>
              <span v-if="activity.completionSummary">{{ activity.completionSummary }}</span>
              <span v-if="activity.completionReviewComment">教师意见：{{ activity.completionReviewComment }}</span>
            </div>
            <div class="card-footer">
              <el-button text @click="openActivity(activity.id)">查看详情</el-button>
            </div>
          </div>
        </article>
      </div>

      <div v-if="!filteredActivities.length" class="empty-state">当前没有符合条件的公开活动。</div>
    </section>

    <section class="grid-two">
      <article class="section-panel">
        <h2 class="page-title page-title--section">论坛</h2>
        <div class="split-list explore-forum">
          <article v-for="post in forumPosts" :key="post.id" class="explore-forum__item">
            <img :src="post.coverImage || pickDefaultCover(post.id)" :alt="post.title" />
            <div>
              <strong>{{ post.title }}</strong>
              <span>{{ post.authorName }} · {{ post.createTime }}</span>
              <p>{{ post.content }}</p>
            </div>
          </article>
        </div>
        <div class="card-footer">
          <el-button plain @click="openForum">{{ session.isAuthenticated ? '进入论坛' : '登录后进入论坛' }}</el-button>
        </div>
      </article>

      <article class="section-panel">
        <h2 class="page-title page-title--section">社团</h2>
        <div class="split-list">
          <div v-for="club in clubs" :key="club.id" class="split-list__item">
            <div>
              <strong>{{ club.name }}</strong>
              <span>{{ club.category }} · {{ club.totalMembers }} 人</span>
            </div>
            <el-button text @click="openClub(club.id)">{{ session.isAuthenticated ? '查看社团' : '登录查看' }}</el-button>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { ActivitySummary } from '../api/activity'
import { type ClubSummary } from '../api/club'
import { type ForumPost } from '../api/forum'
import { getPublicActivities as getOpenActivities, getPublicClubs, getPublicForumPosts } from '../api/public'
import { pickDefaultCover } from '../content/default-covers'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const session = useSessionStore()
const clubs = ref<ClubSummary[]>([])
const activities = ref<ActivitySummary[]>([])
const forumPosts = ref<ForumPost[]>([])

const filters = reactive({
  keyword: '',
  clubId: '',
  status: 'all',
})

const featuredActivities = computed(() => activities.value.slice(0, 3))
const clubOptions = computed(() => clubs.value)
const filteredActivities = computed(() => {
  const keyword = filters.keyword.trim().toLowerCase()
  return activities.value.filter((activity) => {
    const matchesKeyword = !keyword || [
      activity.name,
      activity.location,
      activity.clubName,
      activity.summary,
    ].some((value) => value?.toLowerCase().includes(keyword))

    const matchesClub = !filters.clubId || activity.clubId === filters.clubId
    const matchesStatus = filters.status === 'all' || String(activity.completionStatus) === filters.status
    return matchesKeyword && matchesClub && matchesStatus
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
}

function openActivity(activityId: string) {
  if (session.isAuthenticated) {
    router.push(`/activities/${activityId}`)
    return
  }
  ElMessage.info('登录后可查看具体活动内容')
  router.push('/login')
}

function openForum() {
  router.push(session.isAuthenticated ? '/forum' : '/login')
}

function openClub(clubId: string) {
  if (session.isAuthenticated) {
    router.push(`/clubs/${clubId}`)
    return
  }
  router.push('/login')
}

async function loadData() {
  try {
    const [clubRes, activityRes, forumRes] = await Promise.all([
      getPublicClubs(),
      getOpenActivities(),
      getPublicForumPosts(),
    ])

    if (clubRes.code !== 0) throw new Error(clubRes.msg)
    if (activityRes.code !== 0) throw new Error(activityRes.msg)
    if (forumRes.code !== 0) throw new Error(forumRes.msg)

    clubs.value = clubRes.data.slice(0, 6)
    activities.value = activityRes.data
    forumPosts.value = forumRes.data.slice(0, 4)
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '公开内容加载失败')
  }
}

onMounted(() => {
  void loadData()
})
</script>

<style scoped>
.explore-shell {
  display: grid;
  gap: 18px;
  padding: 24px;
}

.explore-hero {
  display: grid;
  grid-template-columns: minmax(0, 0.9fr) minmax(0, 1.1fr);
  gap: 18px;
}

.explore-hero__intro {
  display: grid;
  align-content: space-between;
  gap: 24px;
  min-height: 360px;
}

.explore-hero__featured {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.explore-feature {
  cursor: pointer;
}

.explore-feature img,
.activity-card img,
.explore-forum__item img {
  display: block;
  width: 100%;
  object-fit: cover;
}

.explore-feature img,
.activity-card img {
  height: 220px;
}

.explore-filters {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr auto;
  gap: 12px;
  margin-bottom: 18px;
}

.explore-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.activity-card__meta,
.activity-card__completion {
  display: grid;
  gap: 6px;
  color: var(--ink-soft);
}

.activity-card__completion strong {
  color: var(--ink);
}

.page-title--section {
  font-size: 34px;
  margin-bottom: 18px;
}

.explore-forum__item {
  display: grid;
  grid-template-columns: 132px minmax(0, 1fr);
  gap: 14px;
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(255, 253, 249, 0.82);
}

.explore-forum__item img {
  height: 108px;
  border-radius: 10px;
}

.explore-forum__item strong,
.explore-forum__item span,
.explore-forum__item p {
  display: block;
}

.explore-forum__item span,
.explore-forum__item p {
  color: var(--ink-soft);
}

@media (max-width: 1180px) {
  .explore-hero,
  .explore-hero__featured,
  .explore-filters {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .explore-shell {
    padding: 16px;
  }

  .explore-forum__item {
    grid-template-columns: 1fr;
  }
}
</style>
