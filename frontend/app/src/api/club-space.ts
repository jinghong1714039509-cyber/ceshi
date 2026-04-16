import http from './http'
import type { ActivitySummary } from './activity'
import type { NoticeItem } from './notice'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface ClubPost {
  id: string
  title: string
  content: string
  pinned: boolean
  createTime: string
  authorName: string
}

export interface ClubMessage {
  id: string
  authorId: string
  content: string
  createTime: string
  authorName: string
}

export interface ClubSpace {
  clubId: string
  clubName: string
  category: string
  managerName: string
  memberCount: number
  canManage: boolean
  activities: ActivitySummary[]
  notices: NoticeItem[]
  posts: ClubPost[]
  messages: ClubMessage[]
}

export async function getClubSpace(clubId: string) {
  const response = await http.get<ApiResponse<ClubSpace>>(`/clubs/${clubId}/space`)
  return response.data
}

export async function createClubPost(clubId: string, payload: { title: string; content: string }) {
  const response = await http.post<ApiResponse<null>>(`/clubs/${clubId}/space/posts`, payload)
  return response.data
}

export async function createClubMessage(clubId: string, payload: { content: string }) {
  const response = await http.post<ApiResponse<null>>(`/clubs/${clubId}/space/messages`, payload)
  return response.data
}
