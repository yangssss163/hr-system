<template>
  <div class="order-manage">
    <el-card>
      <div class="toolbar">
        <el-input v-model="queryParams.keyword" placeholder="搜索订单编号" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width:120px" @change="loadData">
          <el-option label="待付款" value="pending_payment" /><el-option label="已付款" value="paid" /><el-option label="已发货" value="shipped" /><el-option label="已完成" value="completed" /><el-option label="已取消" value="cancelled" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-permission="'crm:order:create'" type="primary" @click="handleAdd">新增订单</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">{{ statusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signDate" label="签约日期" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-permission="'crm:order:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'crm:order:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑订单' : '新增订单'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="客户" prop="customerId"><el-select v-model="form.customerId" @change="onCustomerChange"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="商机"><el-select v-model="form.opportunityId"><el-option v-for="o in filteredOpportunities" :key="o.id" :label="o.name" :value="o.id" /></el-select></el-form-item>
        <el-form-item label="订单编号" prop="orderNo"><el-input v-model="form.orderNo" /></el-form-item>
        <el-form-item label="金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="签约日期" prop="signDate"><el-date-picker v-model="form.signDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="状态" prop="status"><el-select v-model="form.status"><el-option label="待付款" value="pending_payment" /><el-option label="已付款" value="paid" /><el-option label="已发货" value="shipped" /><el-option label="已完成" value="completed" /><el-option label="已取消" value="cancelled" /></el-select></el-form-item>
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
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, customerApi, opportunityApi } from '@/api/modules/crm'
import { getCompanyTree } from '@/api/system/company'
import { getDeptTree } from '@/api/system/dept'
import { getEmployeeList } from '@/api/modules/employee'
import type { Order, OrderForm, Customer, Opportunity, Company, Dept, Employee } from '@/api/types'

const tableData = ref<Order[]>([])
const customers = ref<Customer[]>([])
const opportunities = ref<Opportunity[]>([])
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

const filteredOpportunities = computed(() => {
  if (!form.customerId) return opportunities.value
  return opportunities.value.filter(o => o.customerId === form.customerId)
})

const onCustomerChange = () => { form.opportunityId = 0 }

const statusMap: Record<string, { label: string; type: string }> = {
  pending_payment: { label: '待付款', type: 'warning' },
  paid: { label: '已付款', type: 'success' },
  shipped: { label: '已发货', type: 'info' },
  completed: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'danger' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ keyword: '', status: '' })
const form = reactive<OrderForm>({ customerId: 0, opportunityId: 0, orderNo: '', amount: 0, status: 'pending_payment', signDate: '' })
const rules = { customerId: [{ required: true, message: '请选择客户', trigger: 'change' }], orderNo: [{ required: true, message: '请输入订单编号', trigger: 'blur' }], amount: [{ required: true, message: '请输入金额', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await orderApi.list({ page: pagination.page, pageSize: pagination.pageSize, keyword: queryParams.keyword || undefined, status: queryParams.status || undefined })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadCustomers = async () => { try { const r = await customerApi.list({ pageSize: 1000 }); customers.value = r.data.records } catch {} }
const loadOpportunities = async () => { try { const r = await opportunityApi.list({ pageSize: 1000 }); opportunities.value = r.data.records } catch {} }
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

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { customerId: 0, opportunityId: 0, orderNo: '', amount: 0, status: 'pending_payment', signDate: '', ownerId: 0 }); selectedCompanyId.value = 0; selectedDeptId.value = 0; deptList.value = []; employeeList.value = []; dialogVisible.value = true }
const handleEdit = async (row: Order) => {
  isEdit.value = true; editId.value = row.id
  form.customerId = row.customerId; form.opportunityId = row.opportunityId || 0
  form.orderNo = row.orderNo; form.amount = row.amount; form.status = row.status
  form.signDate = row.signDate || ''; form.ownerId = row.ownerId || 0
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
const handleDelete = async (row: Order) => {
  try {
    await ElMessageBox.confirm('确定删除？')
    await orderApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    const data = { ...form }
    if ((data.signDate as any) instanceof Date) {
      data.signDate = (data.signDate as any as Date).toISOString().slice(0, 10)
    }
    if (isEdit.value) await orderApi.update(editId.value, data as OrderForm)
    else await orderApi.create(data as OrderForm)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

onMounted(() => { loadData(); loadCustomers(); loadOpportunities(); loadCompanies() })
</script>

<style lang="scss" scoped>
.order-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
