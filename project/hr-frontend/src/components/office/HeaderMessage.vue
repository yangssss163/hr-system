<template>
  <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
    <el-dropdown trigger="click" @command="handleCommand">
      <span class="header-icon">
        <el-icon :size="18"><Bell /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <template v-if="messages.length > 0">
            <el-dropdown-item v-for="msg in messages" :key="msg.id" :command="msg">
              <div class="msg-item">
                <div class="msg-title"><span v-if="msg.isRead === 0" class="unread-dot" />{{ msg.title }}</div>
                <div class="msg-meta">{{ msg.senderName }} · {{ msg.sendTime?.slice(0, 10) }}</div>
              </div>
            </el-dropdown-item>
            <el-dropdown-item divided command="all">查看全部消息</el-dropdown-item>
          </template>
          <el-dropdown-item v-else disabled>暂无消息</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </el-badge>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { messageApi } from '@/api/modules/office'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)
const messages = ref<{ id: number; title: string; isRead: number; senderName: string; sendTime: string }[]>([])
let timer: ReturnType<typeof setInterval> | null = null

const fetchMessages = async () => {
  try {
    const currentUserId = userStore.userInfo?.userId
    if (!currentUserId) return
    const res = await messageApi.list({ page: 1, pageSize: 5, receiverId: currentUserId })
    const records = res.data?.records || []
    messages.value = records.map(m => ({ id: m.id, title: m.title, isRead: m.isRead, senderName: m.senderName, sendTime: m.sendTime }))
    unreadCount.value = records.filter(m => m.isRead === 0).length
  } catch { /* silent */ }
}

const handleCommand = (cmd: any) => {
  if (cmd === 'all') router.push('/office/message')
  else if (cmd?.id) router.push(`/office/message`)
}

onMounted(() => { fetchMessages(); timer = setInterval(fetchMessages, 30000) })
onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style lang="scss" scoped>
.header-icon { display: flex; align-items: center; cursor: pointer; color: #475569; &:hover { color: #1d4ed8; } }
.msg-item { width: 240px; .msg-title { font-size: 13px; color: #333; display: flex; align-items: center; gap: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; } .unread-dot { width: 6px; height: 6px; background: #ef4444; border-radius: 50%; flex-shrink: 0; } .msg-meta { font-size: 11px; color: #999; margin-top: 2px; } }
</style>
