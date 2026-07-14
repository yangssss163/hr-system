import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/auth'
import type { LoginRequest, UserInfo as UserInfoType, Menu } from '@/api/types'
import router from '@/router'
import { asyncRoutes } from '@/router/routes'

interface UserState {
  token: string
  userInfo: UserInfoType | null
  menus: Menu[]
  permissions: string[]
  hasRoutes: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
    menus: [],
    permissions: [],
    hasRoutes: false
  }),

  actions: {
    async login(data: LoginRequest) {
      const res = await loginApi(data)
      this.token = res.data.token
      localStorage.setItem('token', this.token)
    },

    async getUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
      this.permissions = res.data.permissions || []
      return { menus: this.menus, permissions: this.permissions }
    },

    async generateRoutes() {
      await this.getUserInfo()
      asyncRoutes.forEach(route => {
        router.addRoute(route)
      })
      this.hasRoutes = true
    },

    clearAuth() {
      this.token = ''
      this.userInfo = null
      this.menus = []
      this.permissions = []
      this.hasRoutes = false
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },

    async logout() {
      try {
        await logoutApi()
      } catch {
        // 忽略 API 错误，确保退出登录流程不受影响
      }
      this.clearAuth()
      router.replace('/login').catch(() => {})
    }
  }
})