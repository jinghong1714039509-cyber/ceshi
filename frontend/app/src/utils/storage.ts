import type { MenuItem } from '../types/common'

const STORAGE_KEYS = {
  token: 'association-token',
  userId: 'association-user-id',
  role: 'association-role',
  name: 'association-name',
  avatar: 'association-avatar',
  roles: 'association-roles',
  permissions: 'association-permissions',
  menus: 'association-menus',
} as const

function parseJsonArray(value: string | null) {
  if (!value) return []

  try {
    const parsed = JSON.parse(value)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function parseMenus(value: string | null) {
  if (!value) return [] as MenuItem[]

  try {
    const parsed = JSON.parse(value)
    return Array.isArray(parsed) ? (parsed as MenuItem[]) : []
  } catch {
    return [] as MenuItem[]
  }
}

export function getStorageKey(key: keyof typeof STORAGE_KEYS) {
  return STORAGE_KEYS[key]
}

export function getStoredSession() {
  const role = localStorage.getItem(STORAGE_KEYS.role) ?? 'guest'
  const roles = parseJsonArray(localStorage.getItem(STORAGE_KEYS.roles))
  const permissions = parseJsonArray(localStorage.getItem(STORAGE_KEYS.permissions))

  return {
    token: localStorage.getItem(STORAGE_KEYS.token) ?? '',
    userId: localStorage.getItem(STORAGE_KEYS.userId) ?? '',
    role,
    roles: roles.length ? roles : role === 'guest' ? [] : [role],
    permissions,
    displayName: localStorage.getItem(STORAGE_KEYS.name) ?? '未登录',
    avatarUrl: localStorage.getItem(STORAGE_KEYS.avatar) ?? '',
    menus: parseMenus(localStorage.getItem(STORAGE_KEYS.menus)),
  }
}

export function persistSession(session: {
  token: string
  userId: string
  role: string
  roles: string[]
  permissions: string[]
  displayName: string
  avatarUrl: string
  menus: MenuItem[]
}) {
  localStorage.setItem(STORAGE_KEYS.token, session.token)
  localStorage.setItem(STORAGE_KEYS.userId, session.userId)
  localStorage.setItem(STORAGE_KEYS.role, session.role)
  localStorage.setItem(STORAGE_KEYS.roles, JSON.stringify(session.roles))
  localStorage.setItem(STORAGE_KEYS.permissions, JSON.stringify(session.permissions))
  localStorage.setItem(STORAGE_KEYS.name, session.displayName)
  localStorage.setItem(STORAGE_KEYS.avatar, session.avatarUrl)
  localStorage.setItem(STORAGE_KEYS.menus, JSON.stringify(session.menus))
}

export function persistProfile(profile: {
  role: string
  roles: string[]
  permissions: string[]
  displayName: string
  avatarUrl: string
  menus: MenuItem[]
}) {
  localStorage.setItem(STORAGE_KEYS.role, profile.role)
  localStorage.setItem(STORAGE_KEYS.roles, JSON.stringify(profile.roles))
  localStorage.setItem(STORAGE_KEYS.permissions, JSON.stringify(profile.permissions))
  localStorage.setItem(STORAGE_KEYS.name, profile.displayName)
  localStorage.setItem(STORAGE_KEYS.avatar, profile.avatarUrl)
  localStorage.setItem(STORAGE_KEYS.menus, JSON.stringify(profile.menus))
}

export function clearStoredSession() {
  Object.values(STORAGE_KEYS).forEach((key) => localStorage.removeItem(key))
}
