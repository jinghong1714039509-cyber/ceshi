import type { RouteRecordRaw } from 'vue-router'

export const dashboardRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'dashboard',
    component: () => import('../../views/DashboardView.vue'),
    meta: {
      title: '总览',
      module: 'dashboard',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin'],
      permissions: ['dashboard:view'],
      icon: 'dashboard',
    },
  },
]
