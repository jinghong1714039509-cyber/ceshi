import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 20000,
  timeoutErrorMessage: '请求超时，请稍后重试',
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('association-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('association-token')
      window.location.href = '/login'
    }

    if (error.code === 'ECONNABORTED') {
      error.message = '请求超时，请检查后端服务或稍后重试'
    } else if (error.code === 'ERR_NETWORK') {
      error.message = '网络连接失败，请确认前后端服务已启动'
    }

    return Promise.reject(error)
  },
)

export default http
