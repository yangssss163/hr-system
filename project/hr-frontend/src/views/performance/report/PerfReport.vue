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

      <!-- 绩效明细表 -->
      <template v-if="activeTab === 'detail'">
        <div class="search-bar">
          <el-select v-model="detailPlanId" placeholder="考核计划" clearable style="width:200px">
            <el-option v-for="p in planOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
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
        <el-pagination
          v-model:current-page="detailPagination.page" v-model:page-size="detailPagination.pageSize"
          :total="detailPagination.total" layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadDetail" @size-change="loadDetail"
        />
      </template>

      <!-- 部门绩效汇总 -->
      <template v-if="activeTab === 'dept'">
        <div class="search-bar">
          <el-select v-model="deptPlanId" placeholder="考核计划" clearable style="width:200px">
            <el-option v-for="p in planOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-table :data="deptData" v-loading="loading">
              <el-table-column prop="deptName" label="部门" width="150" />
              <el-table-column prop="totalCount" label="人数" width="80" />
              <el-table-column prop="avgScore" label="平均分" width="100" />
              <el-table-column prop="excellentCount" label="优秀人数" width="100" />
            </el-table>
          </el-col>
          <el-col :span="12">
            <div ref="deptChartRef" class="chart-container"></div>
          </el-col>
        </el-row>
      </template>

      <!-- 职员绩效汇总 -->
      <template v-if="activeTab === 'employee'">
        <div class="search-bar">
          <el-select v-model="employeePlanId" placeholder="考核计划" clearable style="width:200px">
            <el-option v-for="p in planOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </div>
        <el-row :gutter="20">
          <el-col :span="14">
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
              v-model:current-page="employeePagination.page" v-model:page-size="employeePagination.pageSize"
              :total="employeePagination.total" layout="total, sizes, prev, pager, next, jumper"
              @current-change="loadEmployeeSummary" @size-change="loadEmployeeSummary"
            />
          </el-col>
          <el-col :span="10">
            <div ref="levelChartRef" class="chart-container"></div>
          </el-col>
        </el-row>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { perfReportApi, perfPlanApi } from '@/api/modules/performance'
import type { PerfPlan } from '@/api/types'

const activeTab = ref('detail')
const loading = ref(false)

const planOptions = ref<PerfPlan[]>([])
const detailPlanId = ref<number | undefined>(undefined)
const deptPlanId = ref<number | undefined>(undefined)
const employeePlanId = ref<number | undefined>(undefined)

const detailData = ref<any[]>([])
const deptData = ref<any[]>([])
const employeeData = ref<any[]>([])
const deptChartRef = ref()
const levelChartRef = ref()
let deptChart: echarts.ECharts | null = null
let levelChart: echarts.ECharts | null = null

const detailPagination = reactive({ page: 1, pageSize: 10, total: 0 })
const employeePagination = reactive({ page: 1, pageSize: 10, total: 0 })

const levelTagMap: Record<string, string> = { S: 'danger', A: 'warning', B: 'primary', C: 'info', D: '' }
const getLevelTag = (name: string) => levelTagMap[name] || ''

const levelColors: Record<string, string> = { S: '#ef4444', A: '#f59e0b', B: '#3b82f6', C: '#10b981', D: '#94a3b8' }

const loadPlans = async () => {
  try {
    const res = await perfPlanApi.list({ page: 1, pageSize: 999 })
    planOptions.value = res.data?.records || []
  } catch { /* ignore */ }
}

const renderDeptChart = () => {
  if (!deptChartRef.value || deptData.value.length === 0) return
  if (deptChart) deptChart.dispose()
  deptChart = echarts.init(deptChartRef.value)
  deptChart.setOption({
    title: { text: '部门平均分对比', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: deptData.value.map(d => d.deptName), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', name: '平均分' },
    series: [{
      type: 'bar',
      data: deptData.value.map(d => d.avgScore),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3b82f6' }, { offset: 1, color: '#93c5fd' }
        ])
      }
    }],
    grid: { left: '10%', right: '5%', bottom: '15%', top: '15%' }
  })
}

const renderLevelChart = () => {
  if (!levelChartRef.value || employeeData.value.length === 0) return
  if (levelChart) levelChart.dispose()
  levelChart = echarts.init(levelChartRef.value)
  const levelCount: Record<string, number> = {}
  employeeData.value.forEach(e => {
    levelCount[e.levelName] = (levelCount[e.levelName] || 0) + 1
  })
  const pieData = Object.entries(levelCount).map(([name, value]) => ({ name, value }))
  levelChart.setOption({
    title: { text: '绩效等级分布', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '55%'],
      data: pieData,
      itemStyle: { color: (params: any) => levelColors[params.name] || '#94a3b8' },
      label: { formatter: '{b}\n{d}%' }
    }]
  })
}

const loadDetail = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: detailPagination.page, pageSize: detailPagination.pageSize }
    if (detailPlanId.value) params.planId = detailPlanId.value
    const res = await perfReportApi.detail(params)
    detailData.value = res.data?.records || []
    detailPagination.total = res.data?.total || 0
  } finally { loading.value = false }
}

const loadDeptSummary = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = {}
    if (deptPlanId.value) params.planId = deptPlanId.value
    deptData.value = (await perfReportApi.deptSummary(params)).data || []
    await nextTick()
    renderDeptChart()
  } finally { loading.value = false }
}

const loadEmployeeSummary = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { page: employeePagination.page, pageSize: employeePagination.pageSize }
    if (employeePlanId.value) params.planId = employeePlanId.value
    const res = await perfReportApi.employeeSummary(params)
    employeeData.value = res.data?.records || []
    employeePagination.total = res.data?.total || 0
    await nextTick()
    renderLevelChart()
  } finally { loading.value = false }
}

watch(activeTab, (val) => {
  if (val === 'dept') loadDeptSummary()
  else if (val === 'employee') loadEmployeeSummary()
})

// 筛选条件变化时重置页码并重新加载
watch(deptPlanId, () => loadDeptSummary())
watch(employeePlanId, () => { employeePagination.page = 1; loadEmployeeSummary() })

onMounted(() => { loadPlans(); loadDetail() })
onMounted(() => window.addEventListener('resize', () => { deptChart?.resize(); levelChart?.resize() }))
</script>

<style lang="scss" scoped>
.perf-report {
  .search-bar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
  .chart-container { width: 100%; height: 320px; }
}
</style>
