<template>
  <section class="activity-card-grid">
    <article v-for="activity in activities" :key="activity.id" class="activity-card">
      <img :src="coverFor(activity)" :alt="activity.name" />
      <div class="activity-card__body">
        <div class="activity-card__meta">
          <span class="meta-pill">{{ activity.clubName }}</span>
          <span class="meta-pill">{{ getLifecycleStageLabel(getLifecycleStage(activity)) }}</span>
        </div>
        <h3>{{ activity.name }}</h3>
        <p>{{ activity.summary }}</p>
        <div class="activity-card__stats">
          <span>{{ activity.activeTime }}</span>
          <span>{{ activity.location }}</span>
          <span>{{ activity.total }}/{{ activity.capacity || '不限' }} 人</span>
        </div>
        <div class="activity-card__actions">
          <el-button link type="primary" @click="$emit('open-drawer', activity)">详情</el-button>
          <el-button link @click="$emit('open-detail', activity.id)">页面</el-button>
          <el-button v-if="activity.manageable" link @click="$emit('open-signups', activity)">名单</el-button>
          <el-button v-if="!activity.joined" type="primary" plain @click="$emit('signup', activity.id)">报名</el-button>
          <el-button v-else-if="!activity.manageable" type="danger" plain @click="$emit('cancel-signup', activity.id)">取消报名</el-button>
        </div>
      </div>
    </article>
  </section>
</template>

<script setup lang="ts">
import type { ActivitySummary } from '../../../types/activity'
import { getLifecycleStage, getLifecycleStageLabel } from '../../../types/activity-view'
import { pickDefaultCover } from '../../../content/default-covers'

defineProps<{ activities: ActivitySummary[] }>()

defineEmits<{
  'open-drawer': [ActivitySummary]
  'open-detail': [string]
  'open-signups': [ActivitySummary]
  signup: [string]
  'cancel-signup': [string]
}>()

function coverFor(activity: ActivitySummary) {
  return activity.coverImage || pickDefaultCover(activity.id)
}
</script>

<style scoped>
.activity-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.activity-card {
  overflow: hidden;
  border: 1px solid rgba(208, 219, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 18px 36px rgba(20, 51, 93, 0.07);
}

.activity-card img {
  display: block;
  width: 100%;
  height: 216px;
  object-fit: cover;
}

.activity-card__body {
  display: grid;
  gap: 12px;
  padding: 18px;
}

.activity-card__meta,
.activity-card__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.activity-card h3,
.activity-card p {
  margin: 0;
}

.activity-card h3 {
  color: #183962;
}

.activity-card p,
.activity-card__stats span {
  color: #6f7d8c;
  line-height: 1.7;
}

.activity-card__stats {
  display: grid;
  gap: 6px;
}
</style>
