import http from '../http'
import type { PageResponse, Position, PositionForm, PageParams } from '../types'

export const getPositionList = (params?: PageParams & { deptId?: number; keyword?: string }) => {
  return http.get<PageResponse<Position>>('/positions', { params })
}

export const getPositionById = (id: number) => {
  return http.get<Position>(`/positions/${id}`)
}

export const createPosition = (data: PositionForm) => {
  return http.post('/positions', data)
}

export const updatePosition = (id: number, data: PositionForm) => {
  return http.put(`/positions/${id}`, data)
}

export const deletePosition = (id: number) => {
  return http.delete(`/positions/${id}`)
}