<template>
  <el-drawer v-model="model" :size="560" :with-header="false">
    <template v-if="activity">
      <div class="activity-drawer">
        <section class="activity-drawer__hero">
          <img :src="coverFor(activity)" :alt="activity.name" />
          <div>
            <span class="activity-drawer__eyebrow">Activity</span>
            <h3>{{ activity.name }}</h3>
            <div class="activity-drawer__tags">
              <el-tag>{{ activity.clubName }}</el-tag>
              <el-tag type="success">{{ getLifecycleStageLabel(getLifecycleStage(activity)) }}</el-tag>
            </div>
          </div>
        </section>

        <section class="activity-drawer__section">
          <h4>基础信息</h4>
          <div class="activity-drawer__grid">
            <div><span>活动时间</span><strong>{{ activity.activeTime }}</strong></div>
            <div><span>活动地点</span><strong>{{ activity.location }}</strong></div>
            <div><span>报名人数</span><strong>{{ activity.total }}/{{ activity.capacity || '不限' }}</strong></div>
            <div><span>结束状态</span><strong>{{ completionLabel(activity.completionStatus) }}</strong></div>
          </div>
        </section>

        <section class="activity-drawer__section">
          <h4>活动内容</h4>
          <p>{{ activity.detail }}</p>
        </section>

        <section class="activity-drawer__section">
          <h4>阶段安排</h4>
          <el-steps :active="stageSteps.findIndex((item) => item.active) + 1" finish-status="success" align-center>
            <el-step v-for="step in stageSteps" :key="step.label" :title="step.label" :description="step.description" />
          </el-steps>
        </section>

        <section class="activity-drawer__section">
          <h4>经费审批流转</h4>
          <div class="activity-drawer__timeline">
            <div v-for="item in fundTimeline" :key="item.label" class="activity-drawer__timeline-item">
              <strong>{{ item.label }}</strong>
              <span>{{ item.description }}</span>
            </div>
          </div>
        </section>

        <section class="activity-drawer__section">
          <h4>关联物资进度</h4>
          <div class="activity-drawer__asset-list">
            <div v-for="item in assetChecklist" :key="item.label">
              <strong>{{ item.label }}</strong>
              <span>{{ item.description }}</span>
            </div>
          </div>
        </section>

        <div class="activity-drawer__footer">
          <el-button @click="$emit('open-detail', activity.id)">进入详情页</el-button>
          <el-button v-if="activity.manageable" plain @click="$emit('open-signups', activity)">查看名单</el-button>
          <el-button v-if="!activity.joined" type="primary" @click="$emit('signup', activity.id)">报名活动</el-button>
          <el-button v-else-if="!activity.manageable" type="danger" plain @click="$emit('cancel-signup', activity.id)">取消报名</el-button>
        </div>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ActivitySummary } from '../../../types/activity'
import { getLifecycleStage, getLifecycleStageLabel } from '../../../types/activity-view'
import { pickDefaultCover } from '../../../content/default-covers'

const model = defineModel<boolean>({ required: true })
const props = defineProps<{ activity: ActivitySummary | null }>()

defineEmits<{
  'open-detail': [string]
  'open-signups': [ActivitySummary]
  signup: [string]
  'cancel-signup': [string]
}>()

const stageSteps = computed(() => {
  if (!props.activity) return []
  const stage = getLifecycleStage(props.activity)
  const order = ['preparing', 'promotion', 'running', 'review']
  return [
    { label: '筹备', description: '方案、场地和成员分工', active: order.indexOf(stage) >= 0 },
    { label: '宣传', description: '海报、论坛与报名入口曝光', active: order.indexOf(stage) >= 1 },
    { label: '执行中', description: '活动现场与签到执行', active: order.indexOf(stage) >= 2 },
    { label: '复盘', description: '结束材料、照片与教师确认', active: order.indexOf(stage) >= 3 },
  ]
})

const fundTimeline = computed(() => {
  if (!props.activity) return []
  return [
    { label: '预算提交', description: props.activity.manageable ? '已在活动发起时提交预算单。' : '活动负责人已准备预算材料。' },
    { label: '指导教师确认', description: props.activity.completionStatus > 0 ? '教师已进入结束材料确认阶段。' : '待活动执行前确认预算合理性。' },
    { label: '经费结项', description: props.activity.completionStatus === 2 ? '活动已完成确认，可归档经费结果。' : '待活动完成后进入结项。' },
  ]
})

const assetChecklist = computed(() => {
  if (!props.activity) return []
  return [
    { label: '场地物料', description: props.activity.location ? `已锁定活动地点：${props.activity.location}` : '待补充活动地点。' },
    { label: '宣传物料', description: '可在宣传阶段准备横幅、海报和签到物资。' },
    { label: '现场设备', description: props.activity.manageable ? '建议活动前 2 天完成设备检查。' : '等待活动负责人确认设备清单。' },
  ]
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
</script>

<style scoped>
.activity-drawer {
  display: grid;
  gap: 18px;
  padding: 24px 24px 108px;
}

.activity-drawer__hero,
.activity-drawer__section {
  display: grid;
  gap: 14px;
  padding: 18px;
  border: 1px solid rgba(214, 224, 233, 0.94);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.94);
}

.activity-drawer__hero {
  grid-template-columns: 120px minmax(0, 1fr);
}

.activity-drawer__hero img {
  width: 120px;
  height: 120px;
  border-radius: 18px;
  object-fit: cover;
}

.activity-drawer__eyebrow {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #5880ca;
}

.activity-drawer__hero h3,
.activity-drawer__section h4,
.activity-drawer__grid strong,
.activity-drawer__timeline-item strong,
.activity-drawer__asset-list strong {
  margin: 0;
  color: #183962;
}

.activity-drawer__tags,
.activity-drawer__footer {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.activity-drawer__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.activity-drawer__grid span,
.activity-drawer__section p,
.activity-drawer__timeline-item span,
.activity-drawer__asset-list span {
  color: #6f7d8c;
  line-height: 1.7;
}

.activity-drawer__section p {
  margin: 0;
}

.activity-drawer__timeline,
.activity-drawer__asset-list {
  display: grid;
  gap: 10px;
}

.activity-drawer__timeline-item,
.activity-drawer__asset-list > div {
  display: grid;
  gap: 6px;
  padding: 12px 14px;
  border: 1px solid rgba(219, 227, 235, 0.92);
  border-radius: 14px;
  background: #fbfcfe;
}

.activity-drawer__footer {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  justify-content: flex-end;
  padding: 18px 24px 24px;
  border-top: 1px solid rgba(219, 227, 235, 0.95);
  background: rgba(255, 255, 255, 0.98);
}

@media (max-width: 720px) {
  .activity-drawer__hero,
  .activity-drawer__grid {
    grid-template-columns: 1fr;
  }
}
</style>
