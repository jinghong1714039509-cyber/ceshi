import { get, post } from './request'
import type { ClubSpace } from '../types/club'

export type { ClubSpace, ClubMessage, ClubPost } from '../types/club'

export function getClubSpace(clubId: string) {
  return get<ClubSpace>(`/clubs/${clubId}/space`)
}

export function createClubPost(clubId: string, payload: { title: string; content: string }) {
  return post<null, { title: string; content: string }>(`/clubs/${clubId}/space/posts`, payload)
}

export function createClubMessage(clubId: string, payload: { content: string }) {
  return post<null, { content: string }>(`/clubs/${clubId}/space/messages`, payload)
}
