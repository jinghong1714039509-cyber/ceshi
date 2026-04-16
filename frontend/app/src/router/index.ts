import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDefaultLandingPath } from '../app-shell'
import { useSessionStore } from '../stores/session'
import { hasPermission, hasRole } from '../utils/access'
import { activityRoutes } from './modules/activity'
import { approvalRoutes } from './modules/approval'
import { authRoutes } from './modules/auth'
import { clubRoutes } from './modules/club'
import { communityRoutes } from './modules/community'
import { dashboardRoutes } from './modules/dashboard'
import { publicRoutes } from './modules/public'
import { systemRoutes } from './modules/system'

const routes: RouteRecordRaw[] = [
  ...dashboardRoutes,
  ...authRoutes,
  ...publicRoutes,
  ...clubRoutes,
  ...activityRoutes,
  ...approvalRoutes,
  ...communityRoutes,
  ...systemRoutes,
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
    meta: {
      title: '页面跳转',
      module: 'system',
      hidden: true,
    },
  },
]

export function getAppRoutes() {
  return routes
}

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  const session = useSessionStore()

  document.title = `${to.meta.title ?? '校园社团'} | Campus Clubs`

  if (session.isAuthenticated && !session.initialized && to.name !== 'login') {
    try {
      await session.bootstrapSession()
    } catch {
      session.clearSession()
      if (to.meta.requiresAuth) {
        return { name: 'login', query: { redirect: to.fullPath } }
      }
    }
  }

  if (to.meta.requiresAuth && !session.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if ((to.name === 'login' || to.name === 'register') && session.isAuthenticated) {
    return getDefaultLandingPath(session.role)
  }

  if (to.meta.roles?.length && !hasRole(session.roles, to.meta.roles)) {
    ElMessage.warning('当前账号无权访问该页面')
    return getDefaultLandingPath(session.role)
  }

  if (to.meta.permissions?.length && !hasPermission(session.permissions, to.meta.permissions)) {
    ElMessage.warning('当前账号缺少页面权限')
    return getDefaultLandingPath(session.role)
  }

  return true
})

export default router
