import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface ClubSummary {
  id: string
  name: string
  category: string
  managerId: string
  managerName: string
  totalMembers: number
  createdAt: string
  joined: boolean
  pendingApproval: boolean
  canManage: boolean
  canEnterSpace: boolean
}

export interface ClubDetail extends ClubSummary {
  activities: import('./activity').ActivitySummary[]
  notices: import('./notice').NoticeItem[]
}

export interface JoinApplication {
  id: string
  status: number
  statusLabel: string
  createdAt: string
  clubId: string
  clubName: string
  applicantId: string
  applicantName: string
  applicantPhone: string
}

export async function getClubs() {
  const response = await http.get<ApiResponse<ClubSummary[]>>('/clubs')
  return response.data
}

export async function getClubDetail(clubId: string) {
  const response = await http.get<ApiResponse<ClubDetail>>(`/clubs/${clubId}`)
  return response.data
}

export async function getPublicClubs() {
  const response = await http.get<ApiResponse<ClubSummary[]>>('/public/clubs')
  return response.data
}

export async function joinClub(clubId: string) {
  const response = await http.post<ApiResponse<null>>(`/clubs/${clubId}/join`)
  return response.data
}

export async function getMyApplications() {
  const response = await http.get<ApiResponse<JoinApplication[]>>('/join-applications/mine')
  return response.data
}

export async function withdrawApplication(applicationId: string) {
  const response = await http.delete<ApiResponse<null>>(`/join-applications/${applicationId}`)
  return response.data
}

export async function getManagedApplications() {
  const response = await http.get<ApiResponse<JoinApplication[]>>('/admin/join-applications')
  return response.data
}

export async function approveApplication(applicationId: string) {
  const response = await http.post<ApiResponse<null>>(`/admin/join-applications/${applicationId}/approve`)
  return response.data
}
