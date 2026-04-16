import type { RouteRecordRaw } from 'vue-router'

export const clubRoutes: RouteRecordRaw[] = [
  {
    path: '/clubs',
    name: 'clubs',
    component: () => import('../../views/ClubsView.vue'),
    meta: {
      title: '社团',
      module: 'club',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['club:view'],
      icon: 'clubs',
    },
  },
  {
    path: '/clubs/:clubId',
    name: 'club-detail',
    component: () => import('../../views/ClubDetailView.vue'),
    meta: {
      title: '社团详情',
      module: 'club',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['club:view'],
      hidden: true,
    },
  },
  {
    path: '/clubs/:clubId/space',
    name: 'club-space',
    component: () => import('../../views/ClubSpaceView.vue'),
    meta: {
      title: '社团空间',
      module: 'club',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['club:space'],
      hidden: true,
    },
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../../views/ProfileView.vue'),
    meta: {
      title: '社团空间',
      module: 'profile',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['club:space'],
      icon: 'profile',
    },
  },
]
