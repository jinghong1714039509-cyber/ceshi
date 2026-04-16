import axios, { type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse, BackendApiResponse } from '../types/common'
import { clearStoredSession, getStorageKey } from '../utils/storage'

function normalizeResponse<T>(payload: BackendApiResponse<T>): ApiResponse<T> {
  return {
    code: payload.code,
    data: payload.data,
    message: payload.message ?? payload.msg ?? '',
    msg: payload.msg ?? payload.message ?? '',
  }
}

const http = axios.create({
  baseURL: '/api',
  timeout: 20000,
  timeoutErrorMessage: '请求超时，请稍后重试',
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(getStorageKey('token'))
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => normalizeResponse(response.data) as never,
  (error: AxiosError<BackendApiResponse<unknown>>) => {
    const status = error.response?.status
    const responseMessage = error.response?.data?.message ?? error.response?.data?.msg

    if (status === 401) {
      clearStoredSession()
      ElMessage.error(responseMessage || '登录状态已失效，请重新登录')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else if (status === 403) {
      ElMessage.error(responseMessage || '当前账号没有权限执行此操作')
    } else if (status === 500) {
      ElMessage.error(responseMessage || '服务异常，请稍后再试')
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查服务状态后重试')
    } else if (error.code === 'ERR_NETWORK') {
      ElMessage.error('网络连接失败，服务当前不可用')
    } else if (responseMessage) {
      ElMessage.error(responseMessage)
    }

    return Promise.reject(error)
  },
)

export default http
