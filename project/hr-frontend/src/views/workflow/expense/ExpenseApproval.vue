<template>
  <div class="expense-approval">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'workflow:expense:create'" type="primary" @click="handleAdd">新建报销</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString?.() ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="category" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="categoryMap[row.category]?.type || 'info'">{{ categoryMap[row.category]?.label || row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" width="200" show-overflow-tooltip />
        <el-table-column prop="expenseDate" label="费用日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">{{ row.statusName || statusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approverName" label="审批人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-permission="'workflow:expense:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'workflow:expense:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            <el-button v-permission="'workflow:expense:approve'" v-if="row.status === 'pending'" size="small" type="success" @click="handleApprove(row, true)">同意</el-button>
            <el-button v-permission="'workflow:expense:approve'" v-if="row.status === 'pending'" size="small" type="danger" @click="handleApprove(row, false)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑报销' : '新建报销'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="费用类型" prop="category"><el-select v-model="form.category"><el-option label="差旅费" value="travel" /><el-option label="业务招待费" value="entertainment" /><el-option label="办公用品费" value="office" /><el-option label="其他" value="other" /></el-select></el-form-item>
        <el-form-item label="说明" prop="description"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="费用日期" prop="expenseDate"><el-date-picker v-model="form.expenseDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
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
import { useUserStore } from '@/stores/user'
import { expenseApprovalApi } from '@/api/modules/workflow'
import type { ExpenseApproval, ExpenseApprovalForm, ApprovalRequest } from '@/api/types'

const userStore = useUserStore()
const tableData = ref<ExpenseApproval[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const approveDialogVisible = ref(false)
const formRef = ref()
const approveFormRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const approveId = ref(0)

const categoryMap: Record<string, { label: string; type: string }> = {
  travel: { label: '差旅费', type: 'info' },
  entertainment: { label: '业务招待费', type: 'warning' },
  office: { label: '办公用品费', type: 'success' },
  other: { label: '其他', type: 'danger' }
}

const statusMap: Record<string, { label: string; type: string }> = {
  pending: { label: '待审批', type: 'warning' },
  approved: { label: '已批准', type: 'success' },
  rejected: { label: '已拒绝', type: 'danger' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<ExpenseApprovalForm>({ applicantId: 0, amount: 0, category: '', description: '', expenseDate: '' })
const approveForm = reactive<ApprovalRequest>({ result: 'approved', comment: '' })
const rules = { amount: [{ required: true, message: '请输入金额', trigger: 'blur' }], category: [{ required: true, message: '请选择费用类型', trigger: 'change' }], description: [{ required: true, message: '请输入说明', trigger: 'blur' }], expenseDate: [{ required: true, message: '请选择费用日期', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await expenseApprovalApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { applicantId: userStore.userInfo?.userId || 0, amount: 0, category: '', description: '', expenseDate: '' }); dialogVisible.value = true }
const handleEdit = (row: ExpenseApproval) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { applicantId: row.applicantId, amount: row.amount, category: row.category, description: row.description, expenseDate: row.expenseDate }); dialogVisible.value = true }
const handleDelete = async (row: ExpenseApproval) => { await ElMessageBox.confirm('确定删除？'); await expenseApprovalApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleApprove = (row: ExpenseApproval, approve: boolean) => {
  approveId.value = row.id
  Object.assign(approveForm, { result: approve ? 'approved' : 'rejected', comment: '' })
  approveDialogVisible.value = true
}

const submitApprove = async () => {
  await expenseApprovalApi.approve(approveId.value, approveForm)
  ElMessage.success('审批成功'); approveDialogVisible.value = false; loadData()
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (!isEdit.value) form.applicantId = userStore.userInfo?.userId || 0
    if (isEdit.value) await expenseApprovalApi.update(editId.value, form)
    else await expenseApprovalApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.expense-approval { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
