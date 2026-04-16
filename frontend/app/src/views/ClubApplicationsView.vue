<template>
  <div class="approval-page">
    <section class="approval-toolbar">
      <div class="approval-toolbar__left">
        <el-radio-group v-model="mode" @change="loadData">
          <el-radio-button label="mine">我的申请</el-radio-button>
          <el-radio-button v-if="session.role !== 'student'" label="managed">待我审批</el-radio-button>
        </el-radio-group>

        <el-segmented v-model="statusFilter" :options="statusOptions" />
      </div>

      <div class="approval-toolbar__stats">
        <div class="approval-stat">
          <span>总记录</span>
          <strong>{{ filteredApplications.length }}</strong>
        </div>
        <div class="approval-stat">
          <span>处理中</span>
          <strong>{{ pendingCount }}</strong>
        </div>
      </div>
    </section>

    <section class="approval-panel table-panel">
      <div class="approval-panel__header">
        <div>
          <h2>{{ mode === 'managed' ? '审批列表' : '申请记录' }}</h2>
          <span>保留列表操作，但详细信息改为右侧抽屉，减少页面切换打断。</span>
        </div>
      </div>

      <el-table :data="filteredApplications" row-key="id" style="margin-top: 18px;">
        <el-table-column prop="clubName" label="社团" min-width="180" />
        <el-table-column prop="applicantName" label="申请人" min-width="140" />
        <el-table-column prop="applicantPhone" label="电话" min-width="140" />
        <el-table-column prop="createdAt" label="申请时间" min-width="180" sortable />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="approval-actions">
              <el-button link type="primary" @click="openDrawer(row)">查看详情</el-button>
              <el-button
                v-if="mode === 'managed' && row.status === 0"
                v-permission="'approval:manage'"
                link
                type="success"
                @click="approve(row.id)"
              >
                同意
              </el-button>
              <el-button
                v-else-if="mode === 'mine' && row.status === 0"
                link
                type="danger"
                @click="withdraw(row.id)"
              >
                撤销
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-drawer v-model="drawerVisible" :size="540" :with-header="false" class="approval-drawer">
      <template v-if="selectedApplication">
        <div class="approval-drawer__body">
          <section class="approval-drawer__section approval-drawer__hero">
            <div>
              <span class="approval-drawer__eyebrow">{{ mode === 'managed' ? 'Approval' : 'Record' }}</span>
              <h3>{{ selectedApplication.clubName }} 入社申请</h3>
            </div>
            <el-tag :type="statusTagType(selectedApplication.status)">{{ selectedApplication.statusLabel }}</el-tag>
          </section>

          <section class="approval-drawer__section">
            <h4>基础信息</h4>
            <div class="approval-info-grid">
              <div>
                <span>申请人</span>
                <strong>{{ selectedApplication.applicantName }}</strong>
              </div>
              <div>
                <span>联系电话</span>
                <strong>{{ selectedApplication.applicantPhone }}</strong>
              </div>
              <div>
                <span>社团</span>
                <strong>{{ selectedApplication.clubName }}</strong>
              </div>
              <div>
                <span>提交时间</span>
                <strong>{{ selectedApplication.createdAt }}</strong>
              </div>
            </div>
          </section>

          <section class="approval-drawer__section">
            <h4>申请详情</h4>
            <div class="approval-detail-card">
              <p>申请人希望加入 {{ selectedApplication.clubName }}，当前记录由系统按入社申请流程归档，可直接在本页完成审批处理。</p>
              <div class="approval-detail-card__meta">
                <span>申请编号：{{ selectedApplication.id }}</span>
                <span>申请人账号：{{ selectedApplication.applicantId }}</span>
              </div>
            </div>
          </section>

          <section class="approval-drawer__section">
            <h4>审批流</h4>
            <ApprovalTimeline :nodes="approvalTimeline" />
          </section>
        </div>

        <div class="approval-drawer__footer">
          <el-button @click="drawerVisible = false">关闭</el-button>
          <el-button
            v-if="mode === 'managed' && selectedApplication.status === 0"
            v-permission="'approval:manage'"
            type="warning"
            plain
            @click="showSupplementTip"
          >
            补充材料
          </el-button>
          <el-button
            v-if="mode === 'managed' && selectedApplication.status === 0"
            v-permission="'approval:manage'"
            type="danger"
            plain
            @click="showRejectTip"
          >
            驳回
          </el-button>
          <el-button
            v-if="mode === 'managed' && selectedApplication.status === 0"
            v-permission="'approval:manage'"
            type="primary"
            @click="approve(selectedApplication.id)"
          >
            同意
          </el-button>
          <el-button
            v-if="mode === 'mine' && selectedApplication.status === 0"
            type="danger"
            plain
            @click="withdraw(selectedApplication.id)"
          >
            撤销申请
          </el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ApprovalTimeline from '../components/approval/ApprovalTimeline.vue'
import {
  approveApplication,
  getManagedApplications,
  getMyApplications,
  withdrawApplication,
  type JoinApplication,
} from '../api/club'
import type { ApprovalNode } from '../types/approval'
import { useSessionStore } from '../stores/session'

const session = useSessionStore()
const mode = ref<'mine' | 'managed'>(session.role === 'student' ? 'mine' : 'managed')
const applications = ref<JoinApplication[]>([])
const drawerVisible = ref(false)
const selectedApplication = ref<JoinApplication | null>(null)
const statusFilter = ref<'all' | 'pending' | 'done'>('all')

