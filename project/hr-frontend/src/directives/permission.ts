import type { Directive, DirectiveBinding } from 'vue'
import { watch } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * v-permission 权限指令（响应式）
 * 用法:
 *   v-permission="'system:user:create'"          // 单个权限
 *   v-permission="['system:user:create', 'system:user:edit']"  // 满足任一即可
 *
 * 无权限时隐藏元素（display:none），权限就绪后自动恢复显示。
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const userStore = useUserStore()
    const required = binding.value as string | string[]

    if (!required) return

    const permissions = Array.isArray(required) ? required : [required]

    const checkPermission = () => {
      const hasPermission = permissions.some(p => userStore.permissions.includes(p))
      el.style.display = hasPermission ? '' : 'none'
    }

    checkPermission()

    // 响应式监听权限变化（如登录后异步加载权限）
    const unwatch = watch(
      () => [...userStore.permissions],
      () => checkPermission()
    )

    // 存到元素上以便 unmounted 时清理
    ;(el as any).__permission_unwatch = unwatch
  },

  unmounted(el: HTMLElement) {
    const unwatch = (el as any).__permission_unwatch
    if (unwatch) {
      unwatch()
    }
  }
}
