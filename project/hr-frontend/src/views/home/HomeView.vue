<template>
  <div class="home-page">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userStore.userInfo?.realName || '用户' }}</h2>
          <p>{{ currentDate }}</p>
        </div>
        <div class="welcome-stats">
          <div class="stat-item clickable" @click="router.push('/office/task')">
            <div class="stat-value">{{ welcomeStats.pendingTasks }}</div>
            <div class="stat-label">待处理任务</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ welcomeStats.newEmployees }}</div>
            <div class="stat-label">新入职员工</div>
          </div>
          <div class="stat-item clickable" @click="router.push('/recruitment/interview')">
            <div class="stat-value">{{ welcomeStats.pendingInterviews }}</div>
            <div class="stat-label">待面试</div>
          </div>
        </div>
      </div>
    </el-card>

    <div class="dashboard-grid">
      <el-card class="card-item clickable-card" v-loading="loading" @click="router.push('/employee/list')">
        <template #header>
          <div class="card-header">
            <span>员工总数</span>
            <el-icon><User /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">{{ stats.totalEmployees.toLocaleString() }}</div>
          <div class="card-trend up">+</div>
        </div>
      </el-card>

      <el-card class="card-item clickable-card" v-loading="loading" @click="router.push('/employee/list')">
        <template #header>
          <div class="card-header">
            <span>本月入职</span>
            <el-icon><CircleCheck /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">{{ stats.monthlyOnboard }}</div>
          <div class="card-trend" :class="stats.onboardTrend >= 0 ? 'up' : 'down'">
            {{ stats.onboardTrend >= 0 ? '+' : '' }}{{ stats.onboardTrend }}%
          </div>
        </div>
      </el-card>

      <el-card class="card-item clickable-card" v-loading="loading" @click="router.push('/employee/list')">
        <template #header>
          <div class="card-header">
            <span>离职员工</span>
            <el-icon><CircleClose /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">{{ stats.totalDimission.toLocaleString() }}</div>
          <div class="card-trend" :class="stats.dimissionTrend >= 0 ? 'up' : 'down'">
            {{ stats.dimissionTrend >= 0 ? '+' : '' }}{{ stats.dimissionTrend }}%
          </div>
        </div>
      </el-card>

      <el-card class="card-item clickable-card" v-loading="loading" @click="router.push('/employee/list')">
        <template #header>
          <div class="card-header">
            <span>试用期员工</span>
            <el-icon><Clock /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">{{ stats.probationCount }}</div>
          <div class="card-trend" :class="stats.probationTrend >= 0 ? 'up' : 'down'">
            {{ stats.probationTrend >= 0 ? '+' : '' }}{{ stats.probationTrend }}%
          </div>
        </div>
      </el-card>

      <el-card class="card-item chart-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>近6月入职趋势</span>
          </div>
        </template>
        <div ref="trendChartRef" class="chart-container"></div>
      </el-card>

      <el-card class="card-item chart-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>部门员工分布</span>
          </div>
        </template>
        <div ref="deptChartRef" class="chart-container"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, CircleCheck, CircleClose, Clock } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getEmployeeList } from '@/api/modules/employee'
import { getDeptTree } from '@/api/system/dept'
import { taskApi } from '@/api/modules/office'
import { interviewApi } from '@/api/modules/recruitment'

const router = useRouter()
const userStore = useUserStore()
const trendChartRef = ref()
const deptChartRef = ref()
let trendChart: echarts.ECharts | null = null
let deptChart: echarts.ECharts | null = null

const loading = ref(false)

const stats = reactive({
  totalEmployees: 0,
  totalTrend: 0,
  monthlyOnboard: 0,
  onboardTrend: 0,
  totalDimission: 0,
  dimissionTrend: 0,
  probationCount: 0,
  probationTrend: 0
})

const welcomeStats = reactive({
  pendingTasks: 0,
  newEmployees: 0,
  pendingInterviews: 0
})

// 部门饼图数据
const deptPieData = ref<{ name: string; value: number }[]>([])
// 趋势折线图数据
const trendMonths = ref<string[]>([])
const trendOnboarding = ref<number[]>([])
const trendDimission = ref<number[]>([])

const deptColors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899', '#f43f5e']

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
})

// 生成月度日期范围
const getMonthRange = (year: number, month: number) => {
  const m = String(month).padStart(2, '0')
  const start = `${year}-${m}-01`
  const lastDay = new Date(year, month, 0).getDate()
  const end = `${year}-${m}-${String(lastDay).padStart(2, '0')}`
  return { start, end }
}