const statusOptions = [
  { label: '全部', value: 'all' },
  { label: '处理中', value: 'pending' },
  { label: '已完成', value: 'done' },
]

const filteredApplications = computed(() => {
  if (statusFilter.value === 'pending') {
    return applications.value.filter((item) => item.status === 0)
  }

  if (statusFilter.value === 'done') {
    return applications.value.filter((item) => item.status !== 0)
  }

  return applications.value
})

const pendingCount = computed(() => applications.value.filter((item) => item.status === 0).length)

const approvalTimeline = computed<ApprovalNode[]>(() => {
  if (!selectedApplication.value) return []

  const isApproved = selectedApplication.value.status !== 0

  return [
    {
      id: `${selectedApplication.value.id}-submit`,
      nodeName: '提交申请',
      approverName: selectedApplication.value.applicantName,
      status: 'approved',
      comment: '已提交入社申请，等待社团管理员或指导教师审核。',
      actionTime: selectedApplication.value.createdAt,
    },
    {
      id: `${selectedApplication.value.id}-review`,
      nodeName: '管理员审核',
      approverName: mode.value === 'managed' ? session.displayName : '社团管理员 / 指导教师',
      status: isApproved ? 'approved' : 'pending',
      comment: isApproved ? '已通过审核，申请完成。' : '待审核，可在底部操作区直接处理。',
      actionTime: isApproved ? selectedApplication.value.createdAt : '',
    },
  ]
})

function statusTagType(status: number) {
  return status === 0 ? 'warning' : 'success'
}

function openDrawer(row: JoinApplication) {
  selectedApplication.value = row
  drawerVisible.value = true
}

async function loadData() {
  try {
    const result = mode.value === 'managed'
      ? await getManagedApplications()
      : await getMyApplications()

    if (result.code !== 0) {
      throw new Error(result.message)
    }
    applications.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载申请失败')
  }
}

async function approve(applicationId: string) {
  try {
    const result = await approveApplication(applicationId)
    if (result.code !== 0) throw new Error(result.message)
    ElMessage.success(result.message || '审批成功')
    drawerVisible.value = false
    await loadData()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '审批失败')
  }
}

async function withdraw(applicationId: string) {
  try {
    await ElMessageBox.confirm('撤销后需要重新提交申请，是否继续？', '撤销申请', {
      type: 'warning',
    })
    const result = await withdrawApplication(applicationId)
    if (result.code !== 0) throw new Error(result.message)
    ElMessage.success(result.message || '撤销成功')
    drawerVisible.value = false
    await loadData()
  } catch (error: any) {
    if (error === 'cancel') return
    ElMessage.error(error?.response?.data?.msg || error?.message || '撤销失败')
  }
}

function showRejectTip() {
  ElMessage.warning('当前后端暂未提供驳回接口，前端已预留审批位。')
}

function showSupplementTip() {
  ElMessage.info('当前后端暂未提供补充材料接口，前端已预留扩展位。')
}

onMounted(() => {
  void loadData()
})
</script>

<style scoped>
.approval-page {
  display: grid;
  gap: 20px;
}

.approval-toolbar,
.approval-panel {
  border: 1px solid rgba(208, 219, 228, 0.92);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 249, 252, 0.96));
  box-shadow: 0 18px 36px rgba(20, 51, 93, 0.07);
}

.approval-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
}

.approval-toolbar__left,
.approval-toolbar__stats,
.approval-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.approval-stat {
  display: grid;
  gap: 6px;
  padding: 10px 14px;
  border: 1px solid rgba(217, 226, 235, 0.92);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.9);
}

.approval-stat span {
  color: #708091;
}

.approval-stat strong {
  font-size: 24px;
  color: #183962;
}

.approval-panel {
  padding: 22px;
}

.approval-panel__header h2 {
  margin: 0 0 8px;
  color: #183962;
}

.approval-panel__header span {
  color: #6d7c8c;
}

.approval-drawer__body {
  display: grid;
  gap: 18px;
  padding: 24px 24px 100px;
}

.approval-drawer__section {
  display: grid;
  gap: 14px;
  padding: 18px;
  border: 1px solid rgba(214, 224, 233, 0.94);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.94);
}

.approval-drawer__hero {
  display: flex;
  justify-content: space-between;
  align-items: start;
}

.approval-drawer__eyebrow {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #5880ca;
}

.approval-drawer__hero h3,
.approval-drawer__section h4 {
  margin: 0;
  color: #17355f;
}

.approval-info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.approval-info-grid div,
.approval-detail-card {
  display: grid;
  gap: 6px;
}

.approval-info-grid span,
.approval-detail-card p,
.approval-detail-card__meta span {
  color: #6f7d8c;
}

.approval-info-grid strong {
  color: #183962;
}

.approval-detail-card {
  padding: 14px 16px;
  border: 1px solid rgba(218, 226, 235, 0.92);
  border-radius: 16px;
  background: #fbfcfe;
}

.approval-detail-card p {
  margin: 0;
  line-height: 1.8;
}

.approval-detail-card__meta {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.approval-drawer__footer {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 18px 24px 24px;
  border-top: 1px solid rgba(219, 227, 235, 0.95);
  background: rgba(255, 255, 255, 0.98);
}

@media (max-width: 900px) {
  .approval-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .approval-info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
