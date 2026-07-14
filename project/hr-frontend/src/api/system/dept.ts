import http from '../http'
import type { Dept, DeptForm } from '../types'

export const getDeptTree = (params?: { companyId?: number }) => {
  return http.get<Dept[]>('/depts/tree', { params })
}

export const getDeptById = (id: number) => {
  return http.get<Dept>(`/depts/${id}`)
}

export const createDept = (data: DeptForm) => {
  return http.post('/depts', data)
}

export const updateDept = (id: number, data: DeptForm) => {
  return http.put(`/depts/${id}`, data)
}

export const deleteDept = (id: number) => {
  return http.delete(`/depts/${id}`)
}