<template>
  <el-badge :value="unreadNoticeCount" :hidden="unreadNoticeCount === 0" :max="99">
    <el-dropdown trigger="click" @command="handleCommand">
      <span class="header-icon">
        <el-icon :size="18"><ChatLineSquare /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <template v-if="notices.length > 0">
            <el-dropdown-item v-for="item in notices" :key="item.id" :command="item">
              <div class="notice-item" :class="{ 'is-read': isRead(item.id) }">
                <div class="notice-title">
                  <el-tag :type="typeMap[item.type]?.tag || 'info'" size="small">{{ typeMap[item.type]?.label || '未知' }}</el-tag>
                  <span>{{ item.title }}</span>
                </div>
                <div class="notice-time">{{ item.publishTime?.slice(0, 10) }}</div>
              </div>
            </el-dropdown-item>
            <el-dropdown-item divided command="all">查看全部公告</el-dropdown-item>
          </template>
          <el-dropdown-item v-else disabled>暂无公告</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </el-badge>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ChatLineSquare } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { noticeApi } from '@/api/modules/office'

const router = useRouter()
const userStore = useUserStore()
const unreadNoticeCount = ref(0)
const notices = ref<{ id: number; title: string; type: number; publishTime: string }[]>([])
let timer: ReturnType<typeof setInterval> | null = null

const STORAGE_KEY = 'HR_READ_NOTICES'

const getReadNotices = (): number[] => {
  try { return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]'); } catch { return [] }
}
const isRead = (id: number) => getReadNotices().includes(id)

const typeMap: Record<number, { label: string; tag: string }> = {
  1: { label: '通知', tag: 'info' }, 2: { label: '公告', tag: 'success' }, 3: { label: '新闻', tag: 'warning' }
}

const fetchNotices = async () => {
  try {
    const res = await noticeApi.list({ page: 1, pageSize: 5, status: 1 })
    const records = res.data?.records || []
    notices.value = records.map(n => ({ id: n.id, title: n.title, type: n.type, publishTime: n.publishTime }))
    const readIds = getReadNotices()
    unreadNoticeCount.value = records.filter(n => !readIds.includes(n.id)).length
  } catch { /* silent */ }
}

const handleCommand = (cmd: any) => {
  if (cmd === 'all') { router.push('/office/notice'); return }
  if (cmd?.id) {
    const read = getReadNotices()
    if (!read.includes(cmd.id)) {
      read.push(cmd.id)
      localStorage.setItem(STORAGE_KEY, JSON.stringify(read))
      unreadNoticeCount.value = Math.max(0, unreadNoticeCount.value - 1)
    }
    router.push('/office/notice')
  }
}

onMounted(() => { fetchNotices(); timer = setInterval(fetchNotices, 60000) })
onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style lang="scss" scoped>
.header-icon { display: flex; align-items: center; cursor: pointer; color: #475569; &:hover { color: #1d4ed8; } }
.notice-item { width: 240px; .notice-title { display: flex; align-items: center; gap: 8px; font-size: 13px; span { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; } } .notice-time { font-size: 11px; color: #999; margin-top: 4px; } &.is-read { opacity: 0.5; } }
</style>
