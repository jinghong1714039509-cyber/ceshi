import { get, patch, post } from './request'
import type { AdminCreateUserPayload, RegisterPayload, UserItem } from '../types/system'

export type { AdminCreateUserPayload, RegisterPayload, UserItem } from '../types/system'

export function registerUser(payload: RegisterPayload) {
  return post<null, RegisterPayload>('/users/register', payload)
}

export function getUsers() {
  return get<UserItem[]>('/users')
}

export function createUser(payload: AdminCreateUserPayload) {
  return post<null, AdminCreateUserPayload>('/users', payload)
}

export function updateUserStatus(userId: string, status: number) {
  return patch<null, { status: number }>(`/users/${userId}/status`, { status })
}
