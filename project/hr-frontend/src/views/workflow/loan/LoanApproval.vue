<template>
  <div class="loan-approval">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">申请借款</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="employeeName" label="申请人" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="amount" label="借款金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="借款原因" width="200" show-overflow-tooltip />
        <el-table-column prop="returnDate" label="预计归还日期" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">{{ statusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveComment" label="审批意见" width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            <el-button v-if="row.status === 'pending'" size="small" type="success" @click="handleApprove(row, true)">同意</el-button>
            <el-button v-if="row.status === 'pending'" size="small" type="danger" @click="handleApprove(row, false)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑借款' : '申请借款'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="借款金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="借款原因" prop="reason"><el-input v-model="form.reason" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="预计归还日期" prop="returnDate"><el-date-picker v-model="form.returnDate" type="date" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
    <el-dialog v-model="approveDialogVisible" title="审批" width="400px">
      <el-form :model="approveForm" ref="approveFormRef" label-width="80px">
        <el-form-item label="审批意见"><el-input v-model="approveForm.comment" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="approveDialogVisible=false">取消</el-button><el-button type="primary" @click="submitApprove">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loanApprovalApi } from '@/api/modules/workflow'
import type { LoanApproval, LoanApprovalForm, ApprovalRequest } from '@/api/types'

const tableData = ref<LoanApproval[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const approveDialogVisible = ref(false)
const formRef = ref()
const approveFormRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const approveId = ref(0)

const statusMap: Record<string, { label: string; type: string }> = {
  pending: { label: '待审批', type: 'warning' },
  approved: { label: '已批准', type: 'success' },
  rejected: { label: '已拒绝', type: 'danger' },
  returned: { label: '已归还', type: 'info' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<LoanApprovalForm>({ amount: 0, reason: '', returnDate: '' })
const approveForm = reactive<ApprovalRequest>({ approve: true, comment: '' })
const rules = { amount: [{ required: true, message: '请输入借款金额', trigger: 'blur' }], reason: [{ required: true, message: '请输入借款原因', trigger: 'blur' }], returnDate: [{ required: true, message: '请选择预计归还日期', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await loanApprovalApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { amount: 0, reason: '', returnDate: '' }); dialogVisible.value = true }
const handleEdit = (row: LoanApproval) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: LoanApproval) => { await ElMessageBox.confirm('确定删除？'); await loanApprovalApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleApprove = (row: LoanApproval, approve: boolean) => {
  approveId.value = row.id
  Object.assign(approveForm, { approve, comment: '' })
  approveDialogVisible.value = true
}

const submitApprove = async () => {
  await loanApprovalApi.approve(approveId.value, approveForm)
  ElMessage.success('审批成功'); approveDialogVisible.value = false; loadData()
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await loanApprovalApi.update(editId.value, form)
    else await loanApprovalApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.loan-approval { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>