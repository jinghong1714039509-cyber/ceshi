import type { ActivitySummary } from './activity'
import type { NoticeItem } from './system'

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
  activities: ActivitySummary[]
  notices: NoticeItem[]
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
