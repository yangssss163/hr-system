<template>
  <div class="interview-list">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">安排面试</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="candidateName" label="候选人" width="100" />
        <el-table-column prop="applyPosition" label="应聘职位" width="120" />
        <el-table-column prop="interviewRound" label="面试轮次" width="100" />
        <el-table-column prop="interviewerName" label="面试官" width="100" />
        <el-table-column prop="interviewTime" label="面试时间" width="180" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{ row }"><el-tag :type="resultTagType(row.result)">{{ resultMap[row.result] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleCheckin(row)">签到</el-button>
            <el-button size="small" @click="handleEvaluate(row)">评价</el-button>
            <el-button size="small" @click="handlePass(row)">通过</el-button>
            <el-button size="small" type="danger" @click="handleEliminate(row)">淘汰</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改面试' : '安排面试'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="候选人" prop="resumeId"><el-select v-model="form.resumeId"><el-option label="李四" :value="1" /></el-select></el-form-item>
        <el-form-item label="面试轮次" prop="interviewRound"><el-input-number v-model="form.interviewRound" :min="1" /></el-form-item>
        <el-form-item label="面试官" prop="interviewerId"><el-select v-model="form.interviewerId"><el-option label="王五" :value="10" /></el-select></el-form-item>
        <el-form-item label="面试时间" prop="interviewTime"><el-date-picker v-model="form.interviewTime" type="datetime" /></el-form-item>
        <el-form-item label="地点" prop="location"><el-input v-model="form.location" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
    <el-dialog v-model="evaluateVisible" title="面试评价" width="500px">
      <el-form :model="evaluateForm" :rules="evaluateRules" ref="evaluateFormRef" label-width="100px">
        <el-form-item label="评分" prop="score"><el-input-number v-model="evaluateForm.score" :min="0" :max="10" :step="0.5" /></el-form-item>
        <el-form-item label="评价" prop="evaluation"><el-textarea v-model="evaluateForm.evaluation" rows="4" /></el-form-item>
        <el-form-item label="结果" prop="result"><el-select v-model="evaluateForm.result"><el-option label="通过" value="pass" /><el-option label="淘汰" value="fail" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="evaluateVisible=false">取消</el-button><el-button type="primary" @click="handleSubmitEvaluate">提交评价</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { interviewApi } from '@/api/modules/recruitment'
import type { Interview, InterviewForm, InterviewEvaluateForm, EliminateRequest } from '@/api/types'

const tableData = ref<Interview[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const evaluateVisible = ref(false)
const formRef = ref()
const evaluateFormRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const evaluateId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<InterviewForm>({ resumeId: 0, interviewRound: 1, interviewerId: 0, interviewTime: '', location: '' })
const evaluateForm = reactive<InterviewEvaluateForm>({ score: 0, evaluation: '', result: 'pass' })

const rules = { resumeId: [{ required: true, message: '请选择候选人', trigger: 'change' }], interviewTime: [{ required: true, message: '请选择时间', trigger: 'change' }] }
const evaluateRules = { score: [{ required: true, message: '请输入评分', trigger: 'blur' }], evaluation: [{ required: true, message: '请输入评价', trigger: 'blur' }], result: [{ required: true, message: '请选择结果', trigger: 'change' }] }

const resultMap: Record<string, string> = { pending: '待面试', pass: '通过', fail: '淘汰', offer: '已发offer', hired: '已入职' }

const resultTagType = (result: string) => {
  const types: Record<string, string> = { pending: 'warning', pass: 'success', fail: 'danger', offer: 'primary', hired: '' }
  return types[result] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await interviewApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { resumeId: 0, interviewRound: 1, interviewerId: 0, interviewTime: '', location: '' }); dialogVisible.value = true }
const handleEdit = (row: Interview) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }

const handleCheckin = async (row: Interview) => {
  await interviewApi.checkin(row.id)
  ElMessage.success('签到成功'); loadData()
}

const handleEvaluate = (row: Interview) => {
  evaluateId.value = row.id
  Object.assign(evaluateForm, { score: row.score || 0, evaluation: row.evaluation || '', result: row.result || 'pass' })
  evaluateVisible.value = true
}

const handlePass = async (row: Interview) => {
  await interviewApi.pass(row.id)
  ElMessage.success('已通过'); loadData()
}

const handleEliminate = async (row: Interview) => {
  await ElMessageBox.confirm('确定淘汰该候选人？')
  const reason = '淘汰'
  await interviewApi.eliminate(row.id, { reason })
  ElMessage.success('已淘汰'); loadData()
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await interviewApi.update(editId.value, form)
    else await interviewApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

const handleSubmitEvaluate = async () => {
  await evaluateFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    await interviewApi.evaluate(evaluateId.value, evaluateForm)
    ElMessage.success('评价提交成功'); evaluateVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.interview-list { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>