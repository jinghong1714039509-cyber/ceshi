import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title: string
    module: 'auth' | 'dashboard' | 'club' | 'activity' | 'approval' | 'fund' | 'asset' | 'system' | 'community' | 'profile' | 'public'
    requiresAuth?: boolean
    roles?: string[]
    permissions?: string[]
    icon?: string
    hidden?: boolean
  }
}
