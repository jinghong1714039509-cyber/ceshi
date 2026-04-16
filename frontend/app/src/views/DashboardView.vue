<template>
  <div class="page-stack">
    <template v-if="isManager">
      <section class="metrics-grid">
        <article v-for="card in managerCards" :key="card.label" class="metric-card">
          <span class="metric-card__label">{{ card.label }}</span>
          <strong>{{ card.value }}</strong>
          <span class="stat-note">{{ card.tip }}</span>
        </article>
      </section>

      <section class="grid-two">
        <article class="section-panel">
          <h2 class="page-title page-title--section">我管理的社团</h2>
          <div class="card-grid">
            <article v-for="club in managedClubs" :key="club.id" class="paper-list-card manager-club-card">
              <div class="paper-list-card__body">
                <h3 class="paper-list-card__title">{{ club.name }}</h3>
                <div class="inline-meta">
                  <span class="meta-pill">{{ club.category || '未分类' }}</span>
                  <span class="meta-pill">{{ club.totalMembers }} 人</span>
                </div>
                <p class="paper-list-card__text">负责人：{{ club.managerName || session.displayName }}</p>
                <div class="card-footer">
                  <el-button text @click="router.push(`/clubs/${club.id}`)">查看社团</el-button>
                  <el-button plain @click="router.push(`/clubs/${club.id}/space`)">社团空间</el-button>
                </div>
              </div>
            </article>
          </div>
        </article>

        <article class="section-panel">
          <h2 class="page-title page-title--section">待处理</h2>
          <div class="split-list">
            <div class="split-list__item">
              <div>
                <strong>待审核入团申请</strong>
                <span>{{ pendingApprovals }} 条</span>
              </div>
              <el-tag type="warning">{{ pendingApprovals }}</el-tag>
            </div>
            <div class="split-list__item">
              <div>
                <strong>未读私信</strong>
                <span>{{ unreadMessages }} 条</span>
              </div>
              <el-tag type="danger">{{ unreadMessages }}</el-tag>
            </div>
            <div class="split-list__item">
              <div>
                <strong>近期活动</strong>
                <span>{{ upcomingManagedActivities }} 场</span>
              </div>
              <el-tag>{{ upcomingManagedActivities }}</el-tag>
            </div>
          </div>
          <div class="card-footer">
            <el-button plain @click="router.push('/join-applications')">查看审核</el-button>
            <el-button type="primary" @click="router.push('/messages')">进入消息</el-button>
          </div>
        </article>
      </section>
    </template>

    <template v-else>
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
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActivities, signupActivity, type ActivitySummary } from '../api/activity'
import { getClubs, getManagedApplications, type ClubSummary, type JoinApplication } from '../api/club'
import { getForumPosts, type ForumPost } from '../api/forum'
import { getMessageContacts, type MessageContact } from '../api/messages'
import { pickDefaultCover } from '../content/default-covers'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const session = useSessionStore()
const posts = ref<ForumPost[]>([])
const activities = ref<ActivitySummary[]>([])
const clubs = ref<ClubSummary[]>([])
const managedApplications = ref<JoinApplication[]>([])
const messageContacts = ref<MessageContact[]>([])

const isManager = computed(() => ['super-admin', 'club-admin', 'teacher-admin'].includes(session.role))
const managedClubs = computed(() => clubs.value.filter((club) => club.canManage))
const pendingApprovals = computed(() => managedApplications.value.filter((item) => item.status === 0).length)
const unreadMessages = computed(() => messageContacts.value.reduce((sum, item) => sum + item.unreadCount, 0))
const upcomingManagedActivities = computed(() => activities.value.filter((item) => item.manageable).length)
const managerCards = computed(() => [
  { label: '管理社团', value: String(managedClubs.value.length), tip: '当前由你负责的社团数量' },
  { label: '待审核申请', value: String(pendingApprovals.value), tip: '等待处理的入团申请' },
  { label: '未读私信', value: String(unreadMessages.value), tip: '来自成员的未读消息' },
  { label: '近期活动', value: String(upcomingManagedActivities.value), tip: '你所管理社团的活动数量' },
])

async function loadManagerOverview() {
  const [clubRes, activityRes, applicationRes, contactRes] = await Promise.all([
    getClubs(),
    getActivities(),
    getManagedApplications(),
    getMessageContacts(),
  ])
  if (clubRes.code === 0) clubs.value = clubRes.data
  if (activityRes.code === 0) activities.value = activityRes.data
  if (applicationRes.code === 0) managedApplications.value = applicationRes.data
  if (contactRes.code === 0) messageContacts.value = contactRes.data
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
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '报名成功')
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

.manager-club-card {
  height: 100%;
}

@media (max-width: 760px) {
  .student-home__hero {
    flex-direction: column;
    align-items: start;
  }
}
</style>
