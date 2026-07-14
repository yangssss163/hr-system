<template>
  <div class="expense-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增费用</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="amount" label="费用金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="type" label="费用类型" width="120" />
        <el-table-column prop="description" label="描述" width="200" />
        <el-table-column prop="expenseDate" label="费用日期" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '已确认' : '待确认' }}</el-tag>
          </template>
        </el-table-column>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑费用' : '新增费用'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="客户" prop="customerId"><el-select v-model="form.customerId"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="费用金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="费用类型" prop="type"><el-select v-model="form.type"><el-option label="销售费用" value="sales" /><el-option label="管理费用" value="management" /><el-option label="财务费用" value="finance" /><el-option label="其他" value="other" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="费用日期" prop="expenseDate"><el-date-picker v-model="form.expenseDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="待确认" :value="0" /><el-option label="已确认" :value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { expenseApi, customerApi } from '@/api/modules/crm'
import type { CrmExpense, CrmExpenseForm, Customer } from '@/api/types'

const tableData = ref<CrmExpense[]>([])
const customers = ref<Customer[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<CrmExpenseForm>({ customerId: 0, amount: 0, type: '', description: '', expenseDate: '', status: 0 })
const rules = { customerId: [{ required: true, message: '请选择客户', trigger: 'change' }], amount: [{ required: true, message: '请输入费用金额', trigger: 'blur' }], type: [{ required: true, message: '请选择费用类型', trigger: 'change' }], expenseDate: [{ required: true, message: '请选择费用日期', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await expenseApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadCustomers = async () => {
  try {
    const res = await customerApi.list({ pageSize: 1000 })
    customers.value = res.data.records
  } catch {}
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { customerId: 0, amount: 0, type: '', description: '', expenseDate: '', status: 0 }); dialogVisible.value = true }
const handleEdit = (row: CrmExpense) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: CrmExpense) => { await ElMessageBox.confirm('确定删除？'); await expenseApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await expenseApi.update(editId.value, form)
    else await expenseApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadCustomers() })
</script>

<style lang="scss" scoped>
.expense-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>