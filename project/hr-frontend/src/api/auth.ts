import http from './http'
import type { LoginRequest, LoginResponse, UserInfo, Menu, ChangePasswordRequest, RegisterRequest } from './types'

export const login = (data: LoginRequest) => {
  return http.post<LoginResponse>('/auth/login', data)
}

export const register = (data: RegisterRequest) => {
  return http.post('/auth/register', data)
}

export const logout = () => {
  return http.post('/auth/logout')
}

export const getUserInfo = () => {
  return http.get<UserInfo>('/auth/userinfo')
}

export const getMenus = () => {
  return http.get<Menu[]>('/auth/menus')
}

export const changePassword = (data: ChangePasswordRequest) => {
  return http.put('/auth/password', data)
}