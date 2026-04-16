import type { Component } from 'vue'
import {
  Calendar,
  ChatDotRound,
  Collection,
  EditPen,
  House,
  Message,
  Setting,
  UserFilled,
} from '@element-plus/icons-vue'

export type AppRole = 'super-admin' | 'club-admin' | 'teacher-admin' | 'student' | 'guest' | string

export interface NavigationItem {
  label: string
  path: string
  icon: Component
  roles?: AppRole[]
}

export const navigationItems: NavigationItem[] = [
  { label: '总览', path: '/', icon: House, roles: ['super-admin', 'club-admin', 'teacher-admin'] },
  { label: '论坛', path: '/forum', icon: EditPen },
  { label: '活动', path: '/activities', icon: Calendar },
  { label: '社团', path: '/clubs', icon: Collection },
  { label: '社团空间', path: '/profile', icon: ChatDotRound },
  { label: '消息', path: '/messages', icon: Message },
  { label: '成员', path: '/members', icon: UserFilled, roles: ['super-admin', 'club-admin', 'teacher-admin'] },
  { label: '用户', path: '/users', icon: Setting, roles: ['super-admin'] },
]

export function getVisibleNavigation(role: AppRole) {
  const managerRoles = ['super-admin', 'club-admin', 'teacher-admin']
  if (managerRoles.includes(role)) {
    return navigationItems.filter((item) => !item.roles || item.roles.includes(role))
  }
  return navigationItems.filter((item) => ['/forum', '/activities', '/clubs', '/profile', '/messages'].includes(item.path))
}

export function getRoleLabel(role: AppRole) {
  switch (role) {
    case 'super-admin':
      return '系统管理员'
    case 'club-admin':
      return '社团管理员'
    case 'teacher-admin':
      return '指导教师'
    case 'student':
      return '学生用户'
    default:
      return '访客'
  }
}

export function getPageTitle(path: string, role?: AppRole) {
  if (path.startsWith('/clubs/') && path.endsWith('/space')) return '社团空间'
  if (path.startsWith('/clubs/')) return '社团详情'
  if (path.startsWith('/activities/')) return '活动详情'
  if (path === '/' && role === 'student') return '论坛'

  const matched = navigationItems.find((item) => item.path === path)
  return matched?.label ?? '校园社团'
}
