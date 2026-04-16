import type { Component } from 'vue'
import {
  Bell,
  Calendar,
  ChatDotRound,
  Collection,
  DataBoard,
  EditPen,
  House,
  Message,
  Setting,
  UserFilled,
} from '@element-plus/icons-vue'
import type { MenuItem } from './types/common'
import { getRoleLabel } from './utils/access'

export const APP_NAME = 'Campus Clubs'
export const APP_TITLE = '校园社团'

export const PUBLIC_ROUTE_NAMES = ['login', 'register', 'explore']

const MENU_ICON_MAP: Record<string, Component> = {
  dashboard: House,
  forum: EditPen,
  activities: Calendar,
  clubs: Collection,
  profile: ChatDotRound,
  messages: Message,
  notices: Bell,
  members: UserFilled,
  users: Setting,
  approvals: DataBoard,
}

const ROUTE_ICON_ALIAS: Record<string, keyof typeof MENU_ICON_MAP> = {
  house: 'dashboard',
  editpen: 'forum',
  calendar: 'activities',
  collection: 'clubs',
  chatdotround: 'profile',
  message: 'messages',
  bell: 'notices',
  userfilled: 'members',
  setting: 'users',
  databoard: 'approvals',
}

export function getIconComponent(icon?: string, fallback = 'dashboard') {
  if (!icon) {
    return MENU_ICON_MAP[fallback] ?? House
  }

  const normalized = icon.replace(/[-_\s]/g, '').toLowerCase()
  const alias = ROUTE_ICON_ALIAS[normalized] ?? normalized
  return MENU_ICON_MAP[alias] ?? MENU_ICON_MAP[fallback] ?? House
}

export function getAvatarText(displayName: string) {
  return displayName.trim().slice(0, 1).toUpperCase() || 'U'
}

export function getDefaultLandingPath(role: string) {
  return ['super-admin', 'club-admin', 'teacher-admin'].includes(role) ? '/' : '/forum'
}

export function getHeaderTitle(routeTitle?: string) {
  return routeTitle || APP_TITLE
}

export function getVisibleMenus(menus: MenuItem[]) {
  return menus.filter((item) => !item.hidden)
}

export { getRoleLabel }
