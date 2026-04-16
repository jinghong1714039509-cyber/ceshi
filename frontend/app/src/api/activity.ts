import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface ActivitySummary {
  id: string
  name: string
  summary: string
  detail: string
  requirement: string
  total: number
  activeTime: string
  location: string
  capacity: number
  coverImage: string
  clubId: string
  clubName: string
  joined: boolean
  manageable: boolean
  completionStatus: number
  completionStatusLabel: string
  completionSummary: string
  completionImages: string[]
  completionSubmittedAt: string
  completionReviewComment: string
  completionReviewedAt: string
  canSubmitCompletion: boolean
  canReviewCompletion: boolean
}

export interface ActivitySignup {
  id: string
  createTime: string
  activeId: string
  userId: string
  userName: string
  userGender: string
  userPhone: string
}

export interface CreateActivityPayload {
  clubId: string
  name: string
  summary: string
  detail: string
  requirement: string
  activeTime: string
  location: string
  capacity: number
  coverImage?: string
}

export async function getActivities() {
  const response = await http.get<ApiResponse<ActivitySummary[]>>('/activities')
  return response.data
}

export async function getPublicActivities() {
  const response = await http.get<ApiResponse<ActivitySummary[]>>('/public/activities')
  return response.data
}

export async function createActivity(payload: CreateActivityPayload) {
  const response = await http.post<ApiResponse<null>>('/activities', payload)
  return response.data
}

export async function uploadActivityCover(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  const response = await http.post<ApiResponse<{ url: string; name: string }>>('/uploads/covers', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

export async function uploadActivityImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  const response = await http.post<ApiResponse<{ url: string; name: string }>>('/uploads/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

export async function getActivityDetail(activityId: string) {
  const response = await http.get<ApiResponse<ActivitySummary>>(`/activities/${activityId}`)
  return response.data
}

export async function submitActivityCompletion(activityId: string, payload: { summary: string; images: string[] }) {
  const response = await http.post<ApiResponse<null>>(`/activities/${activityId}/completion`, payload)
  return response.data
}

export async function reviewActivityCompletion(activityId: string, payload: { approved: boolean; comment: string }) {
  const response = await http.post<ApiResponse<null>>(`/activities/${activityId}/completion/review`, payload)
  return response.data
}

export async function signupActivity(activityId: string) {
  const response = await http.post<ApiResponse<null>>(`/activities/${activityId}/signup`)
  return response.data
}

export async function cancelSignupActivity(activityId: string) {
  const response = await http.delete<ApiResponse<null>>(`/activities/${activityId}/signup`)
  return response.data
}

export async function getActivitySignups(activityId: string) {
  const response = await http.get<ApiResponse<ActivitySignup[]>>(`/activities/${activityId}/signups`)
  return response.data
}
