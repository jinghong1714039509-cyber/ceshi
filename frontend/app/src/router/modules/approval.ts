import type { RouteRecordRaw } from 'vue-router'

export const approvalRoutes: RouteRecordRaw[] = [
  {
    path: '/join-applications',
    name: 'join-applications',
    component: () => import('../../views/ClubApplicationsView.vue'),
    meta: {
      title: '审批中心',
      module: 'approval',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin'],
      permissions: ['approval:view'],
      icon: 'approvals',
    },
  },
]
