<template>
  <div class="customer-manage">
    <el-card>
      <div class="toolbar">
        <el-input v-model="queryParams.keyword" placeholder="搜索名称/电话" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width:120px" @change="loadData">
          <el-option label="启用" value="active" /><el-option label="禁用" value="inactive" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-permission="'crm:customer:create'" type="primary" @click="handleAdd">新增客户</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="客户名称" width="150" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">{{ row.status === 'active' ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-permission="'crm:customer:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'crm:customer:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="客户名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="联系人" prop="contactName"><el-input v-model="form.contactName" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="行业"><el-input v-model="form.industry" /></el-form-item>
        <el-form-item label="来源"><el-input v-model="form.source" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="启用" value="active" /><el-option label="禁用" value="inactive" /></el-select></el-form-item>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerApi } from '@/api/modules/crm'
import { getCompanyTree } from '@/api/system/company'
import { getDeptTree } from '@/api/system/dept'
import { getEmployeeList } from '@/api/modules/employee'
import type { Customer, CustomerForm, Company, Dept, Employee } from '@/api/types'

const tableData = ref<Customer[]>([])
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

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ keyword: '', status: '' })
const form = reactive<CustomerForm>({ name: '', phone: '', email: '', address: '', industry: '', source: '', status: 'active', contactName: '' })
const rules = { name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }], contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }], phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await customerApi.list({ page: pagination.page, pageSize: pagination.pageSize, keyword: queryParams.keyword || undefined, status: queryParams.status || undefined })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const resetForm = () => Object.assign(form, { name: '', phone: '', email: '', address: '', industry: '', source: '', status: 'active', contactName: '' })

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

const handleAdd = () => { isEdit.value = false; editId.value = 0; resetForm(); form.ownerId = 0; selectedCompanyId.value = 0; selectedDeptId.value = 0; deptList.value = []; employeeList.value = []; dialogVisible.value = true }
const handleEdit = async (row: Customer) => {
  isEdit.value = true; editId.value = row.id
  form.name = row.name; form.phone = row.phone; form.email = row.email || ''
  form.address = row.address || ''; form.industry = row.industry || ''
  form.source = row.source || ''; form.status = row.status
  form.contactName = row.contactName || ''; form.ownerId = row.ownerId || 0
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
const handleDelete = async (row: Customer) => {
  try {
    await ElMessageBox.confirm('确定删除？')
    await customerApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e: any) { if (e?.isAuthError) return }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value) await customerApi.update(editId.value, form)
    else await customerApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e: any) { if (e?.isAuthError) return; ElMessage.error(e?.response?.data?.message || e?.message || '操作失败') }
}

onMounted(() => { loadData(); loadCompanies() })
</script>

<style lang="scss" scoped>
.customer-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
