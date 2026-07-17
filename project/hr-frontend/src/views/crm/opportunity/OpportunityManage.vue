<template>
  <div class="opportunity-manage">
    <el-card>
      <div class="toolbar">
        <el-input v-model="queryParams.keyword" placeholder="搜索商机名称" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="queryParams.stage" placeholder="阶段" clearable style="width:120px" @change="loadData">
          <el-option label="需求确认" value="need_confirm" /><el-option label="方案设计" value="design" /><el-option label="报价" value="quote" /><el-option label="商务谈判" value="negotiate" /><el-option label="成交" value="closed" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-permission="'crm:opportunity:create'" type="primary" @click="handleAdd">新增商机</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="name" label="商机名称" width="150" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="stage" label="阶段" width="100">
          <template #default="{ row }">
            <el-tag :type="stageMap[row.stage]?.type || 'info'">{{ stageMap[row.stage]?.label || row.stage }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="probability" label="成功率" width="80">
          <template #default="{ row }">{{ row.probability }}%</template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="expectedCloseDate" label="预计成交日期" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-permission="'crm:opportunity:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'crm:opportunity:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商机' : '新增商机'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="客户" prop="customerId"><el-select v-model="form.customerId"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="商机名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="阶段" prop="stage"><el-select v-model="form.stage"><el-option label="需求确认" value="need_confirm" /><el-option label="方案设计" value="design" /><el-option label="报价" value="quote" /><el-option label="商务谈判" value="negotiate" /><el-option label="成交" value="closed" /></el-select></el-form-item>
        <el-form-item label="成功率" prop="probability"><el-input-number v-model="form.probability" :min="0" :max="100" style="width:100%" /></el-form-item>
        <el-form-item label="负责人" prop="ownerId">
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
        <el-form-item label="预计成交日期" prop="expectedCloseDate"><el-date-picker v-model="form.expectedCloseDate" type="date" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { opportunityApi, customerApi } from '@/api/modules/crm'
import { getCompanyTree } from '@/api/system/company'
import { getDeptTree } from '@/api/system/dept'
import { getEmployeeList } from '@/api/modules/employee'
import type { Opportunity, OpportunityForm, Customer, Company, Dept, Employee } from '@/api/types'

const tableData = ref<Opportunity[]>([])
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

const stageMap: Record<string, { label: string; type: string }> = {
  need_confirm: { label: '需求确认', type: 'warning' },
  design: { label: '方案设计', type: 'info' },
  quote: { label: '报价', type: 'primary' },
  negotiate: { label: '商务谈判', type: 'warning' },
  closed: { label: '成交', type: 'success' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ keyword: '', stage: '' })
const form = reactive<OpportunityForm>({ customerId: 0, name: '', amount: 0, stage: '', probability: 0, ownerId: 0, expectedCloseDate: '' })
const rules = { customerId: [{ required: true, message: '请选择客户', trigger: 'change' }], name: [{ required: true, message: '请输入商机名称', trigger: 'blur' }], amount: [{ required: true, message: '请输入金额', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await opportunityApi.list({ page: pagination.page, pageSize: pagination.pageSize, keyword: queryParams.keyword || undefined, stage: queryParams.stage || undefined })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadCustomers = async () => {
  try {
    const res = await customerApi.list({ pageSize: 1000 })
    customers.value = res.data.records
  } catch (e: any) { if (e?.isAuthError) return; ElMessage.error(e?.response?.data?.message || e?.message || '操作失败') }
}

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

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { customerId: 0, name: '', amount: 0, stage: '', probability: 0, ownerId: 0, expectedCloseDate: '' }); selectedCompanyId.value = 0; selectedDeptId.value = 0; deptList.value = []; employeeList.value = []; dialogVisible.value = true }
const handleEdit = async (row: Opportunity) => {
  isEdit.value = true; editId.value = row.id
  form.customerId = row.customerId; form.name = row.name; form.amount = row.amount
  form.stage = row.stage || ''; form.probability = row.probability; form.ownerId = row.ownerId
  form.expectedCloseDate = row.expectedCloseDate || ''
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
const handleDelete = async (row: Opportunity) => {
  try {
    await ElMessageBox.confirm('确定删除？')
    await opportunityApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    const data = { ...form }
    if (data.expectedCloseDate instanceof Date) {
      data.expectedCloseDate = data.expectedCloseDate.toISOString().slice(0, 10)
    }
    if (isEdit.value) await opportunityApi.update(editId.value, data as OpportunityForm)
    else await opportunityApi.create(data as OpportunityForm)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

onMounted(() => { loadData(); loadCustomers(); loadCompanies() })
</script>

<style lang="scss" scoped>
.opportunity-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
