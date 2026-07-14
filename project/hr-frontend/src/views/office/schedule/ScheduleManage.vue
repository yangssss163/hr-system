<template>
  <div class="schedule-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新建日程</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="日程标题" width="200" />
        <el-table-column prop="description" label="描述" width="250" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑日程' : '新建日程'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="日程标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="开始时间" prop="startTime"><el-date-picker v-model="form.startTime" type="datetime" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间" prop="endTime"><el-date-picker v-model="form.endTime" type="datetime" style="width:100%" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scheduleApi } from '@/api/modules/office'
import type { Schedule, ScheduleForm } from '@/api/types'

const tableData = ref<Schedule[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<ScheduleForm>({ title: '', description: '', startTime: '', endTime: '', location: '', status: 0 })
const rules = { title: [{ required: true, message: '请输入日程标题', trigger: 'blur' }], startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }], endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await scheduleApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { title: '', description: '', startTime: '', endTime: '', location: '', status: 0 }); dialogVisible.value = true }
const handleEdit = (row: Schedule) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Schedule) => { await ElMessageBox.confirm('确定删除？'); await scheduleApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await scheduleApi.update(editId.value, form)
    else await scheduleApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.schedule-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>