import { del, get, post } from './request'
import type { ClubDetail, ClubSummary, JoinApplication } from '../types/club'

export type { ClubDetail, ClubSummary, JoinApplication } from '../types/club'

export function getClubs() {
  return get<ClubSummary[]>('/clubs')
}

export function getClubDetail(clubId: string) {
  return get<ClubDetail>(`/clubs/${clubId}`)
}

export function getPublicClubs() {
  return get<ClubSummary[]>('/public/clubs')
}

export function joinClub(clubId: string) {
  return post<null>(`/clubs/${clubId}/join`)
}

export function getMyApplications() {
  return get<JoinApplication[]>('/join-applications/mine')
}

export function withdrawApplication(applicationId: string) {
  return del<null>(`/join-applications/${applicationId}`)
}

export function getManagedApplications() {
  return get<JoinApplication[]>('/admin/join-applications')
}

export function approveApplication(applicationId: string) {
  return post<null>(`/admin/join-applications/${applicationId}/approve`)
}
