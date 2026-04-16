<template>
  <div class="auth-layout login-layout">
    <section class="auth-panel login-panel login-panel--editorial">
      <div class="login-panel__headline">
        <span class="login-panel__brand">Campus Clubs</span>
        <h1>一个致力于为学生服务的平台</h1>
      </div>

      <div class="login-preview">
        <article v-for="activity in previewActivities" :key="activity.id" class="login-preview__card">
          <img :src="coverFor(activity)" :alt="activity.name" />
          <div class="login-preview__body">
            <h2>{{ activity.name }}</h2>
            <p>{{ activity.clubName }}</p>
            <span>{{ activity.activeTime }} · {{ activity.location }}</span>
          </div>
        </article>
      </div>
    </section>

    <section class="auth-panel login-panel login-panel--form">
      <div class="login-form__header">
        <h2>登录账号</h2>
      </div>

      <el-form :model="form" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.userName" placeholder="例如：student_chen" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <div class="stack-actions">
          <el-button :loading="submitting" type="primary" class="full-width" @click="handleLogin">登录</el-button>
          <el-button plain class="full-width" @click="router.push('/explore')">先浏览活动</el-button>
          <el-button text class="full-width" @click="router.push('/register')">没有账号，去注册</el-button>
        </div>
      </el-form>

      <div class="login-demo">
        <strong>演示账号</strong>
        <span>student_chen / 123456</span>
        <span>teacher_lin / 123456</span>
        <span>admin_media / 123456</span>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { getPublicActivities, type ActivitySummary } from '../api/activity'
import { pickDefaultCover } from '../content/default-covers'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const session = useSessionStore()
const submitting = ref(false)
const activities = ref<ActivitySummary[]>([])

const form = reactive({
  userName: 'student_chen',
  password: '123456',
})

const previewActivities = computed(() => activities.value.slice(0, 4))

function coverFor(activity: ActivitySummary) {
  return activity.coverImage || pickDefaultCover(activity.id)
}

async function loadPreview() {
  try {
    const result = await getPublicActivities()
    if (result.code === 0) {
      activities.value = result.data
    }
  } catch {
    // Ignore preview failure on login.
  }
}

async function handleLogin() {
  if (!form.userName || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  try {
    submitting.value = true
    const result = await login({
      userName: form.userName,
      password: form.password,
    })

    if (result.code !== 0) {
      ElMessage.error(result.msg || '登录失败')
      return
    }

    session.setSession(
      result.data.token,
      result.data.userId,
      result.data.role,
      result.data.displayName || result.data.userName,
      result.data.avatarUrl || '',
    )
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '登录失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  void loadPreview()
})
</script>

<style scoped>
.login-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) 420px;
  gap: 20px;
  padding: 24px;
}

.login-panel {
  min-height: calc(100vh - 48px);
}

.login-panel--editorial {
  display: grid;
  align-content: start;
  gap: 26px;
  background:
    radial-gradient(circle at 18% 14%, rgba(215, 119, 87, 0.14), transparent 22%),
    radial-gradient(circle at 82% 12%, rgba(122, 132, 113, 0.12), transparent 20%),
    linear-gradient(180deg, rgba(255, 253, 249, 0.98), rgba(247, 244, 239, 0.92));
}

.login-panel__headline {
  display: grid;
  gap: 10px;
}

.login-panel__brand {
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--terracotta-deep);
}

.login-panel__headline h1,
.login-form__header h2,
.login-preview__body h2 {
  margin: 0;
  font-family: var(--font-heading);
  color: var(--ink);
}

.login-panel__headline h1 {
  font-size: clamp(42px, 6vw, 72px);
  line-height: 0.94;
  max-width: 11ch;
}

.login-preview {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.login-preview__card {
  overflow: hidden;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(255, 253, 249, 0.92);
}

.login-preview__card img {
  display: block;
  width: 100%;
  height: 188px;
  object-fit: cover;
}

.login-preview__body {
  display: grid;
  gap: 6px;
  padding: 14px;
}

.login-preview__body h2 {
  font-size: 26px;
  line-height: 1.04;
}

.login-preview__body p,
.login-preview__body span {
  margin: 0;
  color: var(--ink-soft);
}

.login-panel--form {
  display: grid;
  align-content: center;
  gap: 22px;
}

.login-form__header {
  display: grid;
  gap: 8px;
}

.login-form__header h2 {
  font-size: 36px;
  line-height: 1;
}

.login-demo {
  display: grid;
  gap: 6px;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(243, 225, 215, 0.28);
  color: var(--ink-soft);
}

@media (max-width: 1080px) {
  .login-layout {
    grid-template-columns: 1fr;
  }

  .login-panel {
    min-height: auto;
  }
}

@media (max-width: 720px) {
  .login-layout {
    padding: 16px;
  }

  .login-preview {
    grid-template-columns: 1fr;
  }
}
</style>
