import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

export const useSessionStore = defineStore('session', () => {
  const token = ref(localStorage.getItem('association-token') ?? '')
  const userId = ref(localStorage.getItem('association-user-id') ?? '')
  const role = ref(localStorage.getItem('association-role') ?? 'guest')
  const displayName = ref(localStorage.getItem('association-name') ?? '未登录')
  const avatarUrl = ref(localStorage.getItem('association-avatar') ?? '')

  const isAuthenticated = computed(() => Boolean(token.value))

  function setSession(nextToken: string, nextUserId: string, nextRole: string, nextName: string, nextAvatarUrl = '') {
    token.value = nextToken
    userId.value = nextUserId
    role.value = nextRole
    displayName.value = nextName
    avatarUrl.value = nextAvatarUrl
    localStorage.setItem('association-token', nextToken)
    localStorage.setItem('association-user-id', nextUserId)
    localStorage.setItem('association-role', nextRole)
    localStorage.setItem('association-name', nextName)
    localStorage.setItem('association-avatar', nextAvatarUrl)
  }

  function setProfile(nextRole: string, nextName: string, nextAvatarUrl = avatarUrl.value) {
    role.value = nextRole
    displayName.value = nextName
    avatarUrl.value = nextAvatarUrl
    localStorage.setItem('association-role', nextRole)
    localStorage.setItem('association-name', nextName)
    localStorage.setItem('association-avatar', nextAvatarUrl)
  }

  function setAvatar(nextAvatarUrl: string) {
    avatarUrl.value = nextAvatarUrl
    localStorage.setItem('association-avatar', nextAvatarUrl)
  }

  function clearSession() {
    token.value = ''
    userId.value = ''
    role.value = 'guest'
    displayName.value = '未登录'
    avatarUrl.value = ''
    localStorage.removeItem('association-token')
    localStorage.removeItem('association-user-id')
    localStorage.removeItem('association-role')
    localStorage.removeItem('association-name')
    localStorage.removeItem('association-avatar')
  }

  return {
    token,
    userId,
    role,
    displayName,
    avatarUrl,
    isAuthenticated,
    setSession,
    setProfile,
    setAvatar,
    clearSession,
  }
})
