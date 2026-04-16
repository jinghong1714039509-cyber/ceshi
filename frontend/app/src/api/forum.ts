import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface ForumComment {
  id: string
  content: string
  createTime: string
  authorId: string
  authorName: string
  authorAvatar: string
  authorRole: string
  mine: boolean
}

export interface ForumPost {
  id: string
  title: string
  content: string
  coverImage: string
  createTime: string
  authorId: string
  authorName: string
  authorAvatar: string
  authorRole: string
  topicTag: string
  ownPost: boolean
  liked: boolean
  likeCount: number
  commentCount: number
  recentComments: ForumComment[]
}

export interface ForumTopic {
  topicTag: string
  postCount: number
}

export interface ForumFeed {
  posts: ForumPost[]
  hotTopics: ForumTopic[]
}

export interface LikeToggleResult {
  liked: boolean
  likeCount: number
}

export async function getForumPosts() {
  const response = await http.get<ApiResponse<ForumPost[]>>('/forum/posts')
  return response.data
}

export async function getPublicForumPosts() {
  const response = await http.get<ApiResponse<ForumPost[]>>('/public/forum/posts')
  return response.data
}

export async function getForumFeed() {
  const response = await http.get<ApiResponse<ForumFeed>>('/forum/feed')
  return response.data
}

export async function getPublicForumFeed() {
  const response = await http.get<ApiResponse<ForumFeed>>('/public/forum/feed')
  return response.data
}

export async function createForumPost(payload: { title?: string; content: string; coverImage?: string; topicTag?: string }) {
  const response = await http.post<ApiResponse<null>>('/forum/posts', payload)
  return response.data
}

export async function createForumComment(postId: string, payload: { content: string }) {
  const response = await http.post<ApiResponse<null>>(`/forum/posts/${postId}/comments`, payload)
  return response.data
}

export async function toggleForumLike(postId: string) {
  const response = await http.post<ApiResponse<LikeToggleResult>>(`/forum/posts/${postId}/likes`)
  return response.data
}
