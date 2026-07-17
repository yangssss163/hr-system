<template>
  <div class="notice-manage">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'office:notice:create'" type="primary" @click="handleAdd">发布公告</el-button>
        <el-button @click="handleMarkAllRead" :disabled="unreadIds.length === 0">全部标记已读</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="220" :class-name="(row: Notice) => isRead(row.id) ? 'read-row' : ''">
          <template #default="{ row }">
            <span :style="{ fontWeight: isRead(row.id) ? 'normal' : 'bold', color: isRead(row.id) ? '#999' : '#333' }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.type || 'info'">{{ typeMap[row.type]?.label || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" width="300" show-overflow-tooltip />
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="!isRead(row.id)" size="small" text type="primary" @click="handleMarkRead(row)">标为已读</el-button>
            <el-button v-permission="'office:notice:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm v-if="isPublisher(row)" title="确定从库中永久删除？" @confirm="handleDelete(row)">
              <template #reference><el-button size="small" text type="danger">删除</el-button></template>
            </el-popconfirm>
            <el-button v-else size="small" text type="warning" @click="handleHide(row)">隐藏</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型" prop="type"><el-select v-model="form.type"><el-option label="通知" :value="1" /><el-option label="公告" :value="2" /><el-option label="新闻" :value="3" /></el-select></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="发布人" prop="publisherId">
          <el-select v-model="form.publisherId" filterable placeholder="选择发布人" style="width:100%">
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName} (${u.deptName || ''})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { noticeApi } from '@/api/modules/office'
import { userApi, type UserSimple } from '@/api/modules/system'
import type { Notice, NoticeForm } from '@/api/types'

const userStore = useUserStore()
const tableData = ref<Notice[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const userList = ref<UserSimple[]>([])

const STORAGE_READ = 'HR_READ_NOTICES'
const STORAGE_HIDDEN = 'HR_HIDDEN_NOTICES'

const getReadIds = (): number[] => { try { return JSON.parse(localStorage.getItem(STORAGE_READ) || '[]') } catch { return [] } }
const getHiddenIds = (): number[] => { try { return JSON.parse(localStorage.getItem(STORAGE_HIDDEN) || '[]') } catch { return [] } }
const isRead = (id: number) => getReadIds().includes(id)
const isHidden = (id: number) => getHiddenIds().includes(id)
const isPublisher = (row: Notice) => row.publisherId === userStore.userInfo?.userId
const unreadIds = computed(() => tableData.value.filter(n => !isRead(n.id) && !isHidden(n.id)).map(n => n.id))

const typeMap: Record<number, { label: string; type: string }> = {
  1: { label: '通知', type: 'info' }, 2: { label: '公告', type: 'success' }, 3: { label: '新闻', type: 'warning' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<NoticeForm>({ title: '', content: '', type: 1, status: 0, publisherId: undefined })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }], content: [{ required: true, message: '请输入内容', trigger: 'blur' }], type: [{ required: true, message: '请选择类型', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await noticeApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    const hiddenIds = getHiddenIds()
    tableData.value = (res.data.records || []).filter(n => !hiddenIds.includes(n.id))
    pagination.total = res.data.total - hiddenIds.length
  } finally { loading.value = false }
}

const loadUsers = async () => {
  try { const res = await userApi.list({ pageSize: 9999 }); userList.value = res.data?.records || [] } catch { /* */ }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { title: '', content: '', type: 1, status: 0, publisherId: undefined }); dialogVisible.value = true }
const handleEdit = (row: Notice) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { title: row.title, content: row.content, type: row.type, status: row.status, publisherId: row.publisherId }); dialogVisible.value = true }

const handleMarkRead = (row: Notice) => {
  const ids = getReadIds()
  if (!ids.includes(row.id)) { ids.push(row.id); localStorage.setItem(STORAGE_READ, JSON.stringify(ids)); loadData() }
}

const handleMarkAllRead = () => {
  const ids = getReadIds()
  for (const n of tableData.value) { if (!ids.includes(n.id)) ids.push(n.id) }
  localStorage.setItem(STORAGE_READ, JSON.stringify(ids))
  ElMessage.success('已全部标记为已读')
  loadData()
}

const handleDelete = async (row: Notice) => {
  await noticeApi.delete(row.id)
  ElMessage.success('已从库中删除')
  loadData()
}

const handleHide = (row: Notice) => {
  const ids = getHiddenIds()
  if (!ids.includes(row.id)) { ids.push(row.id); localStorage.setItem(STORAGE_HIDDEN, JSON.stringify(ids)) }
  ElMessage.success('已从列表中移除')
  loadData()
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await noticeApi.update(editId.value, form as any)
    else await noticeApi.create(form as any)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadUsers() })
</script>

<style lang="scss" scoped>
.notice-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; flex-wrap: wrap; } }
</style>
