<template>
  <div class="user-manage">
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="queryForm.keyword" placeholder="请输入用户名" style="width: 200px" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="queryForm.deptId" placeholder="请选择部门" clearable style="width: 200px">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
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
        <el-button v-permission="'system:user:create'" type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增用户
        </el-button>
      </div>
      <el-table :data="tableData" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="deptName" label="所属部门" />
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
            <el-button v-permission="'system:user:edit'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'system:user:assign-role'" size="small" type="warning" @click="handleEditRole(row)">分配角色</el-button>
            <el-button v-permission="'system:user:delete'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择部门">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
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

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-tree
        :data="roleTreeData"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        show-checkbox
        default-expand-all
        ref="roleTreeRef"
        :check-strictly="true"
      />
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUserList, createUser, updateUser, deleteUser, assignRoles } from '@/api/system/user'
import { getDeptTree } from '@/api/system/dept'
import { getAllRoles } from '@/api/system/role'
import type { User, UserForm, Dept, Role } from '@/api/types'

const tableData = ref<User[]>([])
const deptOptions = ref<{ id: number; label: string }[]>([])
const roleTreeData = ref<Role[]>([])
const selectedRows = ref<User[]>([])
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const formRef = ref()
const roleTreeRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const queryForm = reactive({
  keyword: '',
  deptId: undefined as number | undefined,
  status: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const form = reactive<UserForm>({
  username: '',
  realName: '',
  password: '',
  email: '',
  phone: '',
  deptId: 0,
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: { required: true, message: '请输入姓名', trigger: 'blur' },
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
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

const loadRole = async () => {
  const res = await getAllRoles()
  roleTreeData.value = res.data || []
}

const loadData = async () => {
  const res = await getUserList({
    page: pagination.current,
    pageSize: pagination.pageSize,
    keyword: queryForm.keyword,
    deptId: queryForm.deptId,
    status: queryForm.status
  })
  tableData.value = res.data.records || []
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
  queryForm.status = undefined
  loadData()
}

const handleSelectionChange = (rows: User[]) => {
  selectedRows.value = rows
}

const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  Object.assign(form, { username: '', realName: '', password: '', email: '', phone: '', deptId: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: User) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { username: row.username, realName: row.realName, password: '', email: row.email, phone: row.phone, deptId: row.deptId, status: row.status })
  dialogVisible.value = true
}

const handleEditRole = async (row: User) => {
  editId.value = row.id
  await loadRole()
  roleDialogVisible.value = true
}

const handleDelete = async (row: User) => {
  await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateUser(editId.value, form)
      } else {
        await createUser(form)
      }
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    } catch {
      // http.ts 已显示错误信息
    }
  })
}

const handleSaveRole = async () => {
  if (!roleTreeRef.value) return
  const checkedKeys = roleTreeRef.value.getCheckedKeys()
  await assignRoles(editId.value, { roleIds: checkedKeys })
  ElMessage.success('角色分配成功')
  roleDialogVisible.value = false
}

const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

onMounted(() => {
  loadDept()
  loadData()
})
</script>

<style lang="scss" scoped>
.user-manage {
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