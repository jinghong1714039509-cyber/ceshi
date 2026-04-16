import type { RouteRecordRaw } from 'vue-router'

export const communityRoutes: RouteRecordRaw[] = [
  {
    path: '/forum',
    name: 'forum',
    component: () => import('../../views/ForumView.vue'),
    meta: {
      title: '论坛',
      module: 'community',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['forum:view'],
      icon: 'forum',
    },
  },
  {
    path: '/messages',
    name: 'messages',
    component: () => import('../../views/MessagesView.vue'),
    meta: {
      title: '消息',
      module: 'community',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['message:view'],
      icon: 'messages',
    },
  },
]
