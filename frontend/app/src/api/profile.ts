import { get } from './request'
import type { ProfileOverview } from '../types/system'

export type { ProfileOverview } from '../types/system'

export function getProfileOverview() {
  return get<ProfileOverview>('/profile/overview')
}
