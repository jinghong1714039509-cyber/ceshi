<template>
  <el-drawer v-model="model" :size="560" :with-header="false">
    <template v-if="block">
      <div class="schedule-drawer">
        <section class="schedule-drawer__section">
          <span class="schedule-drawer__eyebrow">Schedule</span>
          <h3>{{ block.name }}</h3>
          <div class="schedule-drawer__chips">
            <el-tag>{{ block.clubName }}</el-tag>
            <el-tag type="success">{{ block.stageLabel }}</el-tag>
          </div>
        </section>

        <section class="schedule-drawer__section">
          <h4>活动基础信息</h4>
          <div class="schedule-drawer__grid">
            <div><span>活动时间</span><strong>{{ block.activeTime }}</strong></div>
            <div><span>活动地点</span><strong>{{ block.location }}</strong></div>
            <div><span>报名人数</span><strong>{{ block.total }}/{{ block.capacity || '不限' }}</strong></div>
            <div><span>结束状态</span><strong>{{ completionLabel(block.completionStatus) }}</strong></div>
          </div>
        </section>

        <section class="schedule-drawer__section">
          <h4>当前阶段</h4>
          <el-steps :active="activeStep" align-center finish-status="success">
            <el-step title="筹备" description="确定方案和场地" />
            <el-step title="宣传" description="招募与海报传播" />
            <el-step title="执行中" description="现场组织与签到" />
            <el-step title="复盘" description="结束材料与教师确认" />
          </el-steps>
        </section>

        <section class="schedule-drawer__section">
          <h4>时间安排</h4>
          <div class="schedule-drawer__list">
            <div><strong>T-7 ~ T-4</strong><span>完成人员分工、预算草案与物料清单。</span></div>
            <div><strong>T-3 ~ T-1</strong><span>发布宣传内容，打开报名入口并确认场地。</span></div>
            <div><strong>T 日</strong><span>执行活动，完成签到、现场组织和影像记录。</span></div>
            <div><strong>T+1 ~ T+3</strong><span>提交结束照片、总结与指导教师确认意见。</span></div>
          </div>
        </section>

        <section class="schedule-drawer__section">
          <h4>经费预算审批节点</h4>
          <div class="schedule-drawer__list">
            <div><strong>预算提交</strong><span>由活动负责人提交预算清单并进入教师确认。</span></div>
            <div><strong>教师确认</strong><span>指导教师核对活动内容与预算合理性。</span></div>
            <div><strong>执行结项</strong><span>活动完成后补充照片与复盘说明，形成归档记录。</span></div>
          </div>
        </section>

        <section class="schedule-drawer__section">
          <h4>关联物资申请进度</h4>
          <div class="schedule-drawer__list">
            <div><strong>宣传物料</strong><span>海报、横幅与签到板在宣传阶段准备。</span></div>
            <div><strong>现场设备</strong><span>执行前一天确认音响、投影和志愿者物资。</span></div>
            <div><strong>影像归档</strong><span>复盘阶段整理结束照片，完成活动闭环。</span></div>
          </div>
        </section>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ActivityLifecycleBlock } from '../../types/activity-view'

const model = defineModel<boolean>({ required: true })
const props = defineProps<{ block: ActivityLifecycleBlock | null }>()

const activeStep = computed(() => {
  switch (props.block?.stage) {
    case 'promotion':
      return 2
    case 'running':
      return 3
    case 'review':
      return 4
    default:
      return 1
  }
})

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
.schedule-drawer {
  display: grid;
  gap: 18px;
  padding: 24px;
}

.schedule-drawer__section {
  display: grid;
  gap: 14px;
  padding: 18px;
  border: 1px solid rgba(214, 224, 233, 0.94);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.94);
}

.schedule-drawer__eyebrow {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #5880ca;
}

.schedule-drawer__section h3,
.schedule-drawer__section h4,
.schedule-drawer__grid strong,
.schedule-drawer__list strong {
  margin: 0;
  color: #183962;
}

.schedule-drawer__chips {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.schedule-drawer__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.schedule-drawer__grid span,
.schedule-drawer__list span {
  color: #6f7d8c;
  line-height: 1.7;
}

.schedule-drawer__list {
  display: grid;
  gap: 10px;
}

.schedule-drawer__list > div {
  display: grid;
  gap: 6px;
  padding: 12px 14px;
  border: 1px solid rgba(219, 227, 235, 0.92);
  border-radius: 14px;
  background: #fbfcfe;
}

@media (max-width: 720px) {
  .schedule-drawer__grid {
    grid-template-columns: 1fr;
  }
}
</style>