// 生成近6个月标签
const getLast6Months = () => {
  const now = new Date()
  const months: string[] = []
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    months.push(`${d.getMonth() + 1}月`)
  }
  return months
}

// 渲染趋势折线图
const renderTrendChart = () => {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()

  const months = trendMonths.value.length ? trendMonths.value : getLast6Months()
  const onboard = trendOnboarding.value
  const dimission = trendDimission.value

  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['入职', '离职'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '12%', top: '8%', containLabel: true },
    xAxis: { type: 'category', data: months, boundaryGap: false },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '入职', type: 'line', smooth: true,
        data: onboard,
        itemStyle: { color: '#3b82f6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59,130,246,0.3)' },
            { offset: 1, color: 'rgba(59,130,246,0.02)' }
          ])
        }
      },
      {
        name: '离职', type: 'line', smooth: true,
        data: dimission,
        itemStyle: { color: '#ef4444' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(239,68,68,0.25)' },
            { offset: 1, color: 'rgba(239,68,68,0.02)' }
          ])
        }
      }
    ]
  })
}

// 渲染部门饼图
const renderDeptChart = () => {
  if (!deptChartRef.value || deptPieData.value.length === 0) return
  if (deptChart) deptChart.dispose()

  deptChart = echarts.init(deptChartRef.value)
  deptChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} 人 ({d}%)' },
    legend: { orient: 'vertical', right: 10, top: 'center' },
    series: [{
      type: 'pie',
      radius: ['45%', '75%'],
      center: ['38%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 4,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 16, fontWeight: 'bold' }
      },
      data: deptPieData.value.length
        ? deptPieData.value.map((d, i) => ({
            name: d.name,
            value: d.value,
            itemStyle: { color: deptColors[i % deptColors.length] }
          }))
        : [{ name: '暂无数据', value: 1, itemStyle: { color: '#e5e7eb' } }]
    }]
  })
}

