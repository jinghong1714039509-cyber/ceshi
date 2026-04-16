<template>
  <div class="page-stack">
    <section class="page-header">
      <div>
        <p class="page-eyebrow">Applications</p>
        <h1 class="page-title">入团申请</h1>
        <p class="page-subtitle">把“我的申请”和“待我审批”做成统一工作台，而不是只有一个裸表格。</p>
      </div>
    </section>

    <section class="section-panel">
      <div class="section-header">
        <div>
          <p>Mode</p>
          <h3>切换工作视角</h3>
          <span>学生看自己的申请记录，管理员直接切到审批视角。</span>
        </div>
      </div>
      <div class="toolbar" style="margin-top: 18px;">
        <el-radio-group v-model="mode" @change="loadData">
          <el-radio-button label="mine">我的申请</el-radio-button>
          <el-radio-button v-if="session.role !== 'student'" label="managed">待我审批</el-radio-button>
        </el-radio-group>
      </div>
    </section>

    <section class="section-panel table-panel">
      <div class="section-header">
        <div>
          <p>Records</p>
          <h3>{{ mode === 'managed' ? '审批列表' : '申请列表' }}</h3>
          <span>状态、联系方式和发起时间统一展示。</span>
        </div>
      </div>

      <el-table :data="applications" style="margin-top: 18px;">
        <el-table-column prop="clubName" label="社团" min-width="180" />
        <el-table-column prop="applicantName" label="申请人" min-width="140" />
        <el-table-column prop="applicantPhone" label="电话" min-width="140" />
        <el-table-column prop="createdAt" label="申请时间" min-width="180" />
        <el-table-column prop="statusLabel" label="状态" width="120" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button
              v-if="mode === 'managed' && row.status === 0"
              type="primary"
              link
              @click="approve(row.id)"
            >
              通过
            </el-button>
            <el-button
              v-else-if="mode === 'mine' && row.status === 0"
              type="danger"
              link
              @click="withdraw(row.id)"
            >
              撤销
            </el-button>
            <span v-else class="muted-text">--</span>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveApplication,
  getManagedApplications,
  getMyApplications,
  withdrawApplication,
  type JoinApplication,
} from '../api/club'
import { useSessionStore } from '../stores/session'

const session = useSessionStore()
const mode = ref<'mine' | 'managed'>(session.role === 'student' ? 'mine' : 'managed')
const applications = ref<JoinApplication[]>([])

async function loadData() {
  try {
    const result = mode.value === 'managed'
      ? await getManagedApplications()
      : await getMyApplications()

    if (result.code !== 0) {
      throw new Error(result.msg)
    }
    applications.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载申请失败')
  }
}

async function approve(applicationId: string) {
  try {
    const result = await approveApplication(applicationId)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '审批成功')
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
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '撤销成功')
    await loadData()
  } catch (error: any) {
    if (error === 'cancel') return
    ElMessage.error(error?.response?.data?.msg || error?.message || '撤销失败')
  }
}

onMounted(() => {
  void loadData()
})
</script>
