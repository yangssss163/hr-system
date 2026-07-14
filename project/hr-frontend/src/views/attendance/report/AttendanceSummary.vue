<template>
  <div class="attendance-summary">
    <el-card>
      <div class="toolbar">
        <el-date-picker v-model="searchForm.month" type="month" placeholder="选择月份" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="handleExport">导出报表</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="employeeId" label="ID" width="60" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="shouldWorkDays" label="应出勤天数" width="100" />
        <el-table-column prop="actualWorkDays" label="实际出勤天数" width="110" />
        <el-table-column prop="lateCount" label="迟到次数" width="80" />
        <el-table-column prop="earlyCount" label="早退次数" width="80" />
        <el-table-column prop="absentCount" label="缺勤次数" width="80" />
        <el-table-column prop="leaveCount" label="请假次数" width="80" />
        <el-table-column prop="overtimeHours" label="加班时长(小时)" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { attendanceReportApi } from '@/api/modules/attendance'

const tableData = ref<any[]>([])
const loading = ref(false)

const searchForm = reactive({ month: '' })

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = {}
    if (searchForm.month) params.month = searchForm.month
    const res = await attendanceReportApi.summary(params)
    tableData.value = res.data.records || []
  } finally { loading.value = false }
}

const handleExport = () => {
  ElMessage.success('导出功能开发中')
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.attendance-summary {
  .toolbar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>