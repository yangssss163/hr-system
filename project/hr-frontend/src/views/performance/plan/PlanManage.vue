<template>
  <div class="plan-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">创建计划</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="计划名称" min-width="200" />
        <el-table-column prop="deptName" label="所属部门" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.statusName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑计划' : '创建计划'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="计划名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="所属部门" prop="deptId"><el-select v-model="form.deptId" placeholder="请选择部门"><el-option label="全部部门" :value="0" /></el-select></el-form-item>
        <el-form-item label="开始日期" prop="startDate"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" /></el-form-item>
        <el-form-item label="结束日期" prop="endDate"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="进行中" :value="1" /><el-option label="已结束" :value="0" /></el-select></el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { perfPlanApi } from '@/api/modules/performance'
import type { PerfPlan, PerfPlanForm } from '@/api/types'

const tableData = ref<PerfPlan[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<PerfPlanForm>({ name: '', deptId: 0, startDate: '', endDate: '', employeeIds: [], status: 1 })
const rules = { name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }], startDate: [{ required: true, message: '请选择开始日期', trigger: 'blur' }], endDate: [{ required: true, message: '请选择结束日期', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await perfPlanApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', deptId: 0, startDate: '', endDate: '', employeeIds: [], status: 1 }); dialogVisible.value = true }
const handleEdit = (row: PerfPlan) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { name: row.name, deptId: row.deptId, startDate: row.startDate, endDate: row.endDate, employeeIds: [], status: row.status }); dialogVisible.value = true }
const handleDelete = async (row: PerfPlan) => { await ElMessageBox.confirm('确定删除？'); await perfPlanApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await perfPlanApi.update(editId.value, form)
    else await perfPlanApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.plan-manage {
  .toolbar { margin-bottom: 16px; }
}
</style>