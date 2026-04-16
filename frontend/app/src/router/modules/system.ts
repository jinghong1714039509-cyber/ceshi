import type { RouteRecordRaw } from 'vue-router'

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/members',
    name: 'members',
    component: () => import('../../views/MembersView.vue'),
    meta: {
      title: '成员管理',
      module: 'system',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin'],
      permissions: ['member:view'],
      icon: 'members',
    },
  },
  {
    path: '/notices',
    name: 'notices',
    component: () => import('../../views/NoticesView.vue'),
    meta: {
      title: '公告',
      module: 'system',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['notice:view'],
      icon: 'notices',
    },
  },
  {
    path: '/users',
    name: 'users',
    component: () => import('../../views/UsersView.vue'),
    meta: {
      title: '用户管理',
      module: 'system',
      requiresAuth: true,
      roles: ['super-admin'],
      permissions: ['user:view'],
      icon: 'users',
    },
  },
]
