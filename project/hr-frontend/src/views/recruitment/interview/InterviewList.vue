<template>
  <div class="interview-list">
    <el-card>
      <!-- 搜索筛选区 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.keyword" placeholder="候选人姓名" clearable style="width:180px" @keyup.enter="handleSearch" />
          <el-select v-model="query.result" placeholder="面试结果" clearable style="width:140px">
            <el-option label="待面试" value="pending" />
            <el-option label="通过" value="pass" />
            <el-option label="淘汰" value="fail" />
            <el-option label="已发Offer" value="offer" />
            <el-option label="已入职" value="hired" />
          </el-select>
          <el-select v-model="query.interviewerId" placeholder="面试官" clearable style="width:160px">
            <el-option v-for="u in interviewerList" :key="u.id" :label="u.realName" :value="u.id" />
          </el-select>
          <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width:260px" />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="candidateName" label="候选人" width="100" />
        <el-table-column prop="applyPosition" label="应聘职位" width="120" />
        <el-table-column prop="interviewRound" label="面试轮次" width="100" />
        <el-table-column prop="interviewerName" label="面试官" width="100" />
        <el-table-column prop="interviewTime" label="面试时间" width="180" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{ row }"><el-tag :type="resultTagType(row.result)">{{ resultMap[row.result] || row.result }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column label="操作" :min-width="300">
          <template #default="{ row }">
            <template v-if="row.result === 'pending'">
              <el-button v-permission="'recruitment:interview:list'" size="small" @click="handleCheckin(row)">签到</el-button>
              <el-button v-permission="'recruitment:interview:evaluate'" size="small" @click="handleEvaluate(row)">评价</el-button>
              <el-button v-permission="'recruitment:interview:edit'" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button v-permission="'recruitment:interview:decide'" size="small" type="success" @click="handlePass(row)">通过</el-button>
              <el-button v-permission="'recruitment:interview:decide'" size="small" type="danger" @click="handleEliminate(row)">淘汰</el-button>
            </template>
            <template v-else-if="row.result === 'pass'">
              <el-button v-permission="'recruitment:interview:offer'" size="small" type="primary" @click="handleOffer(row)">发Offer</el-button>
            </template>
            <template v-else-if="row.result === 'offer'">
              <el-button v-permission="'recruitment:interview:hire'" size="small" type="success" @click="handleConfirmHire(row)">确认入职</el-button>
            </template>
            <span v-else class="text-gray">—</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>

    <!-- 安排/编辑面试弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改面试' : '安排面试'" width="500px" @closed="formRef?.resetFields()">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="候选人" prop="resumeId">
          <el-select v-model="form.resumeId" filterable placeholder="请选择候选人">
            <el-option v-for="r in resumeList" :key="r.id" :label="`${r.name} - ${r.applyPosition || ''}`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="面试轮次" prop="interviewRound">
          <el-input-number v-model="form.interviewRound" :min="1" />
        </el-form-item>
        <el-form-item label="面试官" prop="interviewerId">
          <el-select v-model="form.interviewerId" filterable placeholder="请选择面试官">
            <el-option v-for="u in interviewerList" :key="u.id" :label="u.realName" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="面试时间" prop="interviewTime">
          <el-date-picker v-model="form.interviewTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择面试时间" />
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入面试地点" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="evaluateVisible" title="面试评价" width="500px" @closed="evaluateFormRef?.resetFields()">
      <el-form :model="evaluateForm" :rules="evaluateRules" ref="evaluateFormRef" label-width="100px">
        <el-form-item label="评分" prop="score">
          <el-input-number v-model="evaluateForm.score" :min="0" :max="10" :step="0.5" />
        </el-form-item>
        <el-form-item label="评价" prop="evaluation">
          <el-input v-model="evaluateForm.evaluation" type="textarea" rows="4" placeholder="请输入面试评价" />
        </el-form-item>
        <el-form-item label="结果" prop="result">
          <el-select v-model="evaluateForm.result">
            <el-option label="通过" value="pass" />
            <el-option label="淘汰" value="fail" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluateVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEvaluate">提交评价</el-button>
      </template>
    </el-dialog>

    <!-- 发放Offer弹窗 -->
    <el-dialog v-model="offerVisible" title="发放Offer" width="500px" @closed="offerFormRef?.resetFields()">
      <el-form :model="offerForm" :rules="offerRules" ref="offerFormRef" label-width="100px">
        <el-form-item label="薪资范围" prop="offerSalary">
          <el-input v-model="offerForm.offerSalary" placeholder="如：15K-20K" />
        </el-form-item>
        <el-form-item label="入职日期" prop="offerDate">
          <el-date-picker v-model="offerForm.offerDate" type="date" value-format="YYYY-MM-DD" placeholder="选择入职日期" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="offerForm.remark" type="textarea" rows="3" placeholder="如：试用期3个月" />
        </el-form-item>
        <el-form-item label="通知模板">
          <el-select v-model="offerForm.templateId" placeholder="选择通知模板（可选）" clearable style="width:100%">
            <el-option v-for="t in templateList" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="offerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitOffer">确认发放</el-button>
      </template>
    </el-dialog>
    <!-- 淘汰弹窗 -->
    <el-dialog v-model="eliminateVisible" title="淘汰候选人" width="500px">
      <el-form :model="eliminateForm" label-width="100px">
        <el-form-item label="淘汰原因">
          <el-input v-model="eliminateForm.reason" type="textarea" rows="3" placeholder="请输入淘汰原因" />
        </el-form-item>
        <el-form-item label="通知模板">
          <el-select v-model="eliminateForm.templateId" placeholder="选择通知模板（可选）" clearable style="width:100%">
            <el-option v-for="t in templateList" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="eliminateVisible = false">取消</el-button>
        <el-button type="danger" @click="handleSubmitEliminate">确定淘汰</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { interviewApi, resumeApi, notifyTemplateApi } from '@/api/modules/recruitment'
