<template>
  <div class="page-stack">
    <section class="metrics-grid">
      <article class="metric-card">
        <span class="metric-card__label">社团总数</span>
        <strong>{{ clubs.length }}</strong>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">已加入</span>
        <strong>{{ joinedCount }}</strong>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">待审核</span>
        <strong>{{ pendingCount }}</strong>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">可管理</span>
        <strong>{{ manageableCount }}</strong>
      </article>
    </section>

    <section class="section-panel">
      <div class="club-filters">
        <el-input v-model="keyword" clearable placeholder="搜索社团、类别、负责人" />
        <el-select v-model="membership" placeholder="成员状态">
          <el-option label="全部" value="all" />
          <el-option label="我已加入" value="joined" />
          <el-option label="待审核" value="pending" />
          <el-option label="我可管理" value="manageable" />
          <el-option label="可进入空间" value="space" />
        </el-select>
      </div>

      <div class="club-grid">
        <article v-for="club in filteredClubs" :key="club.id" class="paper-list-card club-card">
          <div class="paper-list-card__body">
            <div class="inline-meta">
              <span class="meta-pill">{{ club.category || '未分类' }}</span>
              <span class="meta-pill">{{ club.totalMembers }} 人</span>
            </div>
            <h2 class="paper-list-card__title">{{ club.name }}</h2>
            <p class="paper-list-card__text">负责人：{{ club.managerName || '未设置' }}</p>
            <p class="paper-list-card__text">创建时间：{{ club.createdAt || '暂无记录' }}</p>

            <div class="inline-meta">
              <span v-if="club.joined" class="meta-pill">已加入</span>
              <span v-if="club.pendingApproval" class="meta-pill">待审核</span>
              <span v-if="club.canManage" class="meta-pill">可管理</span>
              <span v-if="club.canEnterSpace" class="meta-pill">可进入空间</span>
            </div>

            <div class="card-footer">
              <div class="toolbar">
                <el-button text @click="router.push(`/clubs/${club.id}`)">查看详情</el-button>
                <el-button v-if="club.canEnterSpace" plain @click="router.push(`/clubs/${club.id}/space`)">社团空间</el-button>
              </div>
              <div class="toolbar">
                <el-tag v-if="club.joined" type="success">已是成员</el-tag>
                <el-tag v-else-if="club.pendingApproval" type="warning">等待审批</el-tag>
                <el-button v-else type="primary" @click="apply(club.id)">申请加入</el-button>
              </div>
            </div>
          </div>
        </article>
      </div>

      <div v-if="!filteredClubs.length" class="empty-state">当前筛选条件下没有匹配的社团。</div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getClubs, joinClub, type ClubSummary } from '../api/club'

const router = useRouter()
const clubs = ref<ClubSummary[]>([])
const keyword = ref('')
const membership = ref<'all' | 'joined' | 'pending' | 'manageable' | 'space'>('all')

const joinedCount = computed(() => clubs.value.filter((club) => club.joined).length)
const pendingCount = computed(() => clubs.value.filter((club) => club.pendingApproval).length)
const manageableCount = computed(() => clubs.value.filter((club) => club.canManage).length)
const filteredClubs = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  return clubs.value.filter((club) => {
    const matchesKeyword = !value || [
      club.name,
      club.category,
      club.managerName,
    ].some((item) => item?.toLowerCase().includes(value))

    const matchesMembership = (() => {
      switch (membership.value) {
        case 'joined':
          return club.joined
        case 'pending':
          return club.pendingApproval
        case 'manageable':
          return club.canManage
        case 'space':
          return club.canEnterSpace
        default:
          return true
      }
    })()

    return matchesKeyword && matchesMembership
  })
})

async function loadClubs() {
  try {
    const result = await getClubs()
    if (result.code !== 0) throw new Error(result.msg)
    clubs.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载社团失败')
  }
}

async function apply(clubId: string) {
  try {
    const result = await joinClub(clubId)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '申请成功')
    await loadClubs()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '申请失败')
  }
}

onMounted(() => {
  void loadClubs()
})
</script>

<style scoped>
.club-filters {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) 240px;
  gap: 12px;
  margin-bottom: 18px;
}

.club-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.club-card {
  height: 100%;
}

@media (max-width: 760px) {
  .club-filters {
    grid-template-columns: 1fr;
  }
}
</style>