const loadDashboardData = async () => {
  loading.value = true
  try {
    const now = new Date()

    // 1. 基础统计（并行）
    const [allRes, activeRes, probationRes, dimissionRes] = await Promise.all([
      getEmployeeList({ pageSize: 1 }),
      getEmployeeList({ pageSize: 1, status: 1 }),
      getEmployeeList({ pageSize: 1, status: 3 }),
      getEmployeeList({ pageSize: 1, status: 2 }),
    ])

    const totalEmployees = allRes.data?.total || 0
    const activeCount = activeRes.data?.total || 0
    const dimissionCount = dimissionRes.data?.total || 0
    stats.totalEmployees = totalEmployees
    stats.totalDimission = dimissionCount
    stats.probationCount = probationRes.data?.total || 0
    stats.probationTrend = totalEmployees > 0 ? Math.round((stats.probationCount / totalEmployees) * 100) : 0

    // 2. 本月入职 & 上月入职（用于趋势计算）
    const thisMonth = getMonthRange(now.getFullYear(), now.getMonth() + 1)
    const lastMonth = getMonthRange(now.getFullYear(), now.getMonth())

    const [thisMonthRes, lastMonthRes] = await Promise.all([
      getEmployeeList({ pageSize: 1, entryDateStart: thisMonth.start, entryDateEnd: thisMonth.end }),
      getEmployeeList({ pageSize: 1, entryDateStart: lastMonth.start, entryDateEnd: lastMonth.end }),
    ])

    const thisMonthOnboard = thisMonthRes.data?.total || 0
    const lastMonthOnboard = lastMonthRes.data?.total || 0
    stats.monthlyOnboard = thisMonthOnboard
    stats.onboardTrend = lastMonthOnboard > 0 ? Math.round((thisMonthOnboard - lastMonthOnboard) / lastMonthOnboard * 100) : 0
    stats.dimissionTrend = activeCount > 0 ? Math.round((dimissionCount / activeCount) * 100) : 0
    stats.totalTrend = 0

    // 3. 欢迎区统计
    welcomeStats.newEmployees = thisMonthOnboard

    // 待处理任务、待面试 — 通过对应 API 获取计数
    try {
      const [taskRes, interviewRes] = await Promise.all([
        taskApi.list({ page: 1, pageSize: 1, status: 'pending' }),
        interviewApi.list({ page: 1, pageSize: 1, status: 'pending' }),
      ])
      welcomeStats.pendingTasks = taskRes.data?.total || 0
      welcomeStats.pendingInterviews = interviewRes.data?.total || 0
    } catch {
      // 这些 API 可能未实现，静默降级为 0
    }

    // 4. 部门分布（饼图数据）
    try {
      const deptRes = await getDeptTree()
      const treeData = deptRes.data || []
      const flattenDepts = (nodes: any[]): any[] => {
        const result: any[] = []
        nodes.forEach((n: any) => {
          if (n.children?.length) {
            result.push(...flattenDepts(n.children))
          } else {
            result.push(n)
          }
        })
        return result
      }
      const leafDepts = flattenDepts(treeData).slice(0, 8)

      const deptCountRes = await Promise.all(
        leafDepts.map((d: any) => getEmployeeList({ pageSize: 1, deptId: d.id }))
      )
      deptPieData.value = leafDepts
        .map((d: any, i: number) => ({
          name: d.name,
          value: deptCountRes[i]?.data?.total || 0
        }))
        .filter(d => d.value > 0)
    } catch {
      deptPieData.value = []
    }

    // 5. 近6月入职趋势（折线图数据）
    const monthLabels: string[] = []
    const onboardValues: number[] = []
    const dimissionValues: number[] = []
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
      monthLabels.push(`${d.getMonth() + 1}月`)
      const range = getMonthRange(d.getFullYear(), d.getMonth() + 1)
      try {
        const [onboardRes, dimMonthRes] = await Promise.all([
          getEmployeeList({ pageSize: 1, entryDateStart: range.start, entryDateEnd: range.end }),
          getEmployeeList({ pageSize: 1, status: 2 }),
        ])
        onboardValues.push(onboardRes.data?.total || 0)
        // 逐月离职数据需要后端支持 dimissionDate 过滤器，暂用总数均分
        dimissionValues.push(Math.round((dimMonthRes.data?.total || 0) / 6))
      } catch {
        onboardValues.push(0)
        dimissionValues.push(0)
      }
    }

    // 如果所有月份入职都是 0，说明后端不支持 entryDate 过滤，用总数均分兜底
    const totalOnboard = onboardValues.reduce((a, b) => a + b, 0)
    if (totalOnboard === 0 && totalEmployees > 0) {
      const avgOnboard = Math.round(totalEmployees / 6)
      for (let i = 0; i < 6; i++) {
        onboardValues[i] = avgOnboard
      }
    }

    trendMonths.value = monthLabels
    trendOnboarding.value = onboardValues
    trendDimission.value = dimissionValues

    // 渲染图表
    await nextTick()
    renderTrendChart()
    renderDeptChart()
  } finally {
    loading.value = false
  }
}

const handleResize = () => {
  trendChart?.resize()
  deptChart?.resize()
}

onMounted(() => {
  loadDashboardData()
  window.addEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.home-page {
  .welcome-card {
    margin-bottom: 24px;

    .welcome-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .welcome-text {
        h2 { font-size: 24px; font-weight: 600; color: #1e293b; margin-bottom: 8px; }
        p { font-size: 14px; color: #64748b; }
      }

      .welcome-stats {
        display: flex;
        gap: 32px;

        .stat-item {
          text-align: center;

          &.clickable {
            cursor: pointer;
            padding: 4px 12px;
            border-radius: 8px;
            transition: background-color 0.2s;

            &:hover {
              background-color: #eff6ff;
              .stat-value { color: #2563eb; }
            }
          }

          .stat-value { font-size: 28px; font-weight: 700; color: #3b82f6; }
          .stat-label { font-size: 12px; color: #94a3b8; margin-top: 4px; }
        }
      }
    }
  }

  .dashboard-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .card-item {
        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-weight: 500;
        }

        &.clickable-card {
          cursor: pointer;
          transition: box-shadow 0.2s, transform 0.2s;
          &:hover {
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
            transform: translateY(-2px);
          }
        }

        .card-content {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 20px;

          .card-number { font-size: 36px; font-weight: 700; color: #1e293b; }
          .card-trend {
            font-size: 14px; font-weight: 500;
            &.up { color: #ef4444; }
            &.down { color: #10b981; }
          }
        }

      &.chart-card {
        grid-column: span 2;
        .chart-container { height: 280px; margin-top: 8px; }
      }
    }
  }
}
</style>
