import { del, get } from './request'
import type { MemberItem } from '../types/system'

export type { MemberItem } from '../types/system'

export function getMembers() {
  return get<MemberItem[]>('/members')
}

export function removeMember(memberId: string) {
  return del<null>(`/members/${memberId}`)
}
