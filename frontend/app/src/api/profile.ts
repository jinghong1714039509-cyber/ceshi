import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface ProfileOverview {
  userId: string
  userName: string
  displayName: string
  role: string
  myClubs: any[]
  myActivities: any[]
  myApplications: any[]
}

export async function getProfileOverview() {
  const response = await http.get<ApiResponse<ProfileOverview>>('/profile/overview')
  return response.data
}
