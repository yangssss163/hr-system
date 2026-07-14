<template>
  <div class="salary-sheet">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleGenerate">生成工资单</el-button>
        <el-button @click="handleExport">导出工资单</el-button>
      </div>
      <div class="search-bar">
        <el-date-picker v-model="searchForm.yearMonth" type="month" value-format="yyyy-MM" placeholder="选择月份" />
        <el-select v-model="searchForm.status" placeholder="状态">
          <el-option label="全部" :value="0" />
          <el-option label="待审核" value="pending" />
          <el-option label="已审核" value="approved" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="yearMonth" label="月份" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="baseSalary" label="基本工资" width="120">
          <template #default="{ row }">{{ row.baseSalary.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="totalIncome" label="应发合计" width="120">
          <template #default="{ row }">{{ row.totalIncome.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="totalDeduction" label="应扣合计" width="120">
          <template #default="{ row }">{{ row.totalDeduction.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="netSalary" label="实发工资" width="120">
          <template #default="{ row }"><el-tag type="danger">{{ row.netSalary.toLocaleString() }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="generateDialog" title="生成工资单" width="400px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="月份"><el-date-picker v-model="generateForm.month" type="month" value-format="yyyy-MM" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialog=false">取消</el-button>
        <el-button type="primary" @click="handleGenerateConfirm">确定生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { salarySheetApi } from '@/api/modules/salary'
import type { SalarySheet } from '@/api/types'

const tableData = ref<SalarySheet[]>([])
const loading = ref(false)
const generateDialog = ref(false)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ yearMonth: '', status: 0 })
const generateForm = reactive({ month: '', employeeIds: [] as number[] })

const statusMap: Record<string, { text: string; type: string }> = { pending: { text: '待审核', type: 'warning' }, approved: { text: '已审核', type: 'success' } }
const getStatusText = (status: string) => statusMap[status]?.text || status
const getStatusTag = (status: string) => statusMap[status]?.type || ''

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: pagination.page, pageSize: pagination.pageSize }
    if (searchForm.yearMonth) params.yearMonth = searchForm.yearMonth
    if (searchForm.status) params.status = searchForm.status
    const res = await salarySheetApi.list(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleGenerate = () => { generateForm.month = ''; generateForm.employeeIds = []; generateDialog.value = true }
const handleGenerateConfirm = async () => { await salarySheetApi.generate(generateForm); ElMessage.success('生成成功'); generateDialog.value = false; loadData() }
const handleExport = async () => { const res = await salarySheetApi.export({}); const url = window.URL.createObjectURL(new Blob([res.data])); const a = document.createElement('a'); a.href = url; a.download = '工资单.xlsx'; a.click(); URL.revokeObjectURL(url) }

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.salary-sheet {
  .toolbar { margin-bottom: 16px; }
  .search-bar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>