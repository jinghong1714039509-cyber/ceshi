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
