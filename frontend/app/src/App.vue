<template>
  <div :class="['app-root', { 'app-root--public': isPublicPage }]">
    <template v-if="isPublicPage">
      <RouterView />
    </template>

    <template v-else>
      <aside class="app-sidebar">
        <div class="app-brand" @click="router.push(homePath)">
          <span class="app-brand__mark">{{ APP_NAME }}</span>
          <strong>{{ APP_TITLE }}</strong>
        </div>

        <nav class="app-nav">
          <button
            v-for="item in visibleMenus"
            :key="item.path"
            type="button"
            :class="['app-nav__item', { 'app-nav__item--active': isActive(item.path) }]"
            @click="handleNavigation(item.path)"
          >
            <el-icon><component :is="getIconComponent(item.icon, item.name)" /></el-icon>
            <span>{{ item.title }}</span>
          </button>
        </nav>

        <div class="app-sidebar__footer">
          <button type="button" class="app-public-link" @click="router.push('/explore')">公开浏览</button>
        </div>
      </aside>

      <div class="app-main">
        <header class="app-header">
          <h1>{{ pageTitle }}</h1>
          <el-popover placement="bottom-end" :width="320" trigger="click" popper-class="paper-popover">
            <template #reference>
              <button type="button" class="account-trigger">
                <img v-if="session.avatarUrl" :src="session.avatarUrl" alt="用户头像" />
                <span v-else>{{ avatarText }}</span>
              </button>
            </template>

            <div class="account-panel">
              <div class="account-panel__hero">
                <div class="account-panel__avatar">
                  <img v-if="session.avatarUrl" :src="session.avatarUrl" alt="用户头像" />
                  <span v-else>{{ avatarText }}</span>
                </div>
                <div class="account-panel__meta">
                  <strong>{{ session.displayName || '未登录用户' }}</strong>
                  <span>{{ roleLabel }}</span>
                  <small>{{ session.userId || 'guest' }}</small>
                </div>
              </div>

              <input ref="avatarInput" type="file" accept="image/*" class="account-panel__input" @change="handleAvatarChange" />

              <div class="account-panel__actions">
                <el-button :loading="avatarUploading" plain @click="avatarInput?.click()">上传头像</el-button>
                <el-button v-permission="'club:space'" plain @click="router.push('/profile')">我的空间</el-button>
                <el-button type="danger" plain @click="logout">退出登录</el-button>
              </div>
            </div>
          </el-popover>
        </header>

        <main class="app-content">
          <RouterView />
        </main>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { updateAvatar, uploadAvatar } from './api/auth'
import {
  APP_NAME,
  APP_TITLE,
  PUBLIC_ROUTE_NAMES,
  getAvatarText,
  getDefaultLandingPath,
  getHeaderTitle,
  getIconComponent,
  getVisibleMenus,
} from './app-shell'
import { useSessionStore } from './stores/session'
import { getRoleLabel } from './utils/access'

const route = useRoute()
const router = useRouter()
const session = useSessionStore()
const avatarInput = ref<HTMLInputElement | null>(null)
const avatarUploading = ref(false)

const isPublicPage = computed(() => PUBLIC_ROUTE_NAMES.includes(String(route.name ?? '')))
const visibleMenus = computed(() => getVisibleMenus(session.menus))
const roleLabel = computed(() => getRoleLabel(session.role))
const pageTitle = computed(() => getHeaderTitle(String(route.meta.title ?? '')))
const avatarText = computed(() => getAvatarText(session.displayName))
const homePath = computed(() => getDefaultLandingPath(session.role))

function isActive(path: string) {
  return route.path === path || route.path.startsWith(`${path}/`)
}

function handleNavigation(path: string) {
  if (route.path !== path) {
    router.push(path)
  }
}

async function handleAvatarChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  try {
    avatarUploading.value = true
    const uploadResult = await uploadAvatar(file)
    if (uploadResult.code !== 0) throw new Error(uploadResult.message)

    const saveResult = await updateAvatar(uploadResult.data.url)
    if (saveResult.code !== 0) throw new Error(saveResult.message)

    session.setAvatar(saveResult.data.avatarUrl || '')
    ElMessage.success('头像更新成功')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '头像更新失败')
  } finally {
    avatarUploading.value = false
    if (avatarInput.value) avatarInput.value.value = ''
  }
}

