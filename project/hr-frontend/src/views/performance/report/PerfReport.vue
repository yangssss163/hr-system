<template>
  <div class="perf-report">
    <el-card>
      <div class="toolbar">
        <el-tabs v-model="activeTab" @tab-change="onTabChange">
          <el-tab-pane label="绩效明细表" name="detail" />
          <el-tab-pane label="部门绩效汇总" name="dept" />
          <el-tab-pane label="职员绩效汇总" name="employee" />
        </el-tabs>
      </div>

      <!-- 筛选条件 -->
      <div class="search-bar">
        <el-select v-model="searchForm.planId" placeholder="考核计划" clearable>
          <el-option v-for="p in planOptions" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
        <el-button type="primary" @click="reload">查询</el-button>
      </div>

      <!-- 绩效明细表 -->
      <template v-if="activeTab === 'detail'">
        <el-table :data="detailData" v-loading="loading">
          <el-table-column prop="planName" label="考核计划" width="150" />
          <el-table-column prop="employeeName" label="姓名" width="100" />
          <el-table-column prop="deptName" label="部门" width="120" />
          <el-table-column prop="totalScore" label="总分" width="100" />
          <el-table-column prop="levelName" label="等级" width="100">
            <template #default="{ row }"><el-tag :type="getLevelTag(row.levelName)">{{ row.levelName }}</el-tag></template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="detailPagination.page"
          v-model:page-size="detailPagination.pageSize"
          :total="detailPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadDetail"
          @size-change="loadDetail"
          style="margin-top: 16px"
        />
      </template>

      <!-- 部门绩效汇总 -->
      <template v-if="activeTab === 'dept'">
        <el-table :data="deptData" v-loading="loading">
          <el-table-column prop="deptName" label="部门" width="150" />
          <el-table-column prop="totalCount" label="人数" width="80" />
          <el-table-column prop="avgScore" label="平均分" width="100" />
          <el-table-column prop="excellentCount" label="优秀人数" width="100" />
        </el-table>
      </template>

      <!-- 职员绩效汇总 -->
      <template v-if="activeTab === 'employee'">
        <el-table :data="employeeData" v-loading="loading">
          <el-table-column prop="rank" label="排名" width="80" />
          <el-table-column prop="employeeName" label="姓名" width="100" />
          <el-table-column prop="deptName" label="部门" width="120" />
          <el-table-column prop="totalScore" label="总分" width="100" />
          <el-table-column prop="levelName" label="等级" width="100">
            <template #default="{ row }"><el-tag :type="getLevelTag(row.levelName)">{{ row.levelName }}</el-tag></template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="employeePagination.page"
          v-model:page-size="employeePagination.pageSize"
          :total="employeePagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadEmployeeSummary"
          @size-change="loadEmployeeSummary"
          style="margin-top: 16px"
        />
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { perfReportApi, perfPlanApi } from '@/api/modules/performance'
import type { PerfPlan } from '@/api/types'

const activeTab = ref('detail')
const loading = ref(false)

const detailData = ref<any[]>([])
const deptData = ref<any[]>([])
const employeeData = ref<any[]>([])

const planOptions = ref<PerfPlan[]>([])

const searchForm = reactive({ planId: null as number | null })

const detailPagination = reactive({ page: 1, pageSize: 10, total: 0 })
const employeePagination = reactive({ page: 1, pageSize: 10, total: 0 })

const levelTagMap: Record<string, string> = { S: 'danger', A: 'warning', B: 'primary', C: 'info', D: '' }
const getLevelTag = (name: string) => levelTagMap[name] || ''

const loadPlans = async () => {
  const res = await perfPlanApi.list({ page: 1, pageSize: 100 })
  planOptions.value = res.data.records || []
}

const loadDetail = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: detailPagination.page, pageSize: detailPagination.pageSize }
    if (searchForm.planId) params.planId = searchForm.planId
    const res = await perfReportApi.detail(params)
    detailData.value = res.data?.records || []
    detailPagination.total = res.data?.total || 0
  } finally { loading.value = false }
}

const loadDeptSummary = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = {}
    if (searchForm.planId) params.planId = searchForm.planId
    const res = await perfReportApi.deptSummary(params)
    deptData.value = (res.data || []) as any[]
  } finally { loading.value = false }
}

const loadEmployeeSummary = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: employeePagination.page, pageSize: employeePagination.pageSize }
    if (searchForm.planId) params.planId = searchForm.planId
    const res = await perfReportApi.employeeSummary(params)
    employeeData.value = res.data?.records || []
    employeePagination.total = res.data?.total || 0
  } finally { loading.value = false }
}

const onTabChange = (tab: string | number) => {
  if (tab === 'detail') {
    detailPagination.page = 1
    loadDetail()
  } else if (tab === 'dept') {
    loadDeptSummary()
  } else if (tab === 'employee') {
    employeePagination.page = 1
    loadEmployeeSummary()
  }
}

const reload = () => {
  if (activeTab.value === 'detail') {
    detailPagination.page = 1
    loadDetail()
  } else if (activeTab.value === 'dept') {
    loadDeptSummary()
  } else if (activeTab.value === 'employee') {
    employeePagination.page = 1
    loadEmployeeSummary()
  }
}

onMounted(() => {
  loadPlans()
  loadDetail()
})
</script>

<style lang="scss" scoped>
.perf-report {
  .search-bar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>
