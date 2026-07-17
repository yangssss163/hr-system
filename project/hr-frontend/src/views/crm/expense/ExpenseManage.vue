<template>
  <div class="expense-manage">
    <el-card>
      <div class="toolbar">
        <el-input v-model="queryParams.keyword" placeholder="搜索费用名称" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="queryParams.category" placeholder="费用类型" clearable style="width:120px" @change="loadData">
          <el-option label="销售费用" value="sales" /><el-option label="管理费用" value="management" /><el-option label="财务费用" value="finance" /><el-option label="其他" value="other" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width:120px" @change="loadData">
          <el-option label="待确认" value="pending" /><el-option label="已确认" value="confirmed" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-permission="'crm:expense:create'" type="primary" @click="handleAdd">新增费用</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="费用名称" width="150" />
        <el-table-column prop="amount" label="费用金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="category" label="费用类型" width="120">
          <template #default="{ row }">
            <el-tag :type="categoryMap[row.category]?.type || 'info'">{{ categoryMap[row.category]?.label || row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="描述" width="200" />
        <el-table-column prop="expenseDate" label="费用日期" width="120" />
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'confirmed' ? 'success' : 'warning'">{{ row.status === 'confirmed' ? '已确认' : '待确认' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-permission="'crm:expense:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'crm:expense:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑费用' : '新增费用'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="费用名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="费用金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="费用类型" prop="category"><el-select v-model="form.category"><el-option label="销售费用" value="sales" /><el-option label="管理费用" value="management" /><el-option label="财务费用" value="finance" /><el-option label="其他" value="other" /></el-select></el-form-item>
        <el-form-item label="费用日期" prop="expenseDate"><el-date-picker v-model="form.expenseDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="待确认" value="pending" /><el-option label="已确认" value="confirmed" /></el-select></el-form-item>
        <el-form-item label="申请人">
          <div style="display:flex;gap:8px">
            <el-select v-model="selectedCompanyId" placeholder="选择公司" @change="onCompanyChange" style="width:130px">
              <el-option v-for="c in companies" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
            <el-select v-model="selectedDeptId" placeholder="选择部门" @change="onDeptChange" style="width:130px" :disabled="!selectedCompanyId">
              <el-option v-for="d in deptList" :key="d.id" :label="d.name" :value="d.id" />
            </el-select>
            <el-select v-model="form.applicantId" placeholder="选择员工" style="width:130px" :disabled="!selectedDeptId">
              <el-option v-for="e in employeeList" :key="e.id" :label="e.name" :value="e.id" />
            </el-select>
          </div>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { expenseApi } from '@/api/modules/crm'
import { getCompanyTree } from '@/api/system/company'
import { getDeptTree } from '@/api/system/dept'
import { getEmployeeList } from '@/api/modules/employee'
import type { CrmExpense, CrmExpenseForm, Company, Dept, Employee } from '@/api/types'

const tableData = ref<CrmExpense[]>([])
const companies = ref<Company[]>([])
const deptList = ref<Dept[]>([])
const employeeList = ref<Employee[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const selectedCompanyId = ref<number>(0)
const selectedDeptId = ref<number>(0)

const categoryMap: Record<string, { label: string; type: string }> = {
  sales: { label: '销售费用', type: 'primary' },
  management: { label: '管理费用', type: 'info' },
  finance: { label: '财务费用', type: 'warning' },
  other: { label: '其他', type: '' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ keyword: '', status: '', category: '' })
const form = reactive<CrmExpenseForm>({ name: '', amount: 0, expenseDate: '', category: '', status: 'pending' })
const rules = { name: [{ required: true, message: '请输入费用名称', trigger: 'blur' }], amount: [{ required: true, message: '请输入费用金额', trigger: 'blur' }], category: [{ required: true, message: '请选择费用类型', trigger: 'change' }], expenseDate: [{ required: true, message: '请选择费用日期', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await expenseApi.list({ page: pagination.page, pageSize: pagination.pageSize, keyword: queryParams.keyword || undefined, status: queryParams.status || undefined, category: queryParams.category || undefined })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const resetForm = () => Object.assign(form, { name: '', amount: 0, expenseDate: '', category: '', status: 'pending', remark: '' })

const loadCompanies = async () => { try { const r = await getCompanyTree(); companies.value = r.data } catch {} }

const onCompanyChange = async (companyId: number) => {
  selectedDeptId.value = 0; form.applicantId = 0; employeeList.value = []
  if (!companyId) { deptList.value = []; return }
  try { const r = await getDeptTree({ companyId }); deptList.value = r.data } catch {}
}

const onDeptChange = async (deptId: number) => {
  form.applicantId = 0; employeeList.value = []
  if (!deptId) return
  try { const r = await getEmployeeList({ deptId, pageSize: 1000 }); employeeList.value = r.data.records } catch {}
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; resetForm(); form.applicantId = 0; selectedCompanyId.value = 0; selectedDeptId.value = 0; deptList.value = []; employeeList.value = []; dialogVisible.value = true }
const handleEdit = async (row: CrmExpense) => {
  isEdit.value = true; editId.value = row.id
  form.name = row.name; form.amount = row.amount; form.expenseDate = row.expenseDate || ''
  form.category = row.category || ''; form.status = row.status
  form.remark = row.remark || ''; form.applicantId = row.applicantId || 0
  if (row.applicantId) {
    try {
      const r = await getEmployeeList({ pageSize: 1000 })
      const emp = r.data.records.find(e => e.id === row.applicantId)
      if (emp) {
        selectedCompanyId.value = emp.companyId
        await onCompanyChange(emp.companyId)
        selectedDeptId.value = emp.deptId
        await onDeptChange(emp.deptId)
        form.applicantId = row.applicantId
      }
    } catch {}
  } else {
    selectedCompanyId.value = 0; selectedDeptId.value = 0; deptList.value = []; employeeList.value = []
  }
  dialogVisible.value = true
}
const handleDelete = async (row: CrmExpense) => {
  try {
    await ElMessageBox.confirm('确定删除？')
    await expenseApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e: any) { if (e?.isAuthError) return; ElMessage.error(e?.response?.data?.message || e?.message || '操作失败') }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    const data = { ...form }
    if ((data.expenseDate as any) instanceof Date) {
      data.expenseDate = (data.expenseDate as any as Date).toISOString().slice(0, 10)
    }
    if (isEdit.value) await expenseApi.update(editId.value, data as CrmExpenseForm)
    else await expenseApi.create(data as CrmExpenseForm)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

onMounted(() => { loadData(); loadCompanies() })
</script>

<style lang="scss" scoped>
.expense-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
