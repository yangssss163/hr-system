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
        <el-card title="渠道统计">
          <el-table :data="report.channelStats">
            <el-table-column prop="channel" label="渠道" />
            <el-table-column prop="count" label="数量" />
            <el-table-column prop="rate" label="占比">
              <template #default="{ row }">{{ row.rate }}%</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="月度统计">
          <el-table :data="report.monthlyStats">
            <el-table-column prop="month" label="月份" />
            <el-table-column prop="resumes" label="简历数" />
            <el-table-column prop="interviews" label="面试数" />
            <el-table-column prop="hired" label="入职数" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { recruitmentReportApi } from '@/api/modules/recruitment'
import type { RecruitmentReport } from '@/api/types'

const report = reactive<RecruitmentReport>({
  totalResumes: 0, totalInterviews: 0, totalPassed: 0, totalHired: 0, conversionRate: 0,
  channelStats: [], monthlyStats: []
})

const loadData = async () => {
  const res = await recruitmentReportApi.summary()
  Object.assign(report, res.data)
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.recruitment-report {
  .stat-item {
    text-align: center;
    .stat-label { color: #999; font-size: 14px; margin-bottom: 8px; }
    .stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
  }
}
</style>