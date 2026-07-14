<template>
  <div class="holiday-setting">
    <el-card>
      <div class="toolbar">
        <el-select v-model="currentYear" style="width: 120px">
          <el-option v-for="y in years" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-button type="primary" @click="handleAdd">创建假期</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="假期名称" width="120" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" @click="handleCopy(row)">复制</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改假期' : '创建假期'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="假期名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="日期" prop="date"><el-date-picker v-model="form.date" type="date" value-format="yyyy-MM-dd" /></el-form-item>
        <el-form-item label="天数"><el-input-number v-model="form.days" :min="1" :max="30" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="copyVisible" title="复制假期到其他年份" width="400px">
      <el-form :model="copyForm" label-width="100px">
        <el-form-item label="目标年份"><el-select v-model="copyForm.year"><el-option v-for="y in years.filter(yr => yr !== currentYear)" :key="y" :label="y + '年'" :value="y" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="copyVisible=false">取消</el-button>
        <el-button type="primary" @click="handleCopySubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { holidayApi } from '@/api/modules/attendance'
import type { Holiday, HolidayForm } from '@/api/types'

const tableData = ref<Holiday[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const copyVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const copyId = ref(0)

const currentYear = ref(new Date().getFullYear())
const years = [currentYear.value - 2, currentYear.value - 1, currentYear.value, currentYear.value + 1, currentYear.value + 2]

const form = reactive<HolidayForm>({ year: currentYear.value, name: '', date: '', days: 1 })
const copyForm = reactive({ year: 0 })
const rules = { name: [{ required: true, message: '请输入假期名称', trigger: 'blur' }], date: [{ required: true, message: '请选择日期', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await holidayApi.list(currentYear.value)
    tableData.value = res.data.items || []
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { year: currentYear.value, name: '', date: '', days: 1 }); dialogVisible.value = true }
const handleEdit = (row: Holiday) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { year: currentYear.value, name: row.name, date: row.date, days: row.days }); dialogVisible.value = true }
const handleDelete = async (row: Holiday) => { await ElMessageBox.confirm('确定删除？'); await holidayApi.delete(row.id); ElMessage.success('删除成功'); loadData() }
const handleCopy = (row: Holiday) => { copyId.value = row.id; copyForm.year = years.find(y => y !== currentYear.value) || 0; copyVisible.value = true }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    form.year = currentYear.value
    if (isEdit.value) await holidayApi.update(editId.value, form)
    else await holidayApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

const handleCopySubmit = async () => {
  await holidayApi.copy(copyId.value, copyForm.year)
  ElMessage.success('复制成功')
  copyVisible.value = false
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.holiday-setting {
  .toolbar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
}
</style>