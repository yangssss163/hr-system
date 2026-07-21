<template>
  <div class="position-manage">
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="职位名称">
          <el-input v-model="queryForm.keyword" placeholder="请输入职位名称" style="width: 200px" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="queryForm.deptId" placeholder="请选择部门" clearable style="width: 200px">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="toolbar">
        <el-button v-permission="'system:position:create'" type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增职位
        </el-button>
      </div>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="name" label="职位名称" />
        <el-table-column prop="deptName" label="所属部门" />
        <el-table-column prop="sort" label="排序" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-permission="'system:position:edit'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'system:position:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="职位名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入职位名称" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择部门">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPositionList, createPosition, updatePosition, deletePosition } from '@/api/system/position'
import { getDeptTree } from '@/api/system/dept'
import type { Position, PositionForm, Dept } from '@/api/types'

const tableData = ref<Position[]>([])
const deptOptions = ref<{ id: number; label: string }[]>([])
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const queryForm = reactive({
  keyword: '',
  deptId: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const form = reactive<PositionForm>({
  name: '',
  deptId: 0,
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }]
}

const loadDept = async () => {
  const res = await getDeptTree()
  const result: { id: number; label: string }[] = []
  const traverse = (nodes: Dept[], prefix = '') => {
    nodes.forEach(node => {
      result.push({ id: node.id, label: `${prefix}${node.name}` })
      if (node.children) traverse(node.children, prefix + '　└─ ')
    })
  }
  traverse(res.data || [])
  deptOptions.value = result
}

const loadData = async () => {
  const res = await getPositionList({
    page: pagination.current,
    pageSize: pagination.pageSize,
    keyword: queryForm.keyword,
    deptId: queryForm.deptId
  })
  tableData.value = (res.data.records || []).sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  pagination.total = res.data.total || 0
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.current = 1
  loadData()
}

const handlePageChange = (page: number) => {
  pagination.current = page
  loadData()
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.deptId = undefined
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  Object.assign(form, { name: '', deptId: 0, sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: Position) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { name: row.name, deptId: row.deptId, sort: row.sort, status: row.status })
  dialogVisible.value = true
}

const handleDelete = async (row: Position) => {
  await ElMessageBox.confirm('确定要删除该职位吗？', '提示', { type: 'warning' })
  await deletePosition(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updatePosition(editId.value, form)
      } else {
        await createPosition(form)
      }
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    } catch {
      // http.ts 已显示错误信息
    }
  })
}

const dialogTitle = computed(() => isEdit.value ? '编辑职位' : '新增职位')

onMounted(() => {
  loadDept()
  loadData()
})
</script>

<style lang="scss" scoped>
.position-manage {
  .search-card {
    margin-bottom: 16px;
  }

  .toolbar {
    margin-bottom: 16px;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>