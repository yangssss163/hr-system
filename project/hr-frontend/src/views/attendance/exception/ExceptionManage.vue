<template>
  <div class="exception-manage">
    <el-card>
      <div class="search-bar">
        <el-select v-model="searchForm.type" placeholder="异常类型">
          <el-option label="全部" value="" />
          <el-option label="迟到" value="late" />
          <el-option label="早退" value="early" />
          <el-option label="缺勤" value="absent" />
        </el-select>
        <el-date-picker v-model="searchForm.date" type="date" value-format="YYYY-MM-DD" placeholder="日期" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="type" label="异常类型" width="120">
          <template #default="{ row }"><el-tag :type="getTypeTag(row.type)">{{ getTypeText(row.type) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="detail" label="描述" min-width="200" />
        <el-table-column prop="oaStatus" label="OA状态" width="100">
          <template #default="{ row }"><el-tag :type="row.oaStatus === '已审批' ? 'success' : 'warning'">{{ row.oaStatus }}</el-tag></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" @current-change="loadData" @size-change="handleSizeChange" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { attendanceExceptionApi } from '@/api/modules/attendance'
import type { AttendanceException } from '@/api/types'

const tableData = ref<AttendanceException[]>([])
const loading = ref(false)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ type: '', date: '' })

const typeMap: Record<string, { text: string; type: string }> = { late: { text: '迟到', type: 'warning' }, early: { text: '早退', type: 'warning' }, absent: { text: '缺勤', type: 'danger' } }
const getTypeText = (type: string) => typeMap[type]?.text || type
const getTypeTag = (type: string) => typeMap[type]?.type || 'info'

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: pagination.page, pageSize: pagination.pageSize }
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.date) params.date = searchForm.date
    const res = await attendanceExceptionApi.list(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch {
    tableData.value = []
    pagination.total = 0
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => { searchForm.type = ''; searchForm.date = ''; handleSearch() }
const handleSizeChange = () => { pagination.page = 1; loadData() }

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.exception-manage {
  .search-bar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>