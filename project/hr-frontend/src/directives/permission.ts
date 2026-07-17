import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * v-permission 权限指令
 * 用法:
 *   v-permission="'system:user:create'"          // 单个权限
 *   v-permission="['system:user:create', 'system:user:update']"  // 满足任一即可
 *
 * 无权限时移除元素。
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const userStore = useUserStore()
    const required = binding.value as string | string[]

    if (!required) return

    const permissions = Array.isArray(required) ? required : [required]
    const hasPermission = permissions.some(p => userStore.permissions.includes(p))

    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}
