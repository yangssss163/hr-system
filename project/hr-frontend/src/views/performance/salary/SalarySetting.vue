<template>
  <div class="salary-setting">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">创建设置</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="levelName" label="绩效等级" width="100">
          <template #default="{ row }"><el-tag :type="getLevelTag(row.levelName)">{{ row.levelName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="salaryRange" label="绩效工资范围" width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设置' : '创建设置'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="绩效等级" prop="levelId"><el-select v-model="form.levelId"><el-option v-for="l in levels" :key="l.id" :label="l.name" :value="l.id" /></el-select></el-form-item>
        <el-form-item label="工资范围" prop="salaryRange"><el-input v-model="form.salaryRange" placeholder="如：5000-8000" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" :max="100" /></el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { perfSalaryApi, perfLevelApi } from '@/api/modules/performance'
import type { PerfSalary, PerfSalaryForm, PerfLevel } from '@/api/types'

const tableData = ref<PerfSalary[]>([])
const levels = ref<PerfLevel[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<PerfSalaryForm>({ levelId: 0, salaryRange: '', sort: 0 })
const rules = { levelId: [{ required: true, message: '请选择绩效等级', trigger: 'blur' }], salaryRange: [{ required: true, message: '请输入工资范围', trigger: 'blur' }] }

const levelTagMap: Record<string, string> = { S: 'danger', A: 'warning', B: 'primary', C: 'info', D: '' }
const getLevelTag = (name: string) => levelTagMap[name] || ''

const loadData = async () => {
  loading.value = true
  try {
    const salaryRes = await perfSalaryApi.list()
    tableData.value = (salaryRes.data || []).sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
    const levelRes = await perfLevelApi.list()
    levels.value = levelRes.data || []
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { levelId: 0, salaryRange: '', sort: 0 }); dialogVisible.value = true }
const handleEdit = (row: PerfSalary) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { levelId: row.levelId, salaryRange: row.salaryRange, sort: row.sort }); dialogVisible.value = true }
const handleDelete = async (row: PerfSalary) => { await ElMessageBox.confirm('确定删除？'); await perfSalaryApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await perfSalaryApi.update(editId.value, form)
    else await perfSalaryApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.salary-setting {
  .toolbar { margin-bottom: 16px; }
}
</style>