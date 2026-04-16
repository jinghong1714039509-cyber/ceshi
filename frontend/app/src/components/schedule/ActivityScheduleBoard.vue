<template>
  <section class="schedule-board">
    <div class="schedule-board__header">
      <div>
        <strong>活动生命周期</strong>
        <span>以周视图模拟甘特排期，展示筹备、宣传、执行与复盘阶段。</span>
      </div>
      <div class="schedule-board__legend">
        <span data-stage="preparing">筹备</span>
        <span data-stage="promotion">宣传</span>
        <span data-stage="running">执行中</span>
        <span data-stage="review">复盘</span>
      </div>
    </div>

    <div class="schedule-board__table">
      <div class="schedule-board__grid schedule-board__grid--head">
        <div class="schedule-board__activity-col">活动</div>
        <div v-for="day in days" :key="day.key" class="schedule-board__day">{{ day.label }}</div>
      </div>

      <button
        v-for="block in blocks"
        :key="block.id"
        type="button"
        class="schedule-board__grid schedule-board__grid--row"
        @click="$emit('open-detail', block)"
      >
        <div class="schedule-board__activity-col schedule-board__activity-col--body">
          <strong>{{ block.name }}</strong>
          <span>{{ block.clubName }}</span>
        </div>
        <div class="schedule-board__lane">
          <span
            class="schedule-board__bar"
            :data-stage="block.stage"
            :style="{ gridColumn: `${block.stageOffset + 1} / span ${block.stageSpan}` }"
          >
            {{ block.stageLabel }}
          </span>
        </div>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { ActivityLifecycleBlock } from '../../types/activity-view'

defineProps<{
  blocks: ActivityLifecycleBlock[]
  days: Array<{ key: string; label: string }>
}>()

defineEmits<{
  'open-detail': [ActivityLifecycleBlock]
}>()
</script>

<style scoped>
.schedule-board {
  display: grid;
  gap: 18px;
  padding: 22px;
  border: 1px solid rgba(208, 219, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 18px 36px rgba(20, 51, 93, 0.07);
}

.schedule-board__header,
.schedule-board__legend {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.schedule-board__header strong {
  display: block;
  margin-bottom: 6px;
  color: #183962;
}

.schedule-board__header span,
.schedule-board__legend span,
.schedule-board__activity-col span {
  color: #6f7d8c;
}

.schedule-board__legend span {
  padding: 6px 10px;
  border-radius: 999px;
  background: #f4f7fb;
}

.schedule-board__table {
  overflow-x: auto;
}

.schedule-board__grid {
  display: grid;
  grid-template-columns: 220px repeat(28, minmax(28px, 1fr));
  gap: 8px;
  min-width: 1180px;
  align-items: center;
}

.schedule-board__grid--head {
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(220, 228, 235, 0.92);
}

.schedule-board__grid--row {
  width: 100%;
  margin-top: 12px;
  padding: 0;
  border: 0;
  background: transparent;
  text-align: left;
  cursor: pointer;
}

.schedule-board__activity-col {
  font-size: 13px;
  color: #6f7d8c;
}

.schedule-board__activity-col--body {
  display: grid;
  gap: 4px;
}

.schedule-board__activity-col--body strong {
  color: #183962;
}

.schedule-board__lane {
  display: grid;
  grid-template-columns: repeat(28, minmax(28px, 1fr));
  gap: 8px;
  padding: 10px 0;
}

.schedule-board__bar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 36px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.schedule-board__bar[data-stage='preparing'] {
  background: linear-gradient(90deg, #75a8ff, #5b8ff9);
}

.schedule-board__bar[data-stage='promotion'] {
  background: linear-gradient(90deg, #8ecf88, #67b16b);
}

.schedule-board__bar[data-stage='running'] {
  background: linear-gradient(90deg, #f2bd67, #e7a54c);
}

.schedule-board__bar[data-stage='review'] {
  background: linear-gradient(90deg, #eb8b76, #d96d5f);
}

.schedule-board__day {
  text-align: center;
  color: #6f7d8c;
  font-size: 12px;
}
</style>
