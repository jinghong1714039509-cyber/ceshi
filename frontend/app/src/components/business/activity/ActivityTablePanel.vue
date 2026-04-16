<template>
  <section class="activity-table-card table-panel">
    <div class="activity-table-card__header">
      <div class="activity-table-card__meta">
        <strong>活动列表</strong>
        <span>支持多字段筛选、批量勾选、固定列与列显示控制。</span>
      </div>
      <div class="activity-table-card__stats">
        <span>共 {{ activities.length }} 场</span>
        <span v-if="selection.length">已选 {{ selection.length }} 项</span>
      </div>
    </div>

    <div v-if="selection.length" class="activity-table-card__bulk">
      <span>批量操作</span>
      <el-button plain @click="$emit('bulk-open-signups')">批量查看名单</el-button>
      <el-button plain @click="$emit('clear-selection')">清空选择</el-button>
    </div>

    <el-table
      ref="tableRef"
      :data="activities"
      row-key="id"
      height="580"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
    >
      <el-table-column type="selection" width="50" fixed="left" />
      <el-table-column prop="name" label="活动名称" min-width="220" sortable="custom" fixed="left">
        <template #default="{ row }">
          <div class="activity-cell">
            <img :src="coverFor(row)" :alt="row.name" />
            <div>
              <strong>{{ row.name }}</strong>
              <span>{{ row.summary }}</span>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column v-if="columns.clubName" prop="clubName" label="所属社团" min-width="160" />
      <el-table-column v-if="columns.activeTime" prop="activeTime" label="活动时间" min-width="180" sortable="custom" />
      <el-table-column v-if="columns.location" prop="location" label="地点" min-width="160" />
      <el-table-column v-if="columns.capacity" prop="capacity" label="人数上限" width="110" sortable="custom" />
      <el-table-column v-if="columns.total" prop="total" label="报名人数" width="110" sortable="custom" />
      <el-table-column v-if="columns.completionStatus" label="结束状态" width="130">
        <template #default="{ row }">
          <el-tag :type="completionTagType(row.completionStatus)">{{ completionLabel(row.completionStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前阶段" width="110">
        <template #default="{ row }">
          <span>{{ getLifecycleStageLabel(getLifecycleStage(row)) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <div class="activity-table-card__actions">
            <el-button link type="primary" @click="$emit('open-drawer', row)">详情</el-button>
            <el-button link @click="$emit('open-detail', row.id)">页面</el-button>
            <el-button v-if="row.manageable" link @click="$emit('open-signups', row)">名单</el-button>
            <el-button v-if="!row.joined" link type="success" @click="$emit('signup', row.id)">报名</el-button>
            <el-button v-else-if="!row.manageable" link type="danger" @click="$emit('cancel-signup', row.id)">取消报名</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ActivitySummary } from '../../../types/activity'
import type { ActivityColumnState, ActivitySortOrder } from '../../../types/activity-view'
import { getLifecycleStage, getLifecycleStageLabel } from '../../../types/activity-view'
import { pickDefaultCover } from '../../../content/default-covers'

defineProps<{
  activities: ActivitySummary[]
  columns: ActivityColumnState
  selection: ActivitySummary[]
}>()

const emit = defineEmits<{
  'update:selection': [ActivitySummary[]]
  'sort-change': [{ prop: string; order: ActivitySortOrder }]
  'open-drawer': [ActivitySummary]
  'open-detail': [string]
  'open-signups': [ActivitySummary]
  signup: [string]
  'cancel-signup': [string]
  'bulk-open-signups': []
  'clear-selection': []
}>()

const tableRef = ref()

function handleSelectionChange(value: ActivitySummary[]) {
  emit('update:selection', value)
}

function handleSortChange(payload: { prop: string; order: ActivitySortOrder }) {
  emit('sort-change', payload)
}

function coverFor(activity: ActivitySummary) {
  return activity.coverImage || pickDefaultCover(activity.id)
}

function completionLabel(status: number) {
  switch (status) {
    case 1:
      return '待确认'
    case 2:
      return '已确认'
    case 3:
      return '需补充'
    default:
      return '进行中'
  }
}

function completionTagType(status: number) {
  switch (status) {
    case 1:
      return 'warning'
    case 2:
      return 'success'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

defineExpose({
  clearSelection() {
    tableRef.value?.clearSelection()
  },
})
</script>

<style scoped>
.activity-table-card {
  padding: 22px;
  border: 1px solid rgba(208, 219, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 18px 36px rgba(20, 51, 93, 0.07);
}

.activity-table-card__header,
.activity-table-card__bulk {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.activity-table-card__meta,
.activity-table-card__stats {
  display: grid;
  gap: 6px;
}

.activity-table-card__meta strong {
  color: #183962;
}

.activity-table-card__meta span,
.activity-table-card__stats span {
  color: #6f7d8c;
}

.activity-table-card__bulk {
  margin: 16px 0;
  padding: 12px 14px;
  border: 1px dashed rgba(150, 170, 190, 0.9);
  border-radius: 16px;
  background: rgba(247, 250, 253, 0.96);
}

.activity-cell {
  display: grid;
  grid-template-columns: 58px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
}

.activity-cell img {
  width: 58px;
  height: 58px;
  border-radius: 12px;
  object-fit: cover;
}

.activity-cell strong,
.activity-cell span {
  display: block;
}

.activity-cell strong {
  margin-bottom: 4px;
  color: #183962;
}

.activity-cell span {
  color: #6f7d8c;
  line-height: 1.5;
}

.activity-table-card__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>
