<template>
  <div class="page-stack">
    <section class="page-header">
      <div>
        <p class="page-eyebrow">Members</p>
        <h1 class="page-title">成员管理</h1>
      </div>
    </section>

    <section class="metrics-grid">
      <article class="metric-card">
        <span class="metric-card__label">成员总数</span>
        <strong>{{ members.length }}</strong>
        <span class="stat-note">当前列表中的全部成员。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">管理人员</span>
        <strong>{{ managerCount }}</strong>
        <span class="stat-note">带有管理权限的成员数量。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">普通成员</span>
        <strong>{{ memberCount }}</strong>
        <span class="stat-note">可发起私信和移除操作的成员。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">涉及社团</span>
        <strong>{{ teamCount }}</strong>
        <span class="stat-note">当前名单覆盖的社团数量。</span>
      </article>
    </section>

    <section class="section-panel table-panel">
      <div class="section-header">
        <div>
          <p>Roster</p>
          <h3>成员名单</h3>
        </div>
      </div>

      <el-table :data="members" style="margin-top: 18px;">
        <el-table-column prop="teamName" label="社团" min-width="160" />
        <el-table-column prop="userName" label="姓名" min-width="120" />
        <el-table-column prop="userGender" label="性别" width="90" />
        <el-table-column prop="userAge" label="年龄" width="90" />
        <el-table-column prop="userPhone" label="电话" min-width="140" />
        <el-table-column prop="createTime" label="加入时间" min-width="180" />
        <el-table-column label="身份" width="110">
          <template #default="{ row }">
            <el-tag :type="row.manager ? 'warning' : 'info'">{{ row.manager ? '管理者' : '成员' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180">
          <template #default="{ row }">
            <div class="toolbar">
              <el-button v-if="!row.manager" type="primary" link @click="openChat(row.userId)">私信</el-button>
              <el-button v-if="!row.manager" type="danger" link @click="remove(row.id)">移除</el-button>
              <span v-else class="muted-text">--</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMembers, removeMember, type MemberItem } from '../api/member'

const router = useRouter()
const members = ref<MemberItem[]>([])

const managerCount = computed(() => members.value.filter((item) => item.manager).length)
const memberCount = computed(() => members.value.filter((item) => !item.manager).length)
const teamCount = computed(() => new Set(members.value.map((item) => item.teamName)).size)

async function loadMembers() {
  try {
    const result = await getMembers()
    if (result.code !== 0) throw new Error(result.msg)
    members.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载成员失败')
  }
}

function openChat(userId: string) {
  router.push(`/messages?contact=${userId}`)
}

async function remove(memberId: string) {
  try {
    await ElMessageBox.confirm('确认移除该成员吗？', '提示', { type: 'warning' })
    const result = await removeMember(memberId)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '移除成功')
    await loadMembers()
  } catch (error: any) {
    if (error === 'cancel') return
    ElMessage.error(error?.response?.data?.msg || error?.message || '移除失败')
  }
}

onMounted(() => {
  void loadMembers()
})
</script>
