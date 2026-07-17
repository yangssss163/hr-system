<template>
  <div class="leave-type-setting">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'attendance:leave-type:create'" type="primary" @click="handleAdd">创建假期类型</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="假期名称" width="120" />
        <el-table-column prop="code" label="编码" width="100" />
        <el-table-column prop="defaultDays" label="默认天数" width="100" />
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-permission="'attendance:leave-type:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'attendance:leave-type:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑假期类型' : '创建假期类型'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="假期名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码" prop="code"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="默认天数"><el-input-number v-model="form.defaultDays" :min="0" :max="365" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.enabled" /></el-form-item>
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
import { leaveTypeApi } from '@/api/modules/attendance'
import type { LeaveType, LeaveTypeForm } from '@/api/types'

const tableData = ref<LeaveType[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<LeaveTypeForm>({ name: '', code: '', defaultDays: 5, enabled: true })
const rules = { name: [{ required: true, message: '请输入假期名称', trigger: 'blur' }], code: [{ required: true, message: '请输入编码', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await leaveTypeApi.list()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', code: '', defaultDays: 5, enabled: true }); dialogVisible.value = true }
const handleEdit = (row: LeaveType) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: LeaveType) => { await ElMessageBox.confirm('确定删除？'); await leaveTypeApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await leaveTypeApi.update(editId.value, form)
    else await leaveTypeApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.leave-type-setting {
  .toolbar { margin-bottom: 16px; }
}
</style>