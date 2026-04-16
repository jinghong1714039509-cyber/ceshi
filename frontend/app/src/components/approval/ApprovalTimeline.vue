<template>
  <div class="approval-timeline">
    <div v-for="node in nodes" :key="node.id" class="approval-timeline__item" :data-status="node.status">
      <div class="approval-timeline__dot"></div>
      <div class="approval-timeline__content">
        <div class="approval-timeline__head">
          <strong>{{ node.nodeName }}</strong>
          <span>{{ node.actionTime || '待处理' }}</span>
        </div>
        <div class="approval-timeline__meta">
          <span>{{ node.approverName }}</span>
          <em>{{ statusLabel(node.status) }}</em>
        </div>
        <p v-if="node.comment">{{ node.comment }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ApprovalNode } from '../../types/approval'

defineProps<{
  nodes: ApprovalNode[]
}>()

function statusLabel(status: ApprovalNode['status']) {
  switch (status) {
    case 'approved':
      return '已通过'
    case 'rejected':
      return '已驳回'
    case 'supplement':
      return '待补充'
    default:
      return '处理中'
  }
}
</script>

<style scoped>
.approval-timeline {
  display: grid;
  gap: 14px;
}

.approval-timeline__item {
  display: grid;
  grid-template-columns: 22px minmax(0, 1fr);
  gap: 12px;
}

.approval-timeline__dot {
  position: relative;
  width: 12px;
  height: 12px;
  margin-top: 6px;
  border-radius: 999px;
  background: #c9d4e0;
}

.approval-timeline__item:not(:last-child) .approval-timeline__dot::after {
  content: '';
  position: absolute;
  top: 14px;
  left: 50%;
  width: 2px;
  height: calc(100% + 18px);
  transform: translateX(-50%);
  background: #dbe4ec;
}

.approval-timeline__item[data-status='approved'] .approval-timeline__dot {
  background: #6fc19b;
}

.approval-timeline__item[data-status='supplement'] .approval-timeline__dot {
  background: #f2b55b;
}

.approval-timeline__item[data-status='rejected'] .approval-timeline__dot {
  background: #e97a69;
}

.approval-timeline__content {
  display: grid;
  gap: 6px;
  padding: 14px 16px;
  border: 1px solid rgba(214, 224, 233, 0.95);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.94);
}

.approval-timeline__head,
.approval-timeline__meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.approval-timeline__head strong {
  color: #183962;
}

.approval-timeline__head span,
.approval-timeline__meta span,
.approval-timeline__meta em,
.approval-timeline__content p {
  color: #6f7d8c;
}

.approval-timeline__content p {
  margin: 0;
  line-height: 1.7;
}
</style>
