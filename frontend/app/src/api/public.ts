import http from './http'
import type { ActivitySummary } from './activity'
import type { ClubSummary } from './club'
import type { ForumPost } from './forum'
import type { NoticeItem } from './notice'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export async function getPublicNotices() {
  const response = await http.get<ApiResponse<NoticeItem[]>>('/public/notices')
  return response.data
}

export async function getPublicClubs() {
  const response = await http.get<ApiResponse<ClubSummary[]>>('/public/clubs')
  return response.data
}

export async function getPublicActivities() {
  const response = await http.get<ApiResponse<ActivitySummary[]>>('/public/activities')
  return response.data
}

export async function getPublicForumPosts() {
  const response = await http.get<ApiResponse<ForumPost[]>>('/public/forum/posts')
  return response.data
}
