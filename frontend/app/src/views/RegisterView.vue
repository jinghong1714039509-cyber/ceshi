<template>
  <div class="auth-layout">
    <section class="auth-panel auth-panel--highlight">
      <div>
        <p class="hero-panel__eyebrow">Registration</p>
        <h1 class="page-title" style="margin-top: 12px;">创建学生账号</h1>
        <p class="page-subtitle">
          注册默认创建学生身份。表单结构已经按真实业务整理，避免过去那种演示感很强的空壳页面。
        </p>
      </div>

      <div class="card-grid">
        <article class="data-card">
          <h3>注册后可做什么</h3>
          <p>浏览社团、发起入团申请、报名活动、查看公告和管理个人记录。</p>
        </article>
        <article class="data-card">
          <h3>适用场景</h3>
          <p>面向普通学生用户。管理员账号仍建议由后台统一创建和管理。</p>
        </article>
      </div>
    </section>

    <section class="auth-panel auth-panel--form">
      <div class="section-header">
        <div>
          <p>Register</p>
          <h3>填写基础信息</h3>
          <span>提交后即可返回登录页进入系统。</span>
        </div>
      </div>

      <el-form :model="form" label-position="top" style="margin-top: 18px;" @submit.prevent="submit">
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
          <el-form-item label="手机号">
            <el-input v-model="form.phone" />
          </el-form-item>
        </div>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <div class="stack-actions">
          <el-button :loading="submitting" type="primary" class="full-width" @click="submit">
            提交注册
          </el-button>
          <el-button plain class="full-width" @click="router.push('/login')">返回登录</el-button>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerUser } from '../api/user'

const router = useRouter()
const submitting = ref(false)

const form = reactive({
  userName: '',
  password: '',
  name: '',
  gender: '男',
  age: 18,
  phone: '',
  address: '',
})

async function submit() {
  try {
    submitting.value = true
    const result = await registerUser(form)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '注册成功')
    router.push('/login')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '注册失败')
  } finally {
    submitting.value = false
  }
}
</script>
