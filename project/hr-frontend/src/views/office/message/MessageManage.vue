<template>
  <div class="message-manage">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'office:message:create'" type="primary" @click="handleSend">发送消息</el-button>
        <template v-if="activeTab === 'inbox'">
          <el-button :disabled="unreadTotal === 0" @click="handleMarkAllRead">全部标记已读</el-button>
          <el-button type="danger" plain :disabled="readCount === 0" @click="handleClearRead">清除已读</el-button>
        </template>
      </div>

      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <el-tab-pane label="收件箱" name="inbox">
          <el-table :data="tableData" v-loading="loading">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="title" label="标题" width="220" show-overflow-tooltip />
            <el-table-column prop="content" label="内容" width="300" show-overflow-tooltip />
            <el-table-column prop="senderName" label="发送人" width="100" />
            <el-table-column prop="isRead" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.isRead === 1 ? 'success' : 'warning'">{{ row.isRead === 1 ? '已读' : '未读' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sendTime" label="发送时间" width="180" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.isRead === 0" size="small" text type="primary" @click="handleMarkRead(row)">标为已读</el-button>
                <el-button size="small" text type="danger" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="发件箱" name="outbox">
          <el-table :data="tableData" v-loading="loading">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="title" label="标题" width="220" show-overflow-tooltip />
            <el-table-column prop="content" label="内容" width="300" show-overflow-tooltip />
            <el-table-column prop="receiverName" label="接收人" width="100" />
            <el-table-column prop="isRead" label="对方状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isRead === 1 ? 'success' : 'warning'">
                  {{ row.isRead === 1 ? '对方已读' : '对方未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sendTime" label="发送时间" width="180" />
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button size="small" text type="danger" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="发送消息" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="接收人" prop="receiverId">
          <el-select v-model="form.receiverId" filterable placeholder="选择接收人" style="width:100%">
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName} (${u.deptName || ''})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { messageApi } from '@/api/modules/office'
import { userApi, type UserSimple } from '@/api/modules/system'
import type { Message, MessageForm } from '@/api/types'

const userStore = useUserStore()
const activeTab = ref<'inbox' | 'outbox'>('inbox')
const tableData = ref<Message[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const submitting = ref(false)
const userList = ref<UserSimple[]>([])

const readCount = computed(() => tableData.value.filter(m => m.isRead === 1).length)
const unreadTotal = computed(() => tableData.value.filter(m => m.isRead === 0).length)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<MessageForm>({ senderId: 0, receiverId: 0, title: '', content: '' })
const rules = {
  receiverId: [{ required: true, message: '请选择接收人', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const currentUserId = userStore.userInfo?.userId || 0
    const params: Record<string, any> = { page: pagination.page, pageSize: pagination.pageSize }
    if (activeTab.value === 'inbox') {
      params.receiverId = currentUserId
    } else {
      params.senderId = currentUserId
    }
    const res = await messageApi.list(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const onTabChange = () => {
  pagination.page = 1
  loadData()
}

const loadUsers = async () => {
  try { const res = await userApi.list({ pageSize: 9999 }); userList.value = res.data?.records || [] } catch { /* */ }
}

const handleSend = () => {
  Object.assign(form, { senderId: userStore.userInfo?.userId || 0, receiverId: 0, title: '', content: '' })
  dialogVisible.value = true
}

const handleMarkRead = async (row: Message) => {
  await messageApi.update(row.id, { senderId: row.senderId, receiverId: row.receiverId, title: row.title, content: row.content, isRead: 1 } as any)
  ElMessage.success('已标记为已读')
  loadData()
}

const handleMarkAllRead = async () => {
  const unread = tableData.value.filter(m => m.isRead === 0)
  if (unread.length === 0) { ElMessage.info('没有未读消息'); return }
  await ElMessageBox.confirm(`确定将 ${unread.length} 条未读消息标记为已读？`)
  for (const row of unread) {
    await messageApi.update(row.id, { senderId: row.senderId, receiverId: row.receiverId, title: row.title, content: row.content, isRead: 1 } as any)
  }
  ElMessage.success('已全部标记为已读')
  loadData()
}

const handleClearRead = async () => {
  const readMessages = tableData.value.filter(m => m.isRead === 1)
  if (readMessages.length === 0) { ElMessage.info('没有已读消息可清除'); return }
  await ElMessageBox.confirm(`确定清除 ${readMessages.length} 条已读消息？此操作不可恢复。`)
  for (const row of readMessages) { await messageApi.delete(row.id) }
  ElMessage.success('已清除已读消息')
  loadData()
}

const handleDelete = async (row: Message) => {
  await ElMessageBox.confirm('确定删除？')
  await messageApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    submitting.value = true
    try {
      const currentUserId = userStore.userInfo?.userId || 0
      await messageApi.create({ senderId: currentUserId, receiverId: form.receiverId, title: form.title, content: form.content } as any)
      ElMessage.success('发送成功')
      dialogVisible.value = false
      activeTab.value = 'outbox'
      loadData()
    } finally { submitting.value = false }
  })
}

onMounted(() => { loadData(); loadUsers() })
</script>

<style lang="scss" scoped>
.message-manage { .toolbar { margin-bottom: 12px; display: flex; gap: 12px; flex-wrap: wrap; } }
</style>
