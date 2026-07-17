<template>
  <div class="deduction-setting">
    <el-card>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="typeName" label="扣款类型" width="120" />
        <el-table-column prop="deduction" label="扣款金额" width="100">
          <template #default="{ row }">{{ row.deduction }} {{ row.unit }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button v-permission="'attendance:deduction:update'" size="small" @click="handleEdit(row)">编辑</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" title="编辑扣款规则" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="扣款类型">{{ form.typeName }}</el-form-item>
        <el-form-item label="扣款金额"><el-input-number v-model="form.deduction" :min="0" :max="9999" /> <span>{{ form.unit }}</span></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { attendanceDeductionApi } from '@/api/modules/attendance'
import type { AttendanceDeduction } from '@/api/types'

const tableData = ref<AttendanceDeduction[]>([])
const loading = ref(false)
const dialogVisible = ref(false)

const form = reactive({ id: 0, typeName: '', deduction: 0, unit: '', remark: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await attendanceDeductionApi.list()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

const handleEdit = (row: AttendanceDeduction) => {
  Object.assign(form, { id: row.id, typeName: row.typeName, deduction: row.deduction, unit: row.unit, remark: row.remark })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await attendanceDeductionApi.update(form.id, { deduction: form.deduction, remark: form.remark })
  ElMessage.success('修改成功')
  dialogVisible.value = false
  loadData()
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.deduction-setting {
}
</style>