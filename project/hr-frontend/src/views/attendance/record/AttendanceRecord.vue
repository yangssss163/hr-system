<template>
  <div class="attendance-record">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleImport">导入打卡记录</el-button>
        <el-button @click="handleBatchFix">批量修正</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="checkIn" label="上班时间" width="160" />
        <el-table-column prop="checkOut" label="下班时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="100" />
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="importVisible" title="导入打卡记录" width="500px">
      <div class="import-area">
        <el-upload ref="uploadRef" class="upload-demo" :show-file-list="false" :auto-upload="false" :on-change="handleFileChange" accept=".xlsx,.xls,.csv">
          <el-button type="primary">点击上传文件</el-button>
        </el-upload>
        <div class="import-tips">
          <p>支持格式：.xlsx、.xls、.csv</p>
        </div>
      </div>
      <template #footer><el-button @click="handleCloseImport">关闭</el-button></template>
    </el-dialog>
    <el-dialog v-model="batchFixVisible" title="批量修正" width="500px">
      <el-form :model="batchFixForm" label-width="100px">
        <el-form-item label="上班时间"><el-time-picker v-model="batchFixForm.checkIn" format="HH:mm:ss" value-format="HH:mm:ss" /></el-form-item>
        <el-form-item label="下班时间"><el-time-picker v-model="batchFixForm.checkOut" format="HH:mm:ss" value-format="HH:mm:ss" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="batchFixForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchFixVisible=false">取消</el-button>
        <el-button type="primary" @click="handleBatchFixSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { attendanceRecordApi } from '@/api/modules/attendance'
import type { AttendanceRecord, BatchFixRequest } from '@/api/types'

const tableData = ref<AttendanceRecord[]>([])
const loading = ref(false)
const importVisible = ref(false)
const batchFixVisible = ref(false)
const uploadRef = ref()

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const batchFixForm = reactive<BatchFixRequest>({ ids: [], checkIn: '', checkOut: '', remark: '' })

const statusMap: Record<string, { text: string; tag: string }> = {
  normal: { text: '正常', tag: 'success' },
  late: { text: '迟到', tag: 'warning' },
  early: { text: '早退', tag: 'warning' },
  absent: { text: '缺勤', tag: 'danger' },
  overtime: { text: '加班', tag: 'primary' }
}

const getStatusText = (status: string) => statusMap[status]?.text || status
const getStatusTag = (status: string) => statusMap[status]?.tag || ''

const loadData = async () => {
  loading.value = true
  try {
    const res = await attendanceRecordApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleImport = () => importVisible.value = true
const handleCloseImport = () => { importVisible.value = false; uploadRef.value?.clearFiles() }
const handleFileChange = async (file: any) => {
  const formData = new FormData()
  formData.append('file', file.raw)
  await attendanceRecordApi.import(formData)
  ElMessage.success('导入成功')
  importVisible.value = false
  uploadRef.value?.clearFiles()
  loadData()
}

const handleBatchFix = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('暂无数据')
    return
  }
  batchFixForm.ids = tableData.value.slice(0, 10).map(r => r.id)
  batchFixVisible.value = true
}

const handleBatchFixSubmit = async () => {
  await attendanceRecordApi.batchFix(batchFixForm)
  ElMessage.success('批量修正成功')
  batchFixVisible.value = false
  loadData()
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.attendance-record {
  .toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
  .import-area {
    .upload-demo { margin-bottom: 16px; }
    .import-tips { color: #999; font-size: 12px; }
  }
}
</style>