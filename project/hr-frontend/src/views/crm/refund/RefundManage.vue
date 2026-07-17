<template>
  <div class="refund-manage">
    <el-card>
      <div class="toolbar">
        <el-input v-model="queryParams.keyword" placeholder="搜索退款编号" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width:120px" @change="loadData">
          <el-option label="待处理" value="pending" /><el-option label="已完成" value="completed" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-permission="'crm:refund:create'" type="primary" @click="handleAdd">新增退款</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="refundNo" label="退款编号" width="160" />
        <el-table-column prop="amount" label="退款金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="refundDate" label="退款日期" width="120" />
        <el-table-column prop="reason" label="退款原因" width="200" />
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'">{{ row.status === 'completed' ? '已完成' : '待处理' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-permission="'crm:refund:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'crm:refund:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑退款' : '新增退款'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="退款编号" prop="refundNo"><el-input v-model="form.refundNo" /></el-form-item>
        <el-form-item label="客户" prop="customerId"><el-select v-model="form.customerId" @change="onCustomerChange"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="订单" prop="orderId"><el-select v-model="form.orderId"><el-option v-for="o in filteredOrders" :key="o.id" :label="o.orderNo" :value="o.id" /></el-select></el-form-item>
        <el-form-item label="退款金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="退款日期" prop="refundDate"><el-date-picker v-model="form.refundDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="退款原因" prop="reason"><el-input v-model="form.reason" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="待处理" value="pending" /><el-option label="已完成" value="completed" /></el-select></el-form-item>
        <el-form-item label="负责人">
          <div style="display:flex;gap:8px">
            <el-select v-model="selectedCompanyId" placeholder="选择公司" @change="onCompanyChange" style="width:130px">
              <el-option v-for="c in companies" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
            <el-select v-model="selectedDeptId" placeholder="选择部门" @change="onDeptChange" style="width:130px" :disabled="!selectedCompanyId">
              <el-option v-for="d in deptList" :key="d.id" :label="d.name" :value="d.id" />
            </el-select>
            <el-select v-model="form.ownerId" placeholder="选择员工" style="width:130px" :disabled="!selectedDeptId">
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { refundApi, orderApi, customerApi } from '@/api/modules/crm'
import { getCompanyTree } from '@/api/system/company'
import { getDeptTree } from '@/api/system/dept'
import { getEmployeeList } from '@/api/modules/employee'
import type { Refund, RefundForm, Order, Customer, Company, Dept, Employee } from '@/api/types'

const tableData = ref<Refund[]>([])
const orders = ref<Order[]>([])
const customers = ref<Customer[]>([])
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

const filteredOrders = computed(() => {
  if (!form.customerId) return orders.value
  return orders.value.filter(o => o.customerId === form.customerId)
})

const onCustomerChange = () => { form.orderId = 0 }

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ keyword: '', status: '' })
const form = reactive<RefundForm>({ orderId: 0, refundNo: '', amount: 0, refundDate: '', reason: '', status: 'pending', customerId: 0 })
const rules = { refundNo: [{ required: true, message: '请输入退款编号', trigger: 'blur' }], customerId: [{ required: true, message: '请选择客户', trigger: 'change' }], orderId: [{ required: true, message: '请选择订单', trigger: 'change' }], amount: [{ required: true, message: '请输入退款金额', trigger: 'blur' }], refundDate: [{ required: true, message: '请选择退款日期', trigger: 'change' }], reason: [{ required: true, message: '请输入退款原因', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await refundApi.list({ page: pagination.page, pageSize: pagination.pageSize, keyword: queryParams.keyword || undefined, status: queryParams.status || undefined })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadCustomers = async () => { try { const r = await customerApi.list({ pageSize: 1000 }); customers.value = r.data.records } catch {} }
const loadOrders = async () => { try { const r = await orderApi.list({ pageSize: 1000 }); orders.value = r.data.records } catch {} }
const loadCompanies = async () => { try { const r = await getCompanyTree(); companies.value = r.data } catch {} }

const onCompanyChange = async (companyId: number) => {
  selectedDeptId.value = 0; form.ownerId = 0; employeeList.value = []
  if (!companyId) { deptList.value = []; return }
  try { const r = await getDeptTree({ companyId }); deptList.value = r.data } catch {}
}

const onDeptChange = async (deptId: number) => {
  form.ownerId = 0; employeeList.value = []
  if (!deptId) return
  try { const r = await getEmployeeList({ deptId, pageSize: 1000 }); employeeList.value = r.data.records } catch {}
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { orderId: 0, refundNo: '', amount: 0, refundDate: '', reason: '', status: 'pending', customerId: 0, ownerId: 0 }); selectedCompanyId.value = 0; selectedDeptId.value = 0; deptList.value = []; employeeList.value = []; dialogVisible.value = true }
const handleEdit = async (row: Refund) => {
  isEdit.value = true; editId.value = row.id
  form.orderId = row.orderId; form.refundNo = row.refundNo; form.amount = row.amount
  form.refundDate = row.refundDate || ''; form.reason = row.reason || ''
  form.status = row.status; form.customerId = row.customerId || 0; form.ownerId = row.ownerId || 0
  if (row.ownerId) {
    try {
      const r = await getEmployeeList({ pageSize: 1000 })
      const emp = r.data.records.find(e => e.id === row.ownerId)
      if (emp) {
        selectedCompanyId.value = emp.companyId
        await onCompanyChange(emp.companyId)
        selectedDeptId.value = emp.deptId
        await onDeptChange(emp.deptId)
        form.ownerId = row.ownerId
      }
    } catch {}
  } else {
    selectedCompanyId.value = 0; selectedDeptId.value = 0; deptList.value = []; employeeList.value = []
  }
  dialogVisible.value = true
}
const handleDelete = async (row: Refund) => {
  try {
    await ElMessageBox.confirm('确定删除？')
    await refundApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    const data = { ...form }
    if ((data.refundDate as any) instanceof Date) {
      data.refundDate = (data.refundDate as any as Date).toISOString().slice(0, 10)
    }
    if (isEdit.value) await refundApi.update(editId.value, data as RefundForm)
    else await refundApi.create(data as RefundForm)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

onMounted(() => { loadData(); loadCustomers(); loadOrders(); loadCompanies() })
</script>

<style lang="scss" scoped>
.refund-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
