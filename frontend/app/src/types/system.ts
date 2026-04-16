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

export interface UserItem {
  id: string
  userName: string
  name: string
  gender: string
  age: number | null
  phone: string
  address: string
  status: number
  statusLabel: string
  type: number
  roleLabel: string
  createTime: string
}

export interface RegisterPayload {
  userName: string
  password: string
  name: string
  gender: string
  age: number
  phone: string
  address: string
}

export interface AdminCreateUserPayload extends RegisterPayload {
  type: number
  status: number
}

export interface MemberItem {
  id: string
  createTime: string
  teamId: string
  teamName: string
  userId: string
  userName: string
  userGender: string
  userAge: number | null
  userPhone: string
  manager: boolean
}

export interface ProfileOverview {
  userId: string
  userName: string
  displayName: string
  role: string
  myClubs: Array<{ id: string; name: string }>
  myActivities: Array<{ id: string; name: string }>
  myApplications: Array<{ id: string; title: string; status: string }>
}
