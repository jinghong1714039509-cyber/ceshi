import type { AxiosRequestConfig } from 'axios'
import http from './http'
import type { ApiResponse } from '../types/common'

export function get<T>(url: string, config?: AxiosRequestConfig) {
  return http.get<never, ApiResponse<T>>(url, config)
}

export function post<T, P = unknown>(url: string, payload?: P, config?: AxiosRequestConfig) {
  return http.post<never, ApiResponse<T>>(url, payload, config)
}

export function patch<T, P = unknown>(url: string, payload?: P, config?: AxiosRequestConfig) {
  return http.patch<never, ApiResponse<T>>(url, payload, config)
}

export function del<T>(url: string, config?: AxiosRequestConfig) {
  return http.delete<never, ApiResponse<T>>(url, config)
}
