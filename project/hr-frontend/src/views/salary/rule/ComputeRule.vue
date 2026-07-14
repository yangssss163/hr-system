<template>
  <div class="compute-rule">
    <el-card>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="typeName" label="规则类型" width="150" />
        <el-table-column prop="value" label="数值" width="100">
          <template #default="{ row }">{{ row.value }} {{ row.unit }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button size="small" @click="handleEdit(row)">编辑</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" title="编辑核算规则" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="规则类型">{{ form.typeName }}</el-form-item>
        <el-form-item label="数值"><el-input-number v-model="form.value" :min="0" :max="999999" /> <span>{{ form.unit }}</span></el-form-item>
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
import { salaryRuleApi } from '@/api/modules/salary'
import type { SalaryRule } from '@/api/types'

const tableData = ref<SalaryRule[]>([])
const loading = ref(false)
const dialogVisible = ref(false)

const form = reactive({ id: 0, typeName: '', value: 0, unit: '', remark: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await salaryRuleApi.list()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

const handleEdit = (row: SalaryRule) => {
  Object.assign(form, { id: row.id, typeName: row.typeName, value: row.value, unit: row.unit, remark: row.remark })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await salaryRuleApi.update(form.id, { value: form.value, remark: form.remark })
  ElMessage.success('修改成功')
  dialogVisible.value = false
  loadData()
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.compute-rule {
}
</style>