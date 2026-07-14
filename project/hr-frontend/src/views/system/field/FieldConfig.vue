<template>
  <div class="field-config">
    <el-card>
      <div class="toolbar">
        <el-select v-model="moduleFilter" placeholder="按模块筛选" clearable @change="loadData">
          <el-option v-for="(label, key) in entityTypeMap" :key="key" :label="label" :value="key" />
        </el-select>
        <el-button type="primary" @click="handleAddRow">
          <el-icon><Plus /></el-icon>新增行
        </el-button>
        <el-button type="success" @click="handleSaveAll" :disabled="!hasChanges">
          批量保存
        </el-button>
      </div>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="fieldName" label="字段名称">
          <template #default="{ row }">
            <el-input v-model="row.fieldName" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="fieldCode" label="字段编码">
          <template #default="{ row }">
            <el-input v-model="row.fieldCode" size="small" :disabled="!!row.id" />
          </template>
        </el-table-column>
        <el-table-column prop="entityType" label="所属实体" width="120">
          <template #default="{ row }">
            <el-select v-model="row.entityType" size="small">
              <el-option v-for="(label, key) in entityTypeMap" :key="key" :label="label" :value="key" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="fieldType" label="字段类型" width="120">
          <template #default="{ row }">
            <el-select v-model="row.fieldType" size="small">
              <el-option v-for="(label, key) in fieldTypeMap" :key="key" :label="label" :value="key" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80">
          <template #default="{ row }">
            <el-input-number v-model="row.sort" size="small" :min="0" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column prop="required" label="必填" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.required" :active-value="1" :inactive-value="0" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="visible" label="显示" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.visible" :active-value="1" :inactive-value="0" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ $index }">
            <el-button size="small" type="danger" @click="handleRemoveRow($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getFieldConfigs, saveFieldConfigs } from '@/api/system/fieldConfig'
import type { Field } from '@/api/types'

const tableData = ref<Field[]>([])
const moduleFilter = ref('')
const hasChanges = ref(false)
const initialJson = ref('')

const entityTypeMap: Record<string, string> = {
  EMPLOYEE: '员工',
  DEPARTMENT: '部门',
  POSITION: '职位',
  USER: '用户',
  ROLE: '角色',
  COMPANY: '公司'
}

const fieldTypeMap: Record<string, string> = {
  VARCHAR: '字符串',
  INTEGER: '整数',
  BIGINT: '长整数',
  DECIMAL: '小数',
  DATE: '日期',
  DATETIME: '日期时间',
  BOOLEAN: '布尔',
  TEXT: '文本'
}

const getEmptyRow = (): Partial<Field> => ({
  fieldName: '',
  fieldCode: '',
  entityType: 'EMPLOYEE',
  fieldType: 'VARCHAR',
  fieldLength: 255,
  defaultValue: '',
  sort: 0,
  required: 0,
  visible: 1,
  status: 1,
  remark: ''
})

const loadData = async () => {
  const res = await getFieldConfigs(moduleFilter.value || undefined)
  tableData.value = (res.data || []).sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  initialJson.value = JSON.stringify(tableData.value)
  hasChanges.value = false
}

const handleAddRow = () => {
  tableData.value.push(getEmptyRow() as Field)
  hasChanges.value = true
}

const handleRemoveRow = (index: number) => {
  tableData.value.splice(index, 1)
  hasChanges.value = true
}

const handleSaveAll = async () => {
  try {
    await saveFieldConfigs(tableData.value)
    ElMessage.success('保存成功')
    initialJson.value = JSON.stringify(tableData.value)
    hasChanges.value = false
  } catch {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.field-config {
  .toolbar {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;

    .el-select {
      width: 180px;
    }
  }
}
</style>
