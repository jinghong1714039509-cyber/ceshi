import type { Directive } from 'vue'
import { useSessionStore } from '../stores/session'
import { hasPermission } from '../utils/access'

function togglePermission(el: HTMLElement, value: string | string[]) {
  const session = useSessionStore()
  if (!value) return

  el.style.display = hasPermission(session.permissions, value) ? '' : 'none'
}

export const permissionDirective: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    togglePermission(el, binding.value)
  },
  updated(el, binding) {
    togglePermission(el, binding.value)
  },
}
