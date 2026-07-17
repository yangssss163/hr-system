<template>
  <div class="oa-flow-manage">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'attendance:oa:import'" type="primary" @click="handleImport">导入OA单据</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="typeName" label="类型" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="duration" label="时长(天)" width="100" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'pending' ? 'warning' : 'info'">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="importVisible" title="导入OA单据" width="500px">
      <div class="import-area">
        <el-upload ref="uploadRef" class="upload-demo" :show-file-list="false" :auto-upload="false" :on-change="handleFileChange" accept=".xlsx,.xls,.csv">
          <el-button type="primary">点击上传文件</el-button>
        </el-upload>
      </div>
      <template #footer><el-button @click="handleCloseImport">关闭</el-button></template>
    </el-dialog>
    <el-dialog v-model="detailVisible" title="OA流程详情" width="500px">
      <el-descriptions :column="2" v-if="detailData">
        <el-descriptions-item label="姓名">{{ detailData.employeeName }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detailData.typeName }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ detailData.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detailData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ detailData.duration }} 天</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.statusName }}</el-descriptions-item>
      </el-descriptions>
      <template #footer><el-button @click="detailVisible=false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { oaFlowApi } from '@/api/modules/attendance'
import type { OaFlow } from '@/api/types'

const tableData = ref<OaFlow[]>([])
const loading = ref(false)
const importVisible = ref(false)
const detailVisible = ref(false)
const uploadRef = ref()
const detailData = ref<OaFlow | null>(null)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await oaFlowApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleImport = () => importVisible.value = true
const handleCloseImport = () => { importVisible.value = false; uploadRef.value?.clearFiles() }
const handleFileChange = async (file: any) => {
  const formData = new FormData()
  formData.append('file', file.raw)
  await oaFlowApi.import(formData)
  ElMessage.success('导入成功')
  importVisible.value = false
  uploadRef.value?.clearFiles()
  loadData()
}

const handleDetail = async (row: OaFlow) => {
  detailData.value = (await oaFlowApi.detail(row.id)).data
  detailVisible.value = true
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.oa-flow-manage {
  .toolbar { margin-bottom: 16px; }
}
</style>