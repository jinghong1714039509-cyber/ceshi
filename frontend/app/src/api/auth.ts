import http from './http'

export interface LoginPayload {
  userName: string
  password: string
}

export interface AuthUser {
  userId: string
  userName: string
  displayName: string
  type: number
  role: string
  avatarUrl: string
}

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export async function login(payload: LoginPayload) {
  const response = await http.post<ApiResponse<{
    token: string
    userId: string
    userName: string
    displayName: string
    role: string
    avatarUrl: string
  }>>('/auth/login', payload)

  return response.data
}

export async function getCurrentUser() {
  const response = await http.get<ApiResponse<AuthUser>>('/auth/me')
  return response.data
}

export async function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  const response = await http.post<ApiResponse<{ url: string; name: string }>>('/uploads/avatars', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

export async function updateAvatar(avatarUrl: string) {
  const response = await http.post<ApiResponse<AuthUser>>('/auth/avatar', { avatarUrl })
  return response.data
}
