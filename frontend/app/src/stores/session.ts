import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getCurrentUser } from '../api/auth'
import type { MenuItem } from '../types/common'
import {
  clearStoredSession,
  getStoredSession,
  persistProfile,
  persistSession,
} from '../utils/storage'
import { buildMenusFromRoutes, createUserInfoFromCurrentUser } from '../utils/access'
import { getAppRoutes } from '../router'

export const useSessionStore = defineStore('session', () => {
  const stored = getStoredSession()

  const token = ref(stored.token)
  const userId = ref(stored.userId)
  const role = ref(stored.role)
  const roles = ref<string[]>(stored.roles)
  const permissions = ref<string[]>(stored.permissions)
  const displayName = ref(stored.displayName)
  const avatarUrl = ref(stored.avatarUrl)
  const menus = ref<MenuItem[]>(stored.menus)
  const initialized = ref(false)

  const isAuthenticated = computed(() => Boolean(token.value))

  function syncSession() {
    persistSession({
      token: token.value,
      userId: userId.value,
      role: role.value,
      roles: roles.value,
      permissions: permissions.value,
      displayName: displayName.value,
      avatarUrl: avatarUrl.value,
      menus: menus.value,
    })
  }

  function setMenus(nextMenus: MenuItem[]) {
    menus.value = nextMenus
    syncSession()
  }

  function setSession(
    nextToken: string,
    nextUserId: string,
    nextRole: string,
    nextName: string,
    nextAvatarUrl = '',
    nextRoles: string[] = nextRole ? [nextRole] : [],
    nextPermissions: string[] = [],
    nextMenus: MenuItem[] = [],
  ) {
    token.value = nextToken
    userId.value = nextUserId
    role.value = nextRole
    roles.value = nextRoles
    permissions.value = nextPermissions
    displayName.value = nextName
    avatarUrl.value = nextAvatarUrl
    menus.value = nextMenus

    syncSession()
  }

  function setProfile(
    nextRole: string,
    nextName: string,
    nextAvatarUrl = avatarUrl.value,
    nextRoles = roles.value,
    nextPermissions = permissions.value,
    nextMenus = menus.value,
  ) {
    role.value = nextRole
    roles.value = nextRoles
    permissions.value = nextPermissions
    displayName.value = nextName
    avatarUrl.value = nextAvatarUrl
    menus.value = nextMenus

    persistProfile({
      role: nextRole,
      roles: nextRoles,
      permissions: nextPermissions,
      displayName: nextName,
      avatarUrl: nextAvatarUrl,
      menus: nextMenus,
    })
  }

  function setAvatar(nextAvatarUrl: string) {
    avatarUrl.value = nextAvatarUrl
    persistProfile({
      role: role.value,
      roles: roles.value,
      permissions: permissions.value,
      displayName: displayName.value,
      avatarUrl: nextAvatarUrl,
      menus: menus.value,
    })
  }

  async function bootstrapSession() {
    if (!token.value) {
      initialized.value = true
      return null
    }

    const result = await getCurrentUser()
    const user = createUserInfoFromCurrentUser(result.data)
    const nextMenus = buildMenusFromRoutes(getAppRoutes(), user.roles, user.permissions)

    userId.value = user.userId
    role.value = user.role
    roles.value = user.roles
    permissions.value = user.permissions
    displayName.value = user.displayName
    avatarUrl.value = user.avatarUrl || ''
    menus.value = nextMenus
    initialized.value = true

    syncSession()
    return user
  }

  function clearSession() {
    token.value = ''
    userId.value = ''
    role.value = 'guest'
    roles.value = []
    permissions.value = []
    displayName.value = '未登录'
    avatarUrl.value = ''
    menus.value = []
    initialized.value = true
    clearStoredSession()
  }

  return {
    token,
    userId,
    role,
    roles,
    permissions,
    displayName,
    avatarUrl,
    menus,
    initialized,
    isAuthenticated,
    setMenus,
    setSession,
    setProfile,
    setAvatar,
    bootstrapSession,
    clearSession,
  }
})
