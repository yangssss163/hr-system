import http from '../http'
import type { PageResponse } from '../types'

export interface UserSimple {
  id: number
  username: string
  realName: string
  deptId: number
  deptName: string
}

export const userApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<UserSimple>>('/users', { params })
}
