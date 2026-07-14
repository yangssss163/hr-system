import http from '../http'
import type { PageResponse, Role, RoleForm, PageParams } from '../types'

export const getRoleList = (params?: PageParams) => {
  return http.get<PageResponse<Role>>('/roles', { params })
}

export const getAllRoles = () => {
  return http.get<Role[]>('/roles/all')
}

export const getRoleById = (id: number) => {
  return http.get<Role>(`/roles/${id}`)
}

export const createRole = (data: RoleForm) => {
  return http.post('/roles', data)
}

export const updateRole = (id: number, data: RoleForm) => {
  return http.put(`/roles/${id}`, data)
}

export const deleteRole = (id: number) => {
  return http.delete(`/roles/${id}`)
}

export const assignMenus = (id: number, data: { menuIds: number[] }) => {
  return http.put(`/roles/${id}/menus`, data)
}