import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface NoticeItem {
  id: string
  title: string
  detail: string
  createTime: string
  teamId: string
  teamName: string
  systemNotice: boolean
}

export interface CreateNoticePayload {
  title: string
  detail: string
  teamId?: string
}

export async function getNotices() {
  const response = await http.get<ApiResponse<NoticeItem[]>>('/notices')
  return response.data
}

export async function createNotice(payload: CreateNoticePayload) {
  const response = await http.post<ApiResponse<null>>('/notices', payload)
  return response.data
}
