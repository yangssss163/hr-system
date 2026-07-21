<template>
  <div class="card-rule-setting">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'attendance:card-rule:create'" type="primary" @click="handleAdd">创建规则</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="规则名称" width="150" />
        <el-table-column prop="minCardCount" label="最少打卡次数" width="130" />
        <el-table-column prop="allowOvertime" label="允许加班" width="100">
          <template #default="{ row }"><el-tag :type="row.allowOvertime ? 'success' : 'info'">{{ row.allowOvertime ? '是' : '否' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-permission="'attendance:card-rule:edit'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'attendance:card-rule:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑规则' : '创建规则'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="规则名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="最少打卡次数" prop="minCardCount"><el-input-number v-model="form.minCardCount" :min="1" :max="10" /></el-form-item>
        <el-form-item label="允许加班"><el-switch v-model="form.allowOvertime" /></el-form-item>
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
import { cardRuleApi } from '@/api/modules/attendance'
import type { CardRule, CardRuleForm } from '@/api/types'

const tableData = ref<CardRule[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<CardRuleForm>({ name: '', minCardCount: 2, allowOvertime: true, status: 1 })
const rules = { name: [{ required: true, message: '请输入规则名称', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await cardRuleApi.list()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', minCardCount: 2, allowOvertime: true, status: 1 }); dialogVisible.value = true }
const handleEdit = (row: CardRule) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: CardRule) => { await ElMessageBox.confirm('确定删除？'); await cardRuleApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await cardRuleApi.update(editId.value, form)
    else await cardRuleApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.card-rule-setting {
  .toolbar { margin-bottom: 16px; }
}
</style>