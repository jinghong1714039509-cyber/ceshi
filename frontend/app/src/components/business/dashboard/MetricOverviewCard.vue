<template>
  <article class="metric-overview-card" :data-tone="card.tone">
    <span class="metric-overview-card__label">{{ card.label }}</span>
    <strong>{{ card.value }}</strong>
    <span class="metric-overview-card__delta">{{ card.delta }}</span>
  </article>
</template>

<script setup lang="ts">
import type { DashboardMetricsCard } from '../../../types/dashboard'

defineProps<{
  card: DashboardMetricsCard
}>()
</script>

<style scoped>
.metric-overview-card {
  display: grid;
  gap: 10px;
  padding: 20px;
  border: 1px solid rgba(207, 218, 228, 0.9);
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.8), transparent 34%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.96));
  box-shadow: 0 18px 36px rgba(22, 51, 84, 0.08);
}

.metric-overview-card__label,
.metric-overview-card__delta {
  color: #5e6b7a;
}

.metric-overview-card strong {
  font-size: 34px;
  line-height: 1;
  color: #14335d;
}

.metric-overview-card[data-tone='orange'] {
  border-color: rgba(244, 187, 122, 0.7);
}

.metric-overview-card[data-tone='green'] {
  border-color: rgba(132, 195, 162, 0.72);
}

.metric-overview-card[data-tone='blue'] {
  border-color: rgba(129, 170, 230, 0.7);
}

.metric-overview-card[data-tone='gold'] {
  border-color: rgba(232, 202, 128, 0.75);
}
</style>
