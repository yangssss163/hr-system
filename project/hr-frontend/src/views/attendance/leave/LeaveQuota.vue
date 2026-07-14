<template>
  <div class="leave-quota">
    <el-card>
      <div class="toolbar">
        <el-select v-model="searchForm.year" style="width: 120px">
          <el-option v-for="y in years" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-input v-model="searchForm.keyword" placeholder="搜索员工姓名" style="width: 200px" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="leaveTypeName" label="假期类型" width="120" />
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column prop="totalDays" label="总天数" width="80" />
        <el-table-column prop="usedDays" label="已用天数" width="80" />
        <el-table-column prop="remainDays" label="剩余天数" width="80">
          <template #default="{ row }"><el-tag :type="row.remainDays > 0 ? 'success' : 'warning'">{{ row.remainDays }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button size="small" @click="handleAdjust(row)">调整额度</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="adjustVisible" title="调整假期额度" width="400px">
      <el-form :model="adjustForm" label-width="100px">
        <el-form-item label="员工姓名">{{ adjustForm.employeeName }}</el-form-item>
        <el-form-item label="假期类型">{{ adjustForm.leaveTypeName }}</el-form-item>
        <el-form-item label="当前额度">{{ adjustForm.totalDays }} 天</el-form-item>
        <el-form-item label="新额度"><el-input-number v-model="adjustForm.newDays" :min="0" :max="365" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible=false">取消</el-button>
        <el-button type="primary" @click="handleAdjustSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { leaveQuotaApi } from '@/api/modules/attendance'
import type { LeaveQuota } from '@/api/types'

const tableData = ref<LeaveQuota[]>([])
const loading = ref(false)
const adjustVisible = ref(false)

const currentYear = new Date().getFullYear()
const years = [currentYear - 1, currentYear, currentYear + 1]

const searchForm = reactive({ year: currentYear, keyword: '' })
const adjustForm = reactive({ id: 0, employeeName: '', leaveTypeName: '', totalDays: 0, newDays: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { year: searchForm.year }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    const res = await leaveQuotaApi.list(params)
    tableData.value = res.data || []
  } finally { loading.value = false }
}

const handleReset = () => { searchForm.year = currentYear; searchForm.keyword = ''; loadData() }

const handleAdjust = (row: LeaveQuota) => {
  Object.assign(adjustForm, { id: row.id, employeeName: row.employeeName, leaveTypeName: row.leaveTypeName, totalDays: row.totalDays, newDays: row.totalDays })
  adjustVisible.value = true
}

const handleAdjustSubmit = async () => {
  await leaveQuotaApi.adjust(adjustForm.id, { totalDays: adjustForm.newDays })
  ElMessage.success('调整成功')
  adjustVisible.value = false
  loadData()
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.leave-quota {
  .toolbar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>