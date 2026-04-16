import type { MenuItem } from '../types/common'
import type { CurrentUserPayload, UserInfo } from '../types/auth'
import type { RouteRecordRaw } from 'vue-router'

export const ROLE_LABEL_MAP: Record<string, string> = {
  'super-admin': '系统管理员',
  'club-admin': '社团管理员',
  'teacher-admin': '指导教师',
  student: '学生用户',
  guest: '访客',
}

export const ROLE_PERMISSION_MAP: Record<string, string[]> = {
  'super-admin': [
    'dashboard:view',
    'forum:view',
    'forum:create',
    'forum:comment',
    'forum:like',
    'activity:view',
    'activity:apply',
    'activity:create',
    'club:view',
    'club:space',
    'message:view',
    'message:send',
    'approval:view',
    'approval:manage',
    'member:view',
    'member:manage',
    'notice:view',
    'notice:create',
    'user:view',
    'user:manage',
  ],
  'club-admin': [
    'dashboard:view',
    'forum:view',
    'forum:create',
    'forum:comment',
    'forum:like',
    'activity:view',
    'activity:apply',
    'activity:create',
    'club:view',
    'club:space',
    'message:view',
    'message:send',
    'approval:view',
    'approval:manage',
    'member:view',
    'member:manage',
    'notice:view',
    'notice:create',
  ],
  'teacher-admin': [
    'dashboard:view',
    'forum:view',
    'forum:create',
    'forum:comment',
    'forum:like',
    'activity:view',
    'activity:apply',
    'activity:create',
    'club:view',
    'club:space',
    'message:view',
    'message:send',
    'approval:view',
    'approval:manage',
    'member:view',
    'member:manage',
    'notice:view',
    'notice:create',
  ],
  student: [
    'forum:view',
    'forum:create',
    'forum:comment',
    'forum:like',
    'activity:view',
    'activity:apply',
    'club:view',
    'club:space',
    'message:view',
    'message:send',
    'notice:view',
  ],
  guest: [],
}

export function getRoleLabel(role: string) {
  return ROLE_LABEL_MAP[role] ?? '未知角色'
}

export function getPermissionsByRole(role: string) {
  return ROLE_PERMISSION_MAP[role] ?? []
}

export function hasRole(userRoles: string[], role: string | string[]) {
  const targetRoles = Array.isArray(role) ? role : [role]
  return targetRoles.some((item) => userRoles.includes(item))
}

export function hasPermission(userPermissions: string[], permission: string | string[]) {
  const targetPermissions = Array.isArray(permission) ? permission : [permission]
  return targetPermissions.every((item) => userPermissions.includes(item))
}

export function createUserInfoFromCurrentUser(currentUser: CurrentUserPayload): UserInfo {
  const roles = currentUser.role ? [currentUser.role] : []
  const permissions = getPermissionsByRole(currentUser.role)

  return {
    ...currentUser,
    roles,
    permissions,
  }
}

function flattenRoutes(routes: RouteRecordRaw[]) {
  return routes.flatMap((route) => [route, ...(route.children ? flattenRoutes(route.children) : [])])
}

export function buildMenusFromRoutes(routes: RouteRecordRaw[], userRoles: string[], userPermissions: string[]): MenuItem[] {
  const topLevelRouteNames = new Set(['dashboard', 'forum', 'activities', 'clubs', 'profile', 'messages', 'join-applications', 'notices', 'members', 'users'])

  return flattenRoutes(routes)
    .filter((route) => route.meta?.requiresAuth && !route.meta.hidden)
    .filter((route) => topLevelRouteNames.has(String(route.name ?? '')))
    .filter((route) => {
      const allowedRoles = route.meta?.roles ?? []
      const allowedPermissions = route.meta?.permissions ?? []
      const rolePass = allowedRoles.length === 0 || hasRole(userRoles, allowedRoles)
      const permissionPass = allowedPermissions.length === 0 || hasPermission(userPermissions, allowedPermissions)
      return rolePass && permissionPass
    })
    .map((route) => ({
      name: String(route.name ?? route.path),
      title: route.meta?.title ?? String(route.name ?? route.path),
      path: route.path,
      icon: route.meta?.icon,
      permission: route.meta?.permissions?.[0],
      hidden: route.meta?.hidden,
      children: [],
    }))
}
