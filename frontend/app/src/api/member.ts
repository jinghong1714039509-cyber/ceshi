import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
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

export async function getMembers() {
  const response = await http.get<ApiResponse<MemberItem[]>>('/members')
  return response.data
}

export async function removeMember(memberId: string) {
  const response = await http.delete<ApiResponse<null>>(`/members/${memberId}`)
  return response.data
}
