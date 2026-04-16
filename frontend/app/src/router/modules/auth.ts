import type { RouteRecordRaw } from 'vue-router'

export const authRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../../views/LoginView.vue'),
    meta: {
      title: '登录',
      module: 'auth',
      requiresAuth: false,
      hidden: true,
    },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../../views/RegisterView.vue'),
    meta: {
      title: '注册',
      module: 'auth',
      requiresAuth: false,
      hidden: true,
    },
  },
]
