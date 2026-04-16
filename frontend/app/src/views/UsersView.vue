<template>
  <div class="page-stack">
    <section class="page-header">
      <div>
        <p class="page-eyebrow">User Management</p>
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">
          统一管理学生、社团管理员、指导教师和系统管理员账号。教师账号可直接作为社团负责人使用。
        </p>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="dialogVisible = true">新增用户</el-button>
      </div>
    </section>

    <section class="metrics-grid">
      <article class="metric-card">
        <span class="metric-card__label">用户总数</span>
        <strong>{{ users.length }}</strong>
        <span class="stat-note">当前系统中已创建的账号数量。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">启用账号</span>
        <strong>{{ enabledCount }}</strong>
        <span class="stat-note">可以正常登录的平台账号。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">停用账号</span>
        <strong>{{ disabledCount }}</strong>
        <span class="stat-note">已被管理员禁用的账号。</span>
      </article>
      <article class="metric-card">
        <span class="metric-card__label">角色类型</span>
        <strong>{{ roleTypeCount }}</strong>
        <span class="stat-note">当前演示数据覆盖的角色种类。</span>
      </article>
    </section>

    <section class="section-panel table-panel">
      <div class="section-header">
        <div>
          <p>Accounts</p>
          <h3>系统账号</h3>
          <span>支持直接创建不同类型账号，并在列表内快速启停。</span>
        </div>
      </div>

      <el-table :data="users" style="margin-top: 18px;">
        <el-table-column prop="userName" label="用户名" min-width="140" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="roleLabel" label="角色" width="160" />
        <el-table-column prop="statusLabel" label="状态" width="100" />
        <el-table-column prop="phone" label="电话" min-width="140" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" title="新增用户" width="720px">
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="用户名">
            <el-input v-model="form.userName" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input-number v-model="form.age" :min="10" :max="100" class="full-width" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="form.type">
              <el-option label="系统管理员" :value="0" />
              <el-option label="社团管理员" :value="1" />
              <el-option label="学生" :value="2" />
              <el-option label="指导教师" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option label="启用" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createUser, getUsers, updateUserStatus, type UserItem } from '../api/user'

const users = ref<UserItem[]>([])
const dialogVisible = ref(false)

const form = reactive({
  userName: '',
  password: '',
  name: '',
  gender: '男',
  age: 18,
  phone: '',
  address: '',
  type: 2,
  status: 1,
})

const enabledCount = computed(() => users.value.filter((item) => item.status === 1).length)
const disabledCount = computed(() => users.value.filter((item) => item.status !== 1).length)
const roleTypeCount = computed(() => new Set(users.value.map((item) => item.roleLabel)).size)

async function loadUsers() {
  try {
    const result = await getUsers()
    if (result.code !== 0) throw new Error(result.msg)
    users.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载用户失败')
  }
}

async function submit() {
  try {
    const result = await createUser(form)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '创建成功')
    dialogVisible.value = false
    Object.assign(form, {
      userName: '',
      password: '',
      name: '',
      gender: '男',
      age: 18,
      phone: '',
      address: '',
      type: 2,
      status: 1,
    })
    await loadUsers()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '创建失败')
  }
}

async function toggleStatus(user: UserItem) {
  try {
    const nextStatus = user.status === 1 ? 0 : 1
    const result = await updateUserStatus(user.id, nextStatus)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '状态更新成功')
    await loadUsers()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '状态更新失败')
  }
}

onMounted(() => {
  void loadUsers()
})
</script>
