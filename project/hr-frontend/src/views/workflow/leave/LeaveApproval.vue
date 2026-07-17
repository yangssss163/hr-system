<template>
  <div class="leave-approval">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'workflow:leave:create'" type="primary" @click="handleAdd">申请请假</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="leaveType" label="请假类型" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="reason" label="原因" width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">{{ row.statusName || statusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approverName" label="审批人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-permission="'workflow:leave:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'workflow:leave:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            <el-button v-permission="'workflow:leave:approve'" v-if="row.status === 'pending'" size="small" type="success" @click="handleApprove(row, true)">同意</el-button>
            <el-button v-permission="'workflow:leave:approve'" v-if="row.status === 'pending'" size="small" type="danger" @click="handleApprove(row, false)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑请假' : '申请请假'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="请假类型" prop="leaveType"><el-select v-model="form.leaveType"><el-option label="事假" value="personal" /><el-option label="病假" value="sick" /><el-option label="年假" value="annual" /><el-option label="婚假" value="marriage" /><el-option label="产假" value="maternity" /><el-option label="其他" value="other" /></el-select></el-form-item>
        <el-form-item label="开始日期" prop="startDate"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期" prop="endDate"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="天数"><span>{{ form.days }}</span></el-form-item>
        <el-form-item label="原因" prop="reason"><el-input v-model="form.reason" type="textarea" :rows="3" /></el-form-item>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { leaveApprovalApi } from '@/api/modules/workflow'
import type { LeaveApproval, LeaveApprovalForm, ApprovalRequest } from '@/api/types'

const userStore = useUserStore()
const tableData = ref<LeaveApproval[]>([])
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
  rejected: { label: '已拒绝', type: 'danger' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<LeaveApprovalForm>({ applicantId: 0, leaveType: '', startDate: '', endDate: '', days: 1, reason: '' })
const approveForm = reactive<ApprovalRequest>({ result: 'approved', comment: '' })
const rules = { leaveType: [{ required: true, message: '请选择请假类型', trigger: 'change' }], startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }], endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }], reason: [{ required: true, message: '请输入原因', trigger: 'blur' }] }

// 自动计算天数
watch([() => form.startDate, () => form.endDate], ([start, end]) => {
  if (start && end) {
    const diff = Math.ceil((new Date(end).getTime() - new Date(start).getTime()) / (1000 * 60 * 60 * 24)) + 1
    form.days = Math.max(1, diff)
  }
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await leaveApprovalApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { applicantId: userStore.userInfo?.userId || 0, leaveType: '', startDate: '', endDate: '', days: 1, reason: '' }); dialogVisible.value = true }
const handleEdit = (row: LeaveApproval) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { applicantId: row.applicantId, leaveType: row.leaveType, startDate: row.startDate, endDate: row.endDate, days: row.days, reason: row.reason }); dialogVisible.value = true }
const handleDelete = async (row: LeaveApproval) => { await ElMessageBox.confirm('确定删除？'); await leaveApprovalApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleApprove = (row: LeaveApproval, approve: boolean) => {
  approveId.value = row.id
  Object.assign(approveForm, { result: approve ? 'approved' : 'rejected', comment: '' })
  approveDialogVisible.value = true
}

const submitApprove = async () => {
  await leaveApprovalApi.approve(approveId.value, approveForm)
  ElMessage.success('审批成功'); approveDialogVisible.value = false; loadData()
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (!isEdit.value) form.applicantId = userStore.userInfo?.userId || 0
    if (isEdit.value) await leaveApprovalApi.update(editId.value, form)
    else await leaveApprovalApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.leave-approval { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
