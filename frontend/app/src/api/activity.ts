import { del, get, post } from './request'
import type { ActivitySignup, ActivitySummary, CreateActivityPayload } from '../types/activity'
import type { UploadResult } from '../types/common'

export type { ActivitySignup, ActivitySummary, CreateActivityPayload } from '../types/activity'

export function getActivities() {
  return get<ActivitySummary[]>('/activities')
}

export function getPublicActivities() {
  return get<ActivitySummary[]>('/public/activities')
}

export function createActivity(payload: CreateActivityPayload) {
  return post<null, CreateActivityPayload>('/activities', payload)
}

export function uploadActivityCover(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<UploadResult, FormData>('/uploads/covers', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function uploadActivityImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<UploadResult, FormData>('/uploads/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function getActivityDetail(activityId: string) {
  return get<ActivitySummary>(`/activities/${activityId}`)
}

export function submitActivityCompletion(activityId: string, payload: { summary: string; images: string[] }) {
  return post<null, { summary: string; images: string[] }>(`/activities/${activityId}/completion`, payload)
}

export function reviewActivityCompletion(activityId: string, payload: { approved: boolean; comment: string }) {
  return post<null, { approved: boolean; comment: string }>(`/activities/${activityId}/completion/review`, payload)
}

export function signupActivity(activityId: string) {
  return post<null>(`/activities/${activityId}/signup`)
}

export function cancelSignupActivity(activityId: string) {
  return del<null>(`/activities/${activityId}/signup`)
}

export function getActivitySignups(activityId: string) {
  return get<ActivitySignup[]>(`/activities/${activityId}/signups`)
}
