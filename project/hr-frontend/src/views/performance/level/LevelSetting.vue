<template>
  <div class="level-setting">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'performance:level:create'" type="primary" @click="handleAdd">添加等级</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="等级名称" width="100">
          <template #default="{ row }"><el-tag size="large" :type="getLevelTag(row.name)">{{ row.name }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="scoreMin" label="最低分数" width="100" />
        <el-table-column prop="scoreMax" label="最高分数" width="100" />
        <el-table-column prop="coefficient" label="系数" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-permission="'performance:level:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'performance:level:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑等级' : '添加等级'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="等级名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="最低分数" prop="scoreMin"><el-input-number v-model="form.scoreMin" :min="0" :max="100" /></el-form-item>
        <el-form-item label="最高分数" prop="scoreMax"><el-input-number v-model="form.scoreMax" :min="0" :max="100" /></el-form-item>
        <el-form-item label="系数"><el-input-number v-model="form.coefficient" :min="0" :max="5" :step="0.1" /></el-form-item>
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
import { perfLevelApi } from '@/api/modules/performance'
import type { PerfLevel, PerfLevelForm } from '@/api/types'

const tableData = ref<PerfLevel[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<PerfLevelForm>({ name: '', scoreMin: 0, scoreMax: 100, coefficient: 1, sort: 0 })
const rules = { name: [{ required: true, message: '请输入等级名称', trigger: 'blur' }] }

const levelTagMap: Record<string, string> = { S: 'danger', A: 'warning', B: 'primary', C: 'info', D: '' }
const getLevelTag = (name: string) => levelTagMap[name] || ''

const loadData = async () => {
  loading.value = true
  try {
    const res = await perfLevelApi.list()
    tableData.value = (res.data || []).sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', scoreMin: 0, scoreMax: 100, coefficient: 1, sort: 0 }); dialogVisible.value = true }
const handleEdit = (row: PerfLevel) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: PerfLevel) => { await ElMessageBox.confirm('确定删除？'); await perfLevelApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await perfLevelApi.update(editId.value, form)
    else await perfLevelApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.level-setting {
  .toolbar { margin-bottom: 16px; }
}
</style>