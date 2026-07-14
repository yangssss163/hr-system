<template>
  <div class="document-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">上传文档</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="文档名称" width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.type || 'info'">{{ typeMap[row.type]?.label || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleDownload(row)">下载</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文档' : '上传文档'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="文档名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型" prop="type"><el-select v-model="form.type"><el-option label="文档" value="document" /><el-option label="图片" value="image" /><el-option label="视频" value="video" /><el-option label="其他" value="other" /></el-select></el-form-item>
        <el-form-item label="文件路径"><el-input v-model="form.fileUrl" /></el-form-item>
        <el-form-item label="文件大小"><el-input-number v-model="form.fileSize" style="width:100%" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="禁用" :value="0" /><el-option label="正常" :value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { documentApi } from '@/api/modules/office'
import type { Document, DocumentForm } from '@/api/types'

const tableData = ref<Document[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const typeMap: Record<string, { label: string; type: string }> = {
  document: { label: '文档', type: 'info' },
  image: { label: '图片', type: 'success' },
  video: { label: '视频', type: 'warning' },
  other: { label: '其他', type: 'danger' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<DocumentForm>({ name: '', type: '', fileUrl: '', fileSize: 0, description: '', status: 1 })
const rules = { name: [{ required: true, message: '请输入文档名称', trigger: 'blur' }], type: [{ required: true, message: '请选择类型', trigger: 'change' }] }

const formatSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await documentApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', type: '', fileUrl: '', fileSize: 0, description: '', status: 1 }); dialogVisible.value = true }
const handleEdit = (row: Document) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Document) => { await ElMessageBox.confirm('确定删除？'); await documentApi.delete(row.id); ElMessage.success('删除成功'); loadData() }
const handleDownload = (row: Document) => { window.open(row.fileUrl, '_blank') }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await documentApi.update(editId.value, form)
    else await documentApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.document-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>