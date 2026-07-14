import http from '../http'
import type { PageResponse, User, UserForm, PageParams } from '../types'

export const getUserList = (params?: PageParams & { keyword?: string; deptId?: number; status?: number }) => {
  return http.get<PageResponse<User>>('/users', { params })
}

export const getUserById = (id: number) => {
  return http.get<User>(`/users/${id}`)
}

export const createUser = (data: UserForm) => {
  return http.post('/users', data)
}

export const updateUser = (id: number, data: UserForm) => {
  return http.put(`/users/${id}`, data)
}

export const deleteUser = (id: number) => {
  return http.delete(`/users/${id}`)
}

export const assignRoles = (id: number, data: { roleIds: number[] }) => {
  return http.put(`/users/${id}/roles`, data)
}