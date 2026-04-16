import { get, post } from './request'
import type { CreateNoticePayload, NoticeItem } from '../types/system'

export type { CreateNoticePayload, NoticeItem } from '../types/system'

export function getNotices() {
  return get<NoticeItem[]>('/notices')
}

export function createNotice(payload: CreateNoticePayload) {
  return post<null, CreateNoticePayload>('/notices', payload)
}
