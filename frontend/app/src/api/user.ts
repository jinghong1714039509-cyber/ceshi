import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
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

export interface AdminCreateUserPayload extends RegisterPayload {
  type: number
  status: number
}

export async function registerUser(payload: RegisterPayload) {
  const response = await http.post<ApiResponse<null>>('/users/register', payload)
  return response.data
}

export async function getUsers() {
  const response = await http.get<ApiResponse<UserItem[]>>('/users')
  return response.data
}

export async function createUser(payload: AdminCreateUserPayload) {
  const response = await http.post<ApiResponse<null>>('/users', payload)
  return response.data
}

export async function updateUserStatus(userId: string, status: number) {
  const response = await http.patch<ApiResponse<null>>(`/users/${userId}/status`, { status })
  return response.data
}
