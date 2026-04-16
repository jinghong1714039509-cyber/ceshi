export interface ApiResponse<T> {
  code: number
  data: T
  message: string
  msg?: string
}

export interface BackendApiResponse<T> {
  code: number
  data: T
  msg?: string
  message?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface UploadResult {
  url: string
  name: string
}

export interface OptionItem {
  label: string
  value: string | number
}

export interface MenuItem {
  name: string
  title: string
  path: string
  icon?: string
  permission?: string
  hidden?: boolean
  children?: MenuItem[]
}
