<template>
  <div class="page-stack" v-if="club">
    <section class="page-header">
      <div>
        <p class="page-eyebrow">Club Detail</p>
        <h1 class="page-title">{{ club.name }}</h1>
        <p class="page-subtitle">{{ club.category }} · 负责人 {{ club.managerName }} · {{ club.totalMembers }} 位成员</p>
      </div>
      <div class="page-actions">
        <el-button v-if="club.canEnterSpace" type="primary" @click="router.push(`/clubs/${club.id}/space`)">进入空间</el-button>
      </div>
    </section>

    <section class="grid-two">
      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Activities</p>
            <h3>社团活动</h3>
            <span>点击活动卡即可进入具体活动内容页。</span>
          </div>
        </div>
        <div class="split-list" style="margin-top: 18px;">
          <div v-for="activity in club.activities" :key="activity.id" class="club-activity-item" @click="router.push(`/activities/${activity.id}`)">
            <img :src="coverFor(activity)" :alt="activity.name" />
            <div>
              <strong>{{ activity.name }}</strong>
              <span>{{ activity.activeTime }} · {{ activity.location }}</span>
            </div>
          </div>
        </div>
      </article>

      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Notices</p>
            <h3>社团公告</h3>
          </div>
        </div>
        <div class="split-list" style="margin-top: 18px;">
          <div v-for="notice in club.notices" :key="notice.id" class="split-list__item">
            <div>
              <strong>{{ notice.title }}</strong>
              <span>{{ notice.detail }}</span>
            </div>
            <span class="muted-text">{{ notice.createTime }}</span>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getClubDetail, type ClubDetail } from '../api/club'
import type { ActivitySummary } from '../api/activity'
import { pickDefaultCover } from '../content/default-covers'

const route = useRoute()
const router = useRouter()
const club = ref<ClubDetail | null>(null)

function coverFor(activity: ActivitySummary) {
  return activity.coverImage || pickDefaultCover(activity.id)
}

async function loadClub() {
  try {
    const result = await getClubDetail(String(route.params.clubId))
    if (result.code !== 0) throw new Error(result.msg)
    club.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载社团详情失败')
    router.push('/clubs')
  }
}

onMounted(() => {
  void loadClub()
})
</script>

<style scoped>
.club-activity-item {
  display: grid;
  grid-template-columns: 140px minmax(0, 1fr);
  gap: 16px;
  padding: 14px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.6);
  cursor: pointer;
}

.club-activity-item img {
  width: 100%;
  height: 110px;
  border-radius: 16px;
  object-fit: cover;
}

@media (max-width: 960px) {
  .club-activity-item {
    grid-template-columns: 1fr;
  }
}
</style>
