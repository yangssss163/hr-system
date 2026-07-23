<template>
  <div class="shift-setting">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'attendance:shift:create'" type="primary" @click="handleAdd">创建班次</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="班次名称" width="150" />
        <el-table-column prop="startTime" label="上班时间" width="100" />
        <el-table-column prop="endTime" label="下班时间" width="100" />
        <el-table-column prop="lateBuffer" label="迟到缓冲(分钟)" width="130" />
        <el-table-column prop="earlyBuffer" label="早退缓冲(分钟)" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-permission="'attendance:shift:edit'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'attendance:shift:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑班次' : '创建班次'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="班次名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="上班时间" prop="startTime"><el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm" /></el-form-item>
        <el-form-item label="下班时间" prop="endTime"><el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm" /></el-form-item>
        <el-form-item label="迟到缓冲(分钟)"><el-input-number v-model="form.lateBuffer" :min="0" :max="60" /></el-form-item>
        <el-form-item label="早退缓冲(分钟)"><el-input-number v-model="form.earlyBuffer" :min="0" :max="60" /></el-form-item>
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
import { shiftApi } from '@/api/modules/attendance'
import type { Shift, ShiftForm } from '@/api/types'

const tableData = ref<Shift[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<ShiftForm>({ name: '', startTime: '09:00', endTime: '18:00', lateBuffer: 10, earlyBuffer: 10, status: 1 })
const rules = { name: [{ required: true, message: '请输入班次名称', trigger: 'blur' }], startTime: [{ required: true, message: '请选择上班时间', trigger: 'blur' }], endTime: [{ required: true, message: '请选择下班时间', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await shiftApi.list()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; delete (form as any).id; Object.assign(form, { name: '', startTime: '09:00', endTime: '18:00', lateBuffer: 10, earlyBuffer: 10, status: 1 }); dialogVisible.value = true }
const handleEdit = (row: Shift) => { isEdit.value = true; editId.value = row.id; Object.assign(form, { name: row.name, startTime: row.startTime, endTime: row.endTime, lateBuffer: row.lateBuffer, earlyBuffer: row.earlyBuffer, status: row.status }); dialogVisible.value = true }
const handleDelete = async (row: Shift) => { await ElMessageBox.confirm('确定删除？'); await shiftApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await shiftApi.update(editId.value, form)
    else await shiftApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.shift-setting {
  .toolbar { margin-bottom: 16px; }
}
</style>