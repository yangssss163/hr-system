import router from './index'
import { useUserStore } from '@/stores/user'

// 白名单：只有登录页不需要鉴权
const whiteList = ['/login']

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  if (userStore.token) {
    // 已登录用户访问登录页，直接跳到首页
    if (to.path === '/login') {
      next({ path: '/', replace: true })
      return
    }

    // 已登录但未拉取用户信息，先获取信息并生成路由
    if (!userStore.hasRoutes) {
      try {
        await userStore.generateRoutes()
        next({ ...to, replace: true })
      } catch {
        // 获取用户信息失败（如 token 过期），清除登录状态并跳登录页
        userStore.clearAuth()
        next({ path: '/login', query: { redirect: to.path }, replace: true })
      }
      return
    }

    next()
  } else {
    // 未登录：白名单放行，否则重定向到登录页
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next({ path: '/login', query: { redirect: to.path }, replace: true })
    }
  }
})

router.afterEach(() => {})
