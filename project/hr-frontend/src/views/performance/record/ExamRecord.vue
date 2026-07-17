<template>
  <div class="exam-record">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'performance:record:create'" type="primary" @click="handleAdd">添加考核</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="planName" label="考核计划" width="150" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="totalScore" label="总分" width="100">
          <template #default="{ row }"><el-tag :type="getScoreTag(row.totalScore)">{{ row.totalScore }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="levelName" label="等级" width="100">
          <template #default="{ row }"><el-tag :type="getLevelTag(row.levelName)">{{ row.levelName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="evaluatorName" label="评分人" width="100" />
        <el-table-column prop="evaluateTime" label="评分时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button v-permission="'performance:record:update'" size="small" @click="handleEdit(row)">编辑</el-button></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑考核' : '添加考核'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="考核计划" prop="planId"><el-select v-model="form.planId"><el-option label="选择计划" :value="0" /></el-select></el-form-item>
        <el-form-item label="员工" prop="employeeId"><el-select v-model="form.employeeId"><el-option label="选择员工" :value="0" /></el-select></el-form-item>
        <el-form-item label="考核项">
          <el-table :data="form.items" border>
            <el-table-column prop="indicator" label="指标" width="150">
              <template #default="{ row }"><el-input v-model="row.indicator" /></template>
            </el-table-column>
            <el-table-column prop="weight" label="权重(%)" width="100">
              <template #default="{ row }"><el-input-number v-model="row.weight" :min="0" :max="100" /></template>
            </el-table-column>
            <el-table-column prop="score" label="得分" width="100">
              <template #default="{ row }"><el-input-number v-model="row.score" :min="0" :max="100" /></template>
            </el-table-column>
          </el-table>
          <el-button v-permission="'performance:record:update'" type="primary" size="small" @click="addItem" style="margin-top: 10px">添加考核项</el-button>
        </el-form-item>
        <el-form-item label="总分" prop="totalScore"><el-input-number v-model="form.totalScore" :min="0" :max="100" /></el-form-item>
        <el-form-item label="评价"><el-input v-model="form.evaluation" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="绩效等级" prop="levelId"><el-select v-model="form.levelId"><el-option label="选择等级" :value="0" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { perfRecordApi } from '@/api/modules/performance'
import type { PerfRecord, PerfRecordForm } from '@/api/types'

const tableData = ref<PerfRecord[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<PerfRecordForm>({ planId: 0, employeeId: 0, items: [], totalScore: 0, evaluation: '', levelId: 0 })
const rules = { planId: [{ required: true, message: '请选择考核计划', trigger: 'blur' }], employeeId: [{ required: true, message: '请选择员工', trigger: 'blur' }], totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }], levelId: [{ required: true, message: '请选择绩效等级', trigger: 'blur' }] }

const levelTagMap: Record<string, string> = { S: 'danger', A: 'warning', B: 'primary', C: 'info', D: '' }
const getLevelTag = (name: string) => levelTagMap[name] || ''
const getScoreTag = (score: number) => score >= 90 ? 'danger' : score >= 80 ? 'warning' : score >= 60 ? 'primary' : 'info'

const addItem = () => form.items.push({ indicator: '', weight: 0, score: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await perfRecordApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { planId: 0, employeeId: 0, items: [{ indicator: '', weight: 0, score: 0 }], totalScore: 0, evaluation: '', levelId: 0 }); dialogVisible.value = true }
const handleEdit = (row: PerfRecord) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { planId: row.planId, employeeId: row.employeeId, items: [], totalScore: row.totalScore, evaluation: '', levelId: row.levelId }); dialogVisible.value = true }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await perfRecordApi.update(editId.value, form)
    else await perfRecordApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.exam-record {
  .toolbar { margin-bottom: 16px; }
}
</style>