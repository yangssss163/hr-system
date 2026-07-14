<template>
  <div class="exception-manage">
    <el-card>
      <div class="search-bar">
        <el-select v-model="searchForm.type" placeholder="异常类型">
          <el-option label="全部" :value="0" />
          <el-option label="迟到" value="late" />
          <el-option label="早退" value="early" />
          <el-option label="缺勤" value="absent" />
        </el-select>
        <el-date-picker v-model="searchForm.date" type="date" placeholder="日期" />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="type" label="异常类型" width="120">
          <template #default="{ row }"><el-tag :type="getTypeTag(row.type)">{{ getTypeText(row.type) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 'pending' ? 'warning' : 'success'">{{ row.status === 'pending' ? '待处理' : '已处理' }}</el-tag></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
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
const searchForm = reactive({ type: 0, date: '' })

const typeMap: Record<string, { text: string; type: string }> = { late: { text: '迟到', type: 'warning' }, early: { text: '早退', type: 'warning' }, absent: { text: '缺勤', type: 'danger' } }
const getTypeText = (type: string) => typeMap[type]?.text || type
const getTypeTag = (type: string) => typeMap[type]?.type || ''

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: pagination.page, pageSize: pagination.pageSize }
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.date) params.date = searchForm.date
    const res = await attendanceExceptionApi.list(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.exception-manage {
  .search-bar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>