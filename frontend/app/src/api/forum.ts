import { get, post } from './request'
import type { ForumFeed, ForumPost, LikeToggleResult } from '../types/forum'

export type { ForumComment, ForumFeed, ForumPost, ForumTopic, LikeToggleResult } from '../types/forum'

export function getForumPosts() {
  return get<ForumPost[]>('/forum/posts')
}

export function getPublicForumPosts() {
  return get<ForumPost[]>('/public/forum/posts')
}

export function getForumFeed() {
  return get<ForumFeed>('/forum/feed')
}

export function getPublicForumFeed() {
  return get<ForumFeed>('/public/forum/feed')
}

export function createForumPost(payload: { title?: string; content: string; coverImage?: string; topicTag?: string }) {
  return post<null, { title?: string; content: string; coverImage?: string; topicTag?: string }>('/forum/posts', payload)
}

export function createForumComment(postId: string, payload: { content: string }) {
  return post<null, { content: string }>(`/forum/posts/${postId}/comments`, payload)
}

export function toggleForumLike(postId: string) {
  return post<LikeToggleResult>(`/forum/posts/${postId}/likes`)
}
