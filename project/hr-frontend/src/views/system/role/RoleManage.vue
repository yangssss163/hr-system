<template>
  <div class="role-manage">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'system:role:create'" type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增角色
        </el-button>
      </div>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button v-permission="'system:role:update'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'system:role:update'" size="small" type="warning" @click="handlePerm(row)">权限</el-button>
            <el-button v-permission="'system:role:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入角色描述" />
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

    <el-dialog v-model="permDialogVisible" title="权限配置" width="600px">
      <el-tree
        :data="menuTreeData"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        show-checkbox
        default-expand-all
        ref="menuTreeRef"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePerm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getRoleList, getRoleById, createRole, updateRole, deleteRole, assignMenus } from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'
import type { Role, RoleForm, Menu } from '@/api/types'

const tableData = ref<Role[]>([])
const menuTreeData = ref<Menu[]>([])
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const formRef = ref()
const menuTreeRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const currentRoleId = ref(0)

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const form = reactive<RoleForm>({
  name: '',
  code: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getRoleList({ page: pagination.current, pageSize: pagination.pageSize })
  tableData.value = res.data.records || []
  pagination.total = res.data.total || 0
}

const loadMenu = async () => {
  const res = await getMenuTree()
  menuTreeData.value = res.data || []
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

const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  Object.assign(form, { name: '', code: '', description: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: Role) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { name: row.name, code: row.code, description: row.description, status: row.status })
  dialogVisible.value = true
}

const handleDelete = async (row: Role) => {
  await ElMessageBox.confirm('确定要删除该角色吗？', '提示', { type: 'warning' })
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateRole(editId.value, form)
      } else {
        await createRole(form)
      }
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    } catch {
      // http.ts 已显示错误信息
    }
  })
}

const handlePerm = async (row: Role) => {
  currentRoleId.value = row.id
  await loadMenu()
  const res = await getRoleById(row.id)
  const menuIds = res.data.menuIds || []
  permDialogVisible.value = true
  await nextTick()
  menuTreeRef.value?.setCheckedKeys(menuIds)
}

const handleSavePerm = async () => {
  if (!menuTreeRef.value) return
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  await assignMenus(currentRoleId.value, { menuIds: checkedKeys })
  ElMessage.success('权限配置成功')
  permDialogVisible.value = false
  loadData()
}

const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.role-manage {
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