import { getUserList } from '@/api/system/user'
import type { Interview, InterviewForm, InterviewEvaluateForm, OfferRequest, Resume, User, NotifyTemplate } from '@/api/types'

const tableData = ref<Interview[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const evaluateVisible = ref(false)
const offerVisible = ref(false)
const eliminateVisible = ref(false)
const formRef = ref()
const evaluateFormRef = ref()
const offerFormRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const evaluateId = ref(0)
const offerId = ref(0)
const eliminateId = ref(0)

const resumeList = ref<Resume[]>([])
const interviewerList = ref<User[]>([])
const templateList = ref<NotifyTemplate[]>([])

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const query = reactive<{ keyword: string; result: string; interviewerId: number | null; dateRange: [string, string] | null }>({
  keyword: '',
  result: '',
  interviewerId: null,
  dateRange: null
})

const form = reactive<InterviewForm>({ resumeId: 0, interviewRound: 1, interviewerId: 0, interviewTime: '', location: '' })
const evaluateForm = reactive<InterviewEvaluateForm>({ score: 0, evaluation: '', result: 'pass' })
const offerForm = reactive<OfferRequest & { templateId?: number }>({ offerSalary: '', offerDate: '', remark: '', templateId: undefined })
const eliminateForm = reactive({ reason: '', templateId: undefined as number | undefined })

const rules = {
  resumeId: [{ required: true, message: '请选择候选人', trigger: 'change' }],
  interviewTime: [{ required: true, message: '请选择面试时间', trigger: 'change' }]
}
const evaluateRules = {
  score: [{ required: true, message: '请输入评分', trigger: 'blur' }],
  evaluation: [{ required: true, message: '请输入评价', trigger: 'blur' }],
  result: [{ required: true, message: '请选择结果', trigger: 'change' }]
}
const offerRules = {
  offerSalary: [{ required: true, message: '请输入薪资范围', trigger: 'blur' }],
  offerDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

const resultMap: Record<string, string> = { pending: '待面试', pass: '通过', fail: '淘汰', offer: '已发Offer', hired: '已入职' }

const resultTagType = (result: string) => {
  const types: Record<string, string> = { pending: 'warning', pass: 'success', fail: 'danger', offer: 'primary', hired: '' }
  return types[result] || 'info'
}

// ---- 数据加载 ----
const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: pagination.page, pageSize: pagination.pageSize }
    if (query.keyword) params.keyword = query.keyword
    if (query.result) params.result = query.result
    if (query.interviewerId) params.interviewerId = query.interviewerId
    if (query.dateRange) {
      params.interviewDateStart = query.dateRange[0]
      params.interviewDateEnd = query.dateRange[1]
    }
    const res = await interviewApi.list(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const loadResumeList = async () => {
  try {
    const res = await resumeApi.list({ page: 1, pageSize: 200 })
    resumeList.value = res.data.records
  } catch { /* ignore */ }
}

const loadInterviewerList = async () => {
  try {
    const res = await getUserList({ page: 1, pageSize: 200 })
    interviewerList.value = res.data.records
  } catch { /* ignore */ }
}

const loadTemplateList = async () => {
  try {
    const res = await notifyTemplateApi.list({ page: 1, pageSize: 200 })
    templateList.value = res.data.records?.filter((t: NotifyTemplate) => t.status === 1) || []
  } catch { /* ignore */ }
}

// ---- 搜索/筛选 ----
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  query.keyword = ''
  query.result = ''
  query.interviewerId = null
  query.dateRange = null
  pagination.page = 1
  loadData()
}

// ---- 安排/编辑面试 ----
const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  Object.assign(form, { resumeId: 0, interviewRound: 1, interviewerId: 0, interviewTime: '', location: '' })
  dialogVisible.value = true
}

