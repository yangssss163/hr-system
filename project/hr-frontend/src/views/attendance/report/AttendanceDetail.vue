<template>
  <div class="attendance-detail">
    <el-card>
      <div class="toolbar">
        <el-input v-model="searchForm.keyword" placeholder="搜索姓名/工号" style="width: 200px" />
        <el-date-picker v-model="searchForm.dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
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
          <template #default="{ row }"><el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" @current-change="loadData" @size-change="handleSizeChange" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { attendanceReportApi } from '@/api/modules/attendance'
import type { AttendanceRecord } from '@/api/types'

const tableData = ref<AttendanceRecord[]>([])
const loading = ref(false)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ keyword: '', dateRange: [] as string[] })

const statusMap: Record<string, { text: string; tag: string }> = {
  normal: { text: '正常', tag: 'success' },
  late: { text: '迟到', tag: 'warning' },
  early: { text: '早退', tag: 'warning' },
  absent: { text: '缺勤', tag: 'danger' },
  overtime: { text: '加班', tag: 'primary' }
}

const getStatusText = (status: string) => statusMap[status]?.text || status
const getStatusTag = (status: string) => statusMap[status]?.tag || 'info'

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: pagination.page, pageSize: pagination.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.dateRange?.length === 2) {
      params.dateStart = searchForm.dateRange[0]
      params.dateEnd = searchForm.dateRange[1]
    }
    const res = await attendanceReportApi.detail(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch {
    tableData.value = []
    pagination.total = 0
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleSizeChange = () => { pagination.page = 1; loadData() }
const handleReset = () => { searchForm.keyword = ''; searchForm.dateRange = []; handleSearch() }

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.attendance-detail {
  .toolbar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>