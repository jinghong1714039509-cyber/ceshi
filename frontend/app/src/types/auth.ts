import type { MenuItem } from './common'

export interface LoginPayload {
  userName: string
  password: string
}

export interface PermissionInfo {
  code: string
  name: string
  description?: string
}

export interface RoleInfo {
  code: string
  name: string
  permissions?: string[]
}

export interface CurrentUserPayload {
  userId: string
  userName: string
  displayName: string
  type: number
  role: string
  avatarUrl?: string
}

export interface UserInfo extends CurrentUserPayload {
  roles: string[]
  permissions: string[]
}

export interface AuthMeResponse {
  user: UserInfo
  roles: string[]
  permissions: string[]
  menus: MenuItem[]
}

export interface LoginResponse {
  token: string
  userId: string
  userName: string
  displayName: string
  role: string
  avatarUrl: string
}
