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
      <el-form :model="formModel" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="计划名称" prop="name"><el-input v-model="formName" /></el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-select v-model="formDeptId" placeholder="请选择部门" style="width:100%">
            <el-option v-for="d in deptOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="formStartDate" type="date" placeholder="请选择开始日期" :editable="false" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="formEndDate" type="date" placeholder="请选择结束日期" :editable="false" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formStatus">
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { perfPlanApi } from '@/api/modules/performance'
import { getDeptTree } from '@/api/system/dept'
import type { PerfPlan, Dept } from '@/api/types'

/** 将 Date 转为 yyyy-MM-dd 字符串 */
const formatDate = (d: Date | null | string): string => {
  if (!d) return ''
  const date = typeof d === 'string' ? new Date(d) : d
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/** 将 yyyy-MM-dd 字符串转为 Date（编辑时回填用） */
const parseDate = (s: string): Date | null => {
  if (!s) return null
  const [y, m, d] = s.split('-').map(Number)
  return new Date(y, m - 1, d)
}

const tableData = ref<PerfPlan[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const deptOptions = ref<{ id: number; name: string }[]>([])

/** 表单字段 —— 全部使用独立 ref，避免 reactive 代理对 null/Date 的处理问题 */
const formName = ref('')
const formDeptId = ref<number | undefined>(undefined)
const formStartDate = ref<Date | null>(null)
const formEndDate = ref<Date | null>(null)
const formStatus = ref(1)

/** 供 el-form :model 绑定的 computed */
const formModel = computed(() => ({
  name: formName.value,
  deptId: formDeptId.value,
  startDate: formStartDate.value,
  endDate: formEndDate.value,
  status: formStatus.value
}))

const rules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

/** 展平部门树为下拉选项 */
const flattenDeptTree = (tree: Dept[], prefix = ''): { id: number; name: string }[] => {
  const result: { id: number; name: string }[] = []
  for (const node of tree) {
    const label = prefix + node.name
    result.push({ id: node.id, name: label })
    if (node.children && node.children.length > 0) {
      result.push(...flattenDeptTree(node.children, label + ' / '))
    }
  }
  return result
}

const loadDepts = async () => {
  try {
    const res = await getDeptTree()
    deptOptions.value = flattenDeptTree(res.data)
  } catch { /* 加载失败时部门列表为空 */ }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await perfPlanApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

/** 重置表单 */
const resetForm = () => {
  formName.value = ''
  formDeptId.value = undefined
  formStartDate.value = null
  formEndDate.value = null
  formStatus.value = 1
}

const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  resetForm()
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

const handleEdit = (row: PerfPlan) => {
  isEdit.value = true
  editId.value = row.id
  formName.value = row.name
  formDeptId.value = row.deptId
  formStartDate.value = parseDate(row.startDate)
  formEndDate.value = parseDate(row.endDate)
  formStatus.value = row.status
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

const handleDelete = async (row: PerfPlan) => {
  await ElMessageBox.confirm('确定删除？')
  await perfPlanApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (!valid) return

    const data = {
      name: formName.value,
      deptId: formDeptId.value,
      startDate: formatDate(formStartDate.value),
      endDate: formatDate(formEndDate.value),
      employeeIds: [] as number[],
      status: formStatus.value
    }
    console.log('提交数据:', JSON.stringify(data))

    if (isEdit.value) {
      await perfPlanApi.update(editId.value, data as any)
    } else {
      await perfPlanApi.create(data as any)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    await loadData()
  } catch (err: any) {
    console.error('考核计划提交失败:', err)
    console.error('响应详情:', err?.response?.status, JSON.stringify(err?.response?.data))
  }
}

onMounted(() => { loadDepts(); loadData() })
</script>

<style lang="scss" scoped>
.plan-manage {
  .toolbar { margin-bottom: 16px; }
}
</style>
