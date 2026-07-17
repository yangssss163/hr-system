<template>
  <div class="question-bank">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'recruitment:question:create'" type="primary" @click="handleAdd">创建试题</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="difficultyName" label="难度" width="100">
          <template #default="{ row }"><el-tag :type="difficultyTagType(row.difficulty)">{{ row.difficultyName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="title" label="题目" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-permission="'recruitment:question:edit'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'recruitment:question:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑试题' : '创建试题'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category">
            <el-option label="技术" value="technical" />
            <el-option label="行为" value="behavioral" />
            <el-option label="综合" value="hr" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="答案" prop="answer"><el-textarea v-model="form.answer" rows="4" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { questionApi } from '@/api/modules/recruitment'
import type { Question, QuestionForm } from '@/api/types'

const tableData = ref<Question[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<QuestionForm>({ category: 'technical', difficulty: 'medium', title: '', answer: '' })
const rules = { category: [{ required: true, message: '请选择分类', trigger: 'change' }], difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }], title: [{ required: true, message: '请输入题目', trigger: 'blur' }], answer: [{ required: true, message: '请输入答案', trigger: 'blur' }] }

const difficultyTagType = (difficulty: string) => {
  const types: Record<string, string> = { easy: 'success', medium: 'warning', hard: 'danger' }
  return types[difficulty] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await questionApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { category: 'technical', difficulty: 'medium', title: '', answer: '' }); dialogVisible.value = true }
const handleEdit = (row: Question) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { category: row.category, difficulty: row.difficulty, title: row.title, answer: row.answer }); dialogVisible.value = true }
const handleDelete = async (row: Question) => { await ElMessageBox.confirm('确定删除？'); await questionApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await questionApi.update(editId.value, form)
    else await questionApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.question-bank { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>