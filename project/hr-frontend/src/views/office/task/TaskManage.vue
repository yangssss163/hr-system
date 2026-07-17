<template>
  <div class="task-manage">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'office:task:create'" type="primary" @click="handleAdd">创建任务</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="任务标题" width="200" />
        <el-table-column prop="content" label="内容" width="250" show-overflow-tooltip />
        <el-table-column prop="assigneeName" label="负责人" width="100" />
        <el-table-column prop="priorityName" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="row.priority === 'high' ? 'danger' : row.priority === 'medium' ? 'warning' : 'success'">{{ row.priorityName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : row.status === 'in_progress' ? 'warning' : 'info'">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="截止日期" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-permission="'office:task:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'office:task:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '创建任务'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="任务标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="创建人">
          <el-select v-model="form.creatorId" filterable placeholder="选择创建人" style="width:100%">
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName} (${u.deptName || ''})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="assigneeId">
          <el-select v-model="form.assigneeId" filterable placeholder="选择负责人" style="width:100%">
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName} (${u.deptName || ''})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority"><el-select v-model="form.priority"><el-option label="低" value="low" /><el-option label="中" value="medium" /><el-option label="高" value="high" /></el-select></el-form-item>
        <el-form-item label="状态" prop="status"><el-select v-model="form.status"><el-option label="待处理" value="pending" /><el-option label="进行中" value="in_progress" /><el-option label="已完成" value="completed" /></el-select></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="截止日期" prop="dueDate"><el-date-picker v-model="form.dueDate" type="date" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { taskApi } from '@/api/modules/office'
import { userApi, type UserSimple } from '@/api/modules/system'
import type { Task, TaskForm } from '@/api/types'

const tableData = ref<Task[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const userList = ref<UserSimple[]>([])

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const defaultForm = (): TaskForm => ({ title: '', content: '', creatorId: undefined, assigneeId: 0, priority: '', status: '', dueDate: '' })
const form = reactive<TaskForm>(defaultForm())
const rules = { title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }], assigneeId: [{ required: true, message: '请选择负责人', trigger: 'change' }], priority: [{ required: true, message: '请选择优先级', trigger: 'change' }], status: [{ required: true, message: '请选择状态', trigger: 'change' }], dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await taskApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadUsers = async () => {
  try {
    const res = await userApi.list({ pageSize: 9999 })
    userList.value = res.data?.records || []
  } catch { /* silent */ }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, defaultForm()); dialogVisible.value = true }
const handleEdit = (row: Task) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { title: row.title, content: row.content, creatorId: row.creatorId, assigneeId: row.assigneeId, priority: row.priority, status: row.status, startDate: row.startDate, dueDate: row.dueDate }); dialogVisible.value = true }
const handleDelete = async (row: Task) => { await ElMessageBox.confirm('确定删除？'); await taskApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await taskApi.update(editId.value, form as any)
    else await taskApi.create(form as any)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadUsers() })
</script>

<style lang="scss" scoped>
.task-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
