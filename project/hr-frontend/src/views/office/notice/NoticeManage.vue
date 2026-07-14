<template>
  <div class="notice-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">发布公告</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.type || 'info'">{{ typeMap[row.type]?.label || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" width="300" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型" prop="type"><el-select v-model="form.type"><el-option label="通知" value="notice" /><el-option label="公告" value="announcement" /><el-option label="新闻" value="news" /></el-select></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { noticeApi } from '@/api/modules/office'
import type { Notice, NoticeForm } from '@/api/types'

const tableData = ref<Notice[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const typeMap: Record<string, { label: string; type: string }> = {
  notice: { label: '通知', type: 'info' },
  announcement: { label: '公告', type: 'success' },
  news: { label: '新闻', type: 'warning' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<NoticeForm>({ title: '', content: '', type: '', status: 0 })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }], content: [{ required: true, message: '请输入内容', trigger: 'blur' }], type: [{ required: true, message: '请选择类型', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await noticeApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { title: '', content: '', type: '', status: 0 }); dialogVisible.value = true }
const handleEdit = (row: Notice) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Notice) => { await ElMessageBox.confirm('确定删除？'); await noticeApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await noticeApi.update(editId.value, form)
    else await noticeApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.notice-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>