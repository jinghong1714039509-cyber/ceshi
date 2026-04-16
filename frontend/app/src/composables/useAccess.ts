import { computed } from 'vue'
import { useSessionStore } from '../stores/session'
import { hasPermission as checkPermission, hasRole as checkRole } from '../utils/access'

export function useAccess() {
  const session = useSessionStore()

  const roles = computed(() => session.roles)
  const permissions = computed(() => session.permissions)

  function hasRole(role: string | string[]) {
    return checkRole(roles.value, role)
  }

  function hasPermission(permission: string | string[]) {
    return checkPermission(permissions.value, permission)
  }

  return {
    roles,
    permissions,
    hasRole,
    hasPermission,
  }
}
