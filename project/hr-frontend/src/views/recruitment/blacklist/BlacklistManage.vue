<template>
  <div class="blacklist-manage">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'recruitment:blacklist:create'" type="primary" @click="handleAdd">加入黑名单</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="reason" label="原因" width="200" />
        <el-table-column prop="createTime" label="加入时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-permission="'recruitment:blacklist:delete'" size="small" type="danger" @click="handleDelete(row)">移出</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>

    <!-- 加入黑名单弹窗 -->
    <el-dialog v-model="dialogVisible" title="加入黑名单" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="选择候选人" prop="resumeId">
          <el-select v-model="form.resumeId" filterable placeholder="搜索候选人姓名" style="width:100%" @change="handleCandidateChange">
            <el-option v-for="r in candidateList" :key="r.id" :label="`${r.name} - ${r.phone}`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" disabled />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" disabled />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="选填" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" rows="3" placeholder="请输入拉黑原因" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { blacklistApi, resumeApi } from '@/api/modules/recruitment'
import type { Blacklist, BlacklistForm, Resume } from '@/api/types'

const tableData = ref<Blacklist[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<BlacklistForm>({ resumeId: undefined, name: '', phone: '', idCard: '', reason: '' })
const rules = { resumeId: [{ required: true, message: '请选择候选人', trigger: 'change' }], reason: [{ required: true, message: '请输入原因', trigger: 'blur' }] }
const candidateList = ref<Resume[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await blacklistApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = (res.data.records || []).sort((a, b) => b.id - a.id)
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadCandidateList = async () => {
  try {
    const res = await resumeApi.list({ page: 1, pageSize: 200 })
    candidateList.value = (res.data.records || []).filter((r: Resume) => r.status === 'eliminated')
  } catch { /* ignore */ }
}

const handleAdd = () => { Object.assign(form, { resumeId: undefined, name: '', phone: '', idCard: '', reason: '' }); dialogVisible.value = true }

const handleCandidateChange = (resumeId: number) => {
  const selected = candidateList.value.find(r => r.id === resumeId)
  if (selected) {
    form.name = selected.name
    form.phone = selected.phone
  }
}

const handleDelete = async (row: Blacklist) => { await ElMessageBox.confirm('确定移出黑名单？'); await blacklistApi.delete(row.id); ElMessage.success('已移出'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    await blacklistApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadCandidateList() })
</script>

<style lang="scss" scoped>
.blacklist-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>
