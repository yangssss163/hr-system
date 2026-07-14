import http from '../http'
import type { Menu, MenuForm } from '../types'

export const getMenuTree = () => {
  return http.get<Menu[]>('/menus/tree')
}

export const getMenuById = (id: number) => {
  return http.get<Menu>(`/menus/${id}`)
}

export const createMenu = (data: MenuForm) => {
  return http.post('/menus', data)
}

export const updateMenu = (id: number, data: MenuForm) => {
  return http.put(`/menus/${id}`, data)
}

export const deleteMenu = (id: number) => {
  return http.delete(`/menus/${id}`)
}