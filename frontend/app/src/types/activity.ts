export interface ActivitySummary {
  id: string
  name: string
  summary: string
  detail: string
  requirement: string
  total: number
  activeTime: string
  location: string
  capacity: number
  coverImage: string
  clubId: string
  clubName: string
  joined: boolean
  manageable: boolean
  completionStatus: number
  completionStatusLabel: string
  completionSummary: string
  completionImages: string[]
  completionSubmittedAt: string
  completionReviewComment: string
  completionReviewedAt: string
  canSubmitCompletion: boolean
  canReviewCompletion: boolean
}

export interface ActivitySignup {
  id: string
  createTime: string
  activeId: string
  userId: string
  userName: string
  userGender: string
  userPhone: string
}

export interface CreateActivityPayload {
  clubId: string
  name: string
  summary: string
  detail: string
  requirement: string
  activeTime: string
  location: string
  capacity: number
  coverImage?: string
}