function logout() {
  session.clearSession()
  router.push('/login')
}

onMounted(async () => {
  if (session.isAuthenticated && !session.initialized) {
    try {
      await session.bootstrapSession()
    } catch {
      session.clearSession()
      router.push('/login')
    }
  }
})
</script>

<style scoped>
.app-sidebar {
  display: grid;
  grid-template-rows: auto 1fr auto;
  gap: 28px;
  padding: 28px 20px 22px;
  border-right: 1px solid var(--line);
  background: linear-gradient(180deg, rgba(255, 253, 249, 0.96), rgba(247, 244, 239, 0.92));
}

.app-brand {
  display: grid;
  gap: 8px;
  cursor: pointer;
}

.app-brand__mark {
  font-size: 11px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--terracotta-deep);
}

.app-brand strong {
  font-family: var(--font-heading);
  font-size: 34px;
  line-height: 1;
  color: var(--ink);
}

.app-nav {
  display: grid;
  align-content: start;
  gap: 6px;
}

.app-nav__item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 14px;
  border: 1px solid transparent;
  border-radius: 10px;
  background: transparent;
  color: var(--ink-soft);
  cursor: pointer;
  text-align: left;
  transition: background-color 140ms ease, border-color 140ms ease, color 140ms ease;
}

.app-nav__item:hover {
  background: rgba(243, 225, 215, 0.45);
  border-color: rgba(214, 202, 189, 0.7);
}

.app-nav__item--active {
  background: var(--terracotta-soft);
  border-color: var(--line-strong);
  color: var(--ink);
}

.app-sidebar__footer {
  display: grid;
}

.app-public-link {
  width: 100%;
  padding: 11px 14px;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: var(--surface);
  color: var(--ink);
  cursor: pointer;
}

.app-main {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  min-width: 0;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px 28px 18px;
  border-bottom: 1px solid rgba(230, 221, 209, 0.9);
  background: rgba(252, 251, 248, 0.9);
}

.app-header h1 {
  margin: 0;
  font-family: var(--font-heading);
  font-size: clamp(34px, 4vw, 50px);
  line-height: 0.96;
  color: var(--ink);
}

.account-trigger {
  width: 54px;
  height: 54px;
  border: 1px solid var(--line);
  border-radius: 999px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--terracotta-soft), #fff7ef);
  color: var(--terracotta-deep);
  font-weight: 700;
  cursor: pointer;
}

.account-trigger img,
.account-panel__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.account-panel {
  display: grid;
  gap: 18px;
}

.account-panel__hero {
  display: grid;
  grid-template-columns: 64px minmax(0, 1fr);
  gap: 14px;
  align-items: center;
}

.account-panel__avatar {
  width: 64px;
  height: 64px;
  border-radius: 999px;
  overflow: hidden;
  border: 1px solid var(--line);
  background: linear-gradient(135deg, var(--terracotta-soft), #fff7ef);
  color: var(--terracotta-deep);
  display: grid;
  place-items: center;
  font-weight: 700;
}

.account-panel__meta {
  display: grid;
  gap: 4px;
}

.account-panel__meta strong {
  color: var(--ink);
}

.account-panel__meta span,
.account-panel__meta small {
  color: var(--ink-soft);
}

.account-panel__input {
  display: none;
}

.account-panel__actions {
  display: grid;
  gap: 10px;
}

.app-content {
  padding: 24px 28px 32px;
}

@media (max-width: 980px) {
  .app-root {
    grid-template-columns: 1fr;
  }

  .app-sidebar {
    grid-template-rows: auto auto auto;
    border-right: 0;
    border-bottom: 1px solid var(--line);
  }

  .app-nav {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .app-header {
    padding: 18px 18px 14px;
  }

  .app-content {
    padding: 18px;
  }
}

@media (max-width: 720px) {
  .app-nav {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
