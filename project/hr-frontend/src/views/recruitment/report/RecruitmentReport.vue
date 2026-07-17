<template>
  <div class="recruitment-report">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">简历总数</div>
            <div class="stat-value">{{ report.totalResumes }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">面试人数</div>
            <div class="stat-value">{{ report.totalInterviews }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">通过人数</div>
            <div class="stat-value">{{ report.totalPassed }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">入职人数</div>
            <div class="stat-value">{{ report.totalHired }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header><span>渠道统计</span></template>
          <div ref="channelChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>月度趋势</span></template>
          <div ref="monthlyChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { recruitmentReportApi } from '@/api/modules/recruitment'
import type { RecruitmentReport } from '@/api/types'

const report = reactive<RecruitmentReport>({
  totalResumes: 0, totalInterviews: 0, totalPassed: 0, totalHired: 0, conversionRate: 0,
  channelStats: [], monthlyStats: []
})
const channelChartRef = ref()
const monthlyChartRef = ref()
let channelChart: echarts.ECharts | null = null
let monthlyChart: echarts.ECharts | null = null

const renderChannelChart = () => {
  if (!channelChartRef.value || report.channelStats.length === 0) return
  if (channelChart) channelChart.dispose()
  channelChart = echarts.init(channelChartRef.value)
  channelChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '55%'],
      data: report.channelStats.map(c => ({ name: c.channel, value: c.count })),
      label: { formatter: '{b}\n{d}%' }
    }]
  })
}

const renderMonthlyChart = () => {
  if (!monthlyChartRef.value || report.monthlyStats.length === 0) return
  if (monthlyChart) monthlyChart.dispose()
  monthlyChart = echarts.init(monthlyChartRef.value)
  monthlyChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['简历数', '面试数', '入职数'], bottom: 0 },
    xAxis: { type: 'category', data: report.monthlyStats.map(m => m.month) },
    yAxis: { type: 'value' },
    series: [
      { name: '简历数', type: 'bar', data: report.monthlyStats.map(m => m.resumes), itemStyle: { color: '#3b82f6' } },
      { name: '面试数', type: 'bar', data: report.monthlyStats.map(m => m.interviews), itemStyle: { color: '#f59e0b' } },
      { name: '入职数', type: 'line', smooth: true, data: report.monthlyStats.map(m => m.hired), itemStyle: { color: '#10b981' } }
    ],
    grid: { left: '8%', right: '5%', bottom: '15%', top: '10%' }
  })
}

const loadData = async () => {
  const res = await recruitmentReportApi.summary()
  Object.assign(report, res.data)
  await nextTick()
  renderChannelChart()
  renderMonthlyChart()
}

onMounted(() => loadData())
onMounted(() => window.addEventListener('resize', () => { channelChart?.resize(); monthlyChart?.resize() }))
</script>

<style lang="scss" scoped>
.recruitment-report {
  .stat-item {
    text-align: center;
    .stat-label { color: #999; font-size: 14px; margin-bottom: 8px; }
    .stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
  }
  .chart-container { width: 100%; height: 320px; }
}
</style>