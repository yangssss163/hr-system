<template>
  <el-badge :value="taskCount" :hidden="taskCount === 0" :max="99">
    <el-dropdown trigger="click" @command="handleCommand">
      <span class="header-icon">
        <el-icon :size="18"><Flag /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <template v-if="tasks.length > 0">
            <el-dropdown-item v-for="item in tasks" :key="item.id" :command="item">
              <div class="task-item">
                <div class="task-title">
                  <span :class="['priority-dot', item.priority]" />
                  <span>{{ item.title }}</span>
                </div>
                <div class="task-meta">{{ item.statusName }} · {{ item.dueDate }}</div>
              </div>
            </el-dropdown-item>
            <el-dropdown-item divided command="all">查看全部任务</el-dropdown-item>
          </template>
          <el-dropdown-item v-else disabled>暂无待办任务</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </el-badge>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Flag } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { taskApi } from '@/api/modules/office'

const router = useRouter()
const userStore = useUserStore()
const taskCount = ref(0)
const tasks = ref<{ id: number; title: string; priority: string; statusName: string; dueDate: string }[]>([])
let timer: ReturnType<typeof setInterval> | null = null

const fetchTasks = async () => {
  try {
    const currentUserId = userStore.userInfo?.userId
    if (!currentUserId) return
    const res = await taskApi.list({ page: 1, pageSize: 5, status: 'pending,in_progress', assigneeId: currentUserId })
    const records = res.data?.records || []
    tasks.value = records.map(t => ({ id: t.id, title: t.title, priority: t.priority, statusName: t.statusName, dueDate: t.dueDate }))
    taskCount.value = res.data?.total || 0
  } catch { /* silent */ }
}

const handleCommand = (cmd: any) => {
  if (cmd === 'all') router.push('/office/task')
  else if (cmd?.id) router.push('/office/task')
}

onMounted(() => { fetchTasks(); timer = setInterval(fetchTasks, 60000) })
onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style lang="scss" scoped>
.header-icon { display: flex; align-items: center; cursor: pointer; color: #475569; &:hover { color: #1d4ed8; } }
.task-item { width: 220px; .task-title { display: flex; align-items: center; gap: 8px; font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; } .priority-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; &.high { background: #ef4444; } &.medium { background: #f59e0b; } &.low { background: #22c55e; } } .task-meta { font-size: 11px; color: #999; margin-top: 4px; } }
</style>
