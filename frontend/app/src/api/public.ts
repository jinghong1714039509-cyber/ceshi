import { get } from './request'
import type { ActivitySummary } from '../types/activity'
import type { ClubSummary } from '../types/club'
import type { ForumFeed, ForumPost } from '../types/forum'
import type { NoticeItem } from '../types/system'

export function getPublicNotices() {
  return get<NoticeItem[]>('/public/notices')
}

export function getPublicClubs() {
  return get<ClubSummary[]>('/public/clubs')
}

export function getPublicActivities() {
  return get<ActivitySummary[]>('/public/activities')
}

export function getPublicForumPosts() {
  return get<ForumPost[]>('/public/forum/posts')
}

export function getPublicForumFeed() {
  return get<ForumFeed>('/public/forum/feed')
}
