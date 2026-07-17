<template>
  <div class="document-manage">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'office:document:create'" type="primary" @click="handleAdd">上传文档</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="文档标题" width="200" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="content" label="内容" width="250" show-overflow-tooltip />
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="isPublic" label="公开" width="70">
          <template #default="{ row }">
            <el-tag :type="row.isPublic === 1 ? 'success' : 'info'">{{ row.isPublic === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.fileUrl" size="small" @click="handleDownload(row)">下载</el-button>
            <el-button v-permission="'office:document:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'office:document:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文档' : '上传文档'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="文档标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类别" prop="category"><el-select v-model="form.category"><el-option label="制度文档" value="制度文档" /><el-option label="操作手册" value="操作手册" /><el-option label="模板" value="模板" /><el-option label="其他" value="其他" /></el-select></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="文件路径"><el-input v-model="form.fileUrl" placeholder="输入文件URL地址" /></el-form-item>
        <el-form-item label="创建人" prop="creatorId">
          <el-select v-model="form.creatorId" filterable placeholder="选择创建人" style="width:100%">
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName} (${u.deptName || ''})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级ID"><el-input-number v-model="form.parentId" style="width:100%" :min="0" /></el-form-item>
        <el-form-item label="是否公开"><el-switch v-model="form.isPublic" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { documentApi } from '@/api/modules/office'
import { userApi, type UserSimple } from '@/api/modules/system'
import type { Document, DocumentForm } from '@/api/types'

const tableData = ref<Document[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const userList = ref<UserSimple[]>([])

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const defaultForm = (): DocumentForm => ({ title: '', content: '', category: '', parentId: 0, creatorId: undefined, isPublic: 0, fileUrl: '' })
const form = reactive<DocumentForm>(defaultForm())
const rules = { title: [{ required: true, message: '请输入文档标题', trigger: 'blur' }], category: [{ required: true, message: '请选择类别', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await documentApi.list({ page: pagination.page, pageSize: pagination.pageSize })
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
const handleEdit = (row: Document) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { title: row.title, content: row.content, category: row.category, parentId: row.parentId, creatorId: row.creatorId, isPublic: row.isPublic, fileUrl: row.fileUrl }); dialogVisible.value = true }
const handleDelete = async (row: Document) => { await ElMessageBox.confirm('确定删除？'); await documentApi.delete(row.id); ElMessage.success('删除成功'); loadData() }
const handleDownload = (row: Document) => { if (row.fileUrl) window.open(row.fileUrl, '_blank') }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await documentApi.update(editId.value, form as any)
    else await documentApi.create(form as any)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadUsers() })
</script>

<style lang="scss" scoped>
.document-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
