import type { RouteRecordRaw } from 'vue-router'

export const publicRoutes: RouteRecordRaw[] = [
  {
    path: '/explore',
    name: 'explore',
    component: () => import('../../views/ExploreView.vue'),
    meta: {
      title: '公开浏览',
      module: 'public',
      requiresAuth: false,
      hidden: true,
    },
  },
]
