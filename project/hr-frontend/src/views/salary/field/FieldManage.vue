<template>
  <div class="field-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">创建字段</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="字段名称" width="120" />
        <el-table-column prop="code" label="字段编码" width="120" />
        <el-table-column prop="typeName" label="类型" width="100">
          <template #default="{ row }"><el-tag :type="row.type === 'income' ? 'success' : 'warning'">{{ row.typeName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="formula" label="计算公式" min-width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字段' : '创建字段'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="字段名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="字段编码" prop="code"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="类型" prop="type"><el-select v-model="form.type"><el-option label="应发" value="income" /><el-option label="应扣" value="deduction" /></el-select></el-form-item>
        <el-form-item label="计算公式"><el-input v-model="form.formula" placeholder="如：base_salary * 0.1" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" :max="100" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item>
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
import { salaryFieldApi } from '@/api/modules/salary'
import type { SalaryField, SalaryFieldForm } from '@/api/types'

const tableData = ref<SalaryField[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<SalaryFieldForm>({ name: '', code: '', type: 'income', formula: '', sort: 0, status: 1 })
const rules = { name: [{ required: true, message: '请输入字段名称', trigger: 'blur' }], code: [{ required: true, message: '请输入字段编码', trigger: 'blur' }], type: [{ required: true, message: '请选择类型', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await salaryFieldApi.list()
    tableData.value = (res.data || []).sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', code: '', type: 'income', formula: '', sort: 0, status: 1 }); dialogVisible.value = true }
const handleEdit = (row: SalaryField) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: SalaryField) => { await ElMessageBox.confirm('确定删除？'); await salaryFieldApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await salaryFieldApi.update(editId.value, form)
    else await salaryFieldApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.field-manage {
  .toolbar { margin-bottom: 16px; }
}
</style>