<template>
  <div class="perf-report">
    <el-card>
      <div class="toolbar">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="绩效明细表" name="detail" />
          <el-tab-pane label="部门绩效汇总" name="dept" />
          <el-tab-pane label="职员绩效汇总" name="employee" />
        </el-tabs>
      </div>
      <template v-if="activeTab === 'detail'">
        <div class="search-bar">
          <el-select v-model="searchForm.planId" placeholder="考核计划"><el-option label="全部" :value="0" /></el-select>
          <el-button type="primary" @click="loadDetail">查询</el-button>
        </div>
        <el-table :data="detailData" v-loading="loading">
          <el-table-column prop="planName" label="考核计划" width="150" />
          <el-table-column prop="employeeName" label="姓名" width="100" />
          <el-table-column prop="deptName" label="部门" width="120" />
          <el-table-column prop="totalScore" label="总分" width="100" />
          <el-table-column prop="levelName" label="等级" width="100">
            <template #default="{ row }"><el-tag :type="getLevelTag(row.levelName)">{{ row.levelName }}</el-tag></template>
          </el-table-column>
        </el-table>
      </template>
      <template v-if="activeTab === 'dept'">
        <el-table :data="deptData" v-loading="loading">
          <el-table-column prop="deptName" label="部门" width="150" />
          <el-table-column prop="totalCount" label="人数" width="80" />
          <el-table-column prop="avgScore" label="平均分" width="100" />
          <el-table-column prop="excellentCount" label="优秀人数" width="100" />
        </el-table>
      </template>
      <template v-if="activeTab === 'employee'">
        <el-table :data="employeeData" v-loading="loading">
          <el-table-column prop="employeeName" label="姓名" width="100" />
          <el-table-column prop="deptName" label="部门" width="120" />
          <el-table-column prop="totalScore" label="总分" width="100" />
          <el-table-column prop="levelName" label="等级" width="100">
            <template #default="{ row }"><el-tag :type="getLevelTag(row.levelName)">{{ row.levelName }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="rank" label="排名" width="80" />
        </el-table>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { perfReportApi } from '@/api/modules/performance'

const activeTab = ref('detail')
const loading = ref(false)

const detailData = ref<any[]>([])
const deptData = ref<any[]>([])
const employeeData = ref<any[]>([])

const searchForm = reactive({ planId: 0 })

const levelTagMap: Record<string, string> = { S: 'danger', A: 'warning', B: 'primary', C: 'info', D: '' }
const getLevelTag = (name: string) => levelTagMap[name] || ''

const loadDetail = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = {}
    if (searchForm.planId) params.planId = searchForm.planId
    const res = await perfReportApi.detail(params)
    detailData.value = res.data?.records || []
  } finally { loading.value = false }
}

const loadDeptSummary = async () => {
  loading.value = true
  try {
    deptData.value = (await perfReportApi.deptSummary({})).data || []
  } finally { loading.value = false }
}

const loadEmployeeSummary = async () => {
  loading.value = true
  try {
    employeeData.value = (await perfReportApi.employeeSummary({})).data || []
  } finally { loading.value = false }
}

onMounted(() => loadDetail())
</script>

<style lang="scss" scoped>
.perf-report {
  .search-bar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>