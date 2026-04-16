import type { RouteRecordRaw } from 'vue-router'

export const activityRoutes: RouteRecordRaw[] = [
  {
    path: '/activities',
    name: 'activities',
    component: () => import('../../views/ActivitiesView.vue'),
    meta: {
      title: '活动',
      module: 'activity',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['activity:view'],
      icon: 'activities',
    },
  },
  {
    path: '/activities/schedule',
    name: 'activity-schedule',
    component: () => import('../../views/ActivityScheduleView.vue'),
    meta: {
      title: '活动排期',
      module: 'activity',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin'],
      permissions: ['activity:view'],
      hidden: true,
    },
  },
  {
    path: '/activities/:activityId',
    name: 'activity-detail',
    component: () => import('../../views/ActivityDetailView.vue'),
    meta: {
      title: '活动详情',
      module: 'activity',
      requiresAuth: true,
      roles: ['super-admin', 'club-admin', 'teacher-admin', 'student'],
      permissions: ['activity:view'],
      hidden: true,
    },
  },
]
