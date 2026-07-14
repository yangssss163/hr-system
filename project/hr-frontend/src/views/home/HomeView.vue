<template>
  <div class="home-page">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userStore.userInfo?.realName || '用户' }}</h2>
          <p>{{ currentDate }}</p>
        </div>
        <div class="welcome-stats">
          <div class="stat-item">
            <div class="stat-value">12</div>
            <div class="stat-label">待处理任务</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">5</div>
            <div class="stat-label">新入职员工</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">8</div>
            <div class="stat-label">待面试</div>
          </div>
        </div>
      </div>
    </el-card>

    <div class="dashboard-grid">
      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <span>员工总数</span>
            <el-icon><User /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">1,234</div>
          <div class="card-trend positive">+12.5%</div>
        </div>
      </el-card>

      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <span>本月入职</span>
            <el-icon><CircleCheck /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">45</div>
          <div class="card-trend positive">+8.3%</div>
        </div>
      </el-card>

      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <span>本月离职</span>
            <el-icon><CircleClose /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">12</div>
          <div class="card-trend negative">-5.2%</div>
        </div>
      </el-card>

      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <span>试用期员工</span>
            <el-icon><Clock /></el-icon>
          </div>
        </template>
        <div class="card-content">
          <div class="card-number">38</div>
          <div class="card-trend positive">+3.1%</div>
        </div>
      </el-card>

      <el-card class="card-item chart-card">
        <template #header>
          <div class="card-header">
            <span>入职趋势</span>
          </div>
        </template>
        <div ref="chartRef" class="chart-container"></div>
      </el-card>

      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <span>部门分布</span>
          </div>
        </template>
        <div class="dept-list">
          <div v-for="dept in deptList" :key="dept.name" class="dept-item">
            <span class="dept-name">{{ dept.name }}</span>
            <el-progress :percentage="dept.percentage" :color="dept.color" :show-text="false" />
            <span class="dept-count">{{ dept.count }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { User, CircleCheck, CircleClose, Clock } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const userStore = useUserStore()
const chartRef = ref()

const currentDate = computed(() => {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  return now.toLocaleDateString('zh-CN', options)
})

const deptList = ref([
  { name: '技术部', count: 456, percentage: 37, color: '#3b82f6' },
  { name: '产品部', count: 128, percentage: 10, color: '#10b981' },
  { name: '市场部', count: 189, percentage: 15, color: '#f59e0b' },
  { name: '人力资源部', count: 89, percentage: 7, color: '#ef4444' },
  { name: '财务部', count: 67, percentage: 5, color: '#8b5cf6' }
])

onMounted(() => {
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '入职',
        type: 'line',
        smooth: true,
        data: [32, 45, 38, 52, 48, 55],
        itemStyle: { color: '#3b82f6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
          ])
        }
      },
      {
        name: '离职',
        type: 'line',
        smooth: true,
        data: [12, 8, 15, 10, 14, 12],
        itemStyle: { color: '#ef4444' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(239, 68, 68, 0.3)' },
            { offset: 1, color: 'rgba(239, 68, 68, 0.05)' }
          ])
        }
      }
    ]
  })

  window.addEventListener('resize', () => {
    chart.resize()
  })
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
        h2 {
          font-size: 24px;
          font-weight: 600;
          color: #1e293b;
          margin-bottom: 8px;
        }

        p {
          font-size: 14px;
          color: #64748b;
        }
      }

      .welcome-stats {
        display: flex;
        gap: 32px;

        .stat-item {
          text-align: center;

          .stat-value {
            font-size: 28px;
            font-weight: 700;
            color: #3b82f6;
          }

          .stat-label {
            font-size: 12px;
            color: #94a3b8;
            margin-top: 4px;
          }
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

      .card-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 20px;

        .card-number {
          font-size: 36px;
          font-weight: 700;
          color: #1e293b;
        }

        .card-trend {
          font-size: 14px;
          font-weight: 500;

          &.positive {
            color: #10b981;
          }

          &.negative {
            color: #ef4444;
          }
        }
      }

      &.chart-card {
        grid-column: span 2;

        .chart-container {
          height: 200px;
          margin-top: 16px;
        }
      }

      .dept-list {
        margin-top: 16px;

        .dept-item {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;

          .dept-name {
            width: 80px;
            font-size: 14px;
            color: #475569;
          }

          .dept-count {
            width: 40px;
            font-size: 14px;
            color: #64748b;
            text-align: right;
          }
        }
      }
    }
  }
}
</style>