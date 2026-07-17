import router from './index'
import { useUserStore } from '@/stores/user'

// 白名单：只有登录页不需要鉴权
const whiteList = ['/login']

/**
 * 解析 JWT token 的 payload，不做签名验证
 */
function parseJwtPayload(token: string): { exp?: number } | null {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

/**
 * 本地检查 token 是否已过期（无需请求后端）
 */
function isTokenExpired(token: string): boolean {
  const payload = parseJwtPayload(token)
  if (!payload || !payload.exp) return false
  return payload.exp * 1000 < Date.now()
}

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  if (userStore.token) {
    // token 已在本地过期，直接清除登录状态并跳转登录页
    if (isTokenExpired(userStore.token)) {
      userStore.clearAuth()
      next({ path: '/login', query: { redirect: to.path }, replace: true })
      return
    }

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
      } catch (error: any) {
        // 只有认证错误才清除登录状态，网络波动等临时错误放行
        if (error?.isAuthError) {
          userStore.clearAuth()
          next({ path: '/login', query: { redirect: to.path }, replace: true })
        } else {
          console.warn('获取用户信息失败（非认证错误），继续使用现有 token:', error)
          next()
        }
      }
      return
    }

    // 检查页面权限：路由定义了 meta.permission 但用户无权访问时，跳转 403
    const requiredPermission = to.meta?.permission as string | undefined
    if (requiredPermission && !userStore.permissions.includes(requiredPermission)) {
      next({ path: '/403', replace: true })
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