const handleEdit = (row: Interview) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    resumeId: row.resumeId,
    interviewRound: row.interviewRound,
    interviewerId: row.interviewerId,
    interviewTime: row.interviewTime,
    location: row.location
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) {
      await interviewApi.update(editId.value, form)
    } else {
      await interviewApi.create(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  })
}

// ---- 签到 ----
const handleCheckin = async (row: Interview) => {
  await interviewApi.checkin(row.id)
  ElMessage.success('签到成功')
  loadData()
}

// ---- 评价 ----
const handleEvaluate = (row: Interview) => {
  evaluateId.value = row.id
  Object.assign(evaluateForm, {
    score: row.score || 0,
    evaluation: row.evaluation || '',
    result: row.result === 'fail' ? 'fail' : 'pass'
  })
  evaluateVisible.value = true
}

const handleSubmitEvaluate = async () => {
  await evaluateFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    await interviewApi.evaluate(evaluateId.value, evaluateForm)
    ElMessage.success('评价提交成功')
    evaluateVisible.value = false
    loadData()
  })
}

// ---- 通过 ----
const handlePass = async (row: Interview) => {
  await ElMessageBox.confirm(`确定通过候选人「${row.candidateName}」的面试？`, '确认通过', { type: 'warning' })
  await interviewApi.pass(row.id)
  ElMessage.success('已通过')
  loadData()
}

// ---- 淘汰 ----
const handleEliminate = (row: Interview) => {
  eliminateId.value = row.id
  Object.assign(eliminateForm, { reason: '', templateId: undefined })
  eliminateVisible.value = true
}

const handleSubmitEliminate = async () => {
  if (!eliminateForm.reason) { ElMessage.warning('请输入淘汰原因'); return }
  await interviewApi.eliminate(eliminateId.value, { reason: eliminateForm.reason })
  ElMessage.success('已淘汰')
  eliminateVisible.value = false
  loadData()
}

// ---- 发放Offer ----
const handleOffer = (row: Interview) => {
  offerId.value = row.id
  Object.assign(offerForm, { offerSalary: '', offerDate: '', remark: '', templateId: undefined })
  offerVisible.value = true
}

const handleSubmitOffer = async () => {
  await offerFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    await interviewApi.offer(offerId.value, {
      offerSalary: offerForm.offerSalary,
      offerDate: offerForm.offerDate,
      remark: offerForm.remark
    })
    ElMessage.success('Offer已发放')
    offerVisible.value = false
    loadData()
  })
}

// ---- 确认入职 ----
const handleConfirmHire = async (row: Interview) => {
  await ElMessageBox.confirm(`确定候选人「${row.candidateName}」已入职？`, '确认入职', { type: 'warning' })
  await interviewApi.confirmHire(row.id)
  ElMessage.success('已确认入职')
  loadData()
}

onMounted(() => {
  loadData()
  loadResumeList()
  loadInterviewerList()
  loadTemplateList()
})
</script>

<style lang="scss" scoped>
.interview-list {
  .toolbar {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    .toolbar-left {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }
    .toolbar-right {
      display: flex;
      gap: 12px;
    }
  }
  .text-gray {
    color: #909399;
    font-size: 13px;
  }
}
</style>