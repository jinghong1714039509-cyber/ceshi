<template>
  <section class="activity-filters-card">
    <div class="activity-filters-card__main">
      <el-input v-model="localFilters.keyword" clearable placeholder="搜索活动、社团、地点" @input="emitFilters" />
      <el-select v-model="localFilters.clubId" clearable placeholder="社团筛选" @change="emitFilters">
        <el-option v-for="club in clubs" :key="club.id" :label="club.name" :value="club.id" />
      </el-select>
      <el-select v-model="localFilters.status" placeholder="结束状态" @change="emitFilters">
        <el-option label="全部状态" value="all" />
        <el-option label="进行中" value="0" />
        <el-option label="待教师确认" value="1" />
        <el-option label="已确认" value="2" />
        <el-option label="需补充" value="3" />
      </el-select>
      <el-select v-model="localFilters.signup" placeholder="参与范围" @change="emitFilters">
        <el-option label="全部活动" value="all" />
        <el-option label="我已报名" value="joined" />
        <el-option label="未报名" value="not-joined" />
        <el-option label="我可管理" value="manageable" />
      </el-select>
    </div>

    <div class="activity-filters-card__toolbar">
      <el-segmented v-model="localViewMode" :options="viewModeOptions" @change="emitViewMode" />

      <el-select v-model="localSortKey" placeholder="排序字段" @change="emitSort">
        <el-option label="活动时间" value="activeTime" />
        <el-option label="报名人数" value="total" />
        <el-option label="人数上限" value="capacity" />
        <el-option label="活动名称" value="name" />
      </el-select>

      <el-select v-model="localSortOrder" placeholder="排序方式" @change="emitSort">
        <el-option label="默认" :value="null" />
        <el-option label="升序" value="ascending" />
        <el-option label="降序" value="descending" />
      </el-select>

      <el-popover placement="bottom-end" :width="220" trigger="click">
        <template #reference>
          <el-button plain>列设置</el-button>
        </template>
        <div class="activity-filters-card__columns">
          <el-checkbox
            v-for="column in columnOptions"
            :key="column.key"
            :model-value="columns[column.key]"
            @change="(value) => emitColumn(column.key, value)"
          >
            {{ column.label }}
          </el-checkbox>
        </div>
      </el-popover>

      <el-button plain @click="$emit('reset')">重置</el-button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type {
  ActivityColumnState,
  ActivityFilters,
  ActivitySortKey,
  ActivitySortOrder,
  ActivityViewMode,
} from '../../../types/activity-view'
import type { ClubSummary } from '../../../types/club'

const props = defineProps<{
  filters: ActivityFilters
  clubs: ClubSummary[]
  columns: ActivityColumnState
  viewMode: ActivityViewMode
  sortKey: ActivitySortKey
  sortOrder: ActivitySortOrder
}>()

const emit = defineEmits<{
  'update:filters': [ActivityFilters]
  'update:viewMode': [ActivityViewMode]
  'update:sort': [{ key: ActivitySortKey; order: ActivitySortOrder }]
  'update:column': [{ key: keyof ActivityColumnState; value: boolean }]
  reset: []
}>()

const localFilters = reactive({ ...props.filters })
const localViewMode = ref<ActivityViewMode>(props.viewMode)
const localSortKey = ref<ActivitySortKey>(props.sortKey)
const localSortOrder = ref<ActivitySortOrder>(props.sortOrder)

const viewModeOptions = [
  { label: '表格', value: 'table' },
  { label: '卡片', value: 'card' },
]

const columnOptions: Array<{ key: keyof ActivityColumnState; label: string }> = [
  { key: 'clubName', label: '所属社团' },
  { key: 'activeTime', label: '活动时间' },
  { key: 'location', label: '活动地点' },
  { key: 'capacity', label: '人数上限' },
  { key: 'total', label: '报名人数' },
  { key: 'completionStatus', label: '结束状态' },
]

watch(() => props.filters, (value) => Object.assign(localFilters, value), { deep: true })
watch(() => props.viewMode, (value) => { localViewMode.value = value })
watch(() => [props.sortKey, props.sortOrder] as const, ([key, order]) => {
  localSortKey.value = key
  localSortOrder.value = order
})

function emitFilters() {
  emit('update:filters', { ...localFilters })
}

function emitViewMode() {
  emit('update:viewMode', localViewMode.value)
}

function emitSort() {
  emit('update:sort', { key: localSortKey.value, order: localSortOrder.value })
}

function emitColumn(key: keyof ActivityColumnState, value: string | number | boolean) {
  emit('update:column', { key, value: Boolean(value) })
}
</script>

<style scoped>
.activity-filters-card {
  display: grid;
  gap: 16px;
  padding: 20px;
  border: 1px solid rgba(208, 219, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 18px 36px rgba(20, 51, 93, 0.07);
}

.activity-filters-card__main,
.activity-filters-card__toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.activity-filters-card__main > * {
  flex: 1 1 200px;
}

.activity-filters-card__toolbar {
  align-items: center;
  justify-content: space-between;
}

.activity-filters-card__columns {
  display: grid;
  gap: 10px;
}

@media (max-width: 900px) {
  .activity-filters-card__toolbar {
    justify-content: flex-start;
  }
}
</style>
