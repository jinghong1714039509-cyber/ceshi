import { get, post } from './request'
import type { CurrentUserPayload, LoginPayload, LoginResponse } from '../types/auth'
import type { UploadResult } from '../types/common'

export type {
  CurrentUserPayload,
  LoginPayload,
  LoginResponse,
} from '../types/auth'

export function login(payload: LoginPayload) {
  return post<LoginResponse, LoginPayload>('/auth/login', payload)
}

export function getCurrentUser() {
  return get<CurrentUserPayload>('/auth/me')
}

export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<UploadResult, FormData>('/uploads/avatars', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function updateAvatar(avatarUrl: string) {
  return post<CurrentUserPayload, { avatarUrl: string }>('/auth/avatar', { avatarUrl })
}
