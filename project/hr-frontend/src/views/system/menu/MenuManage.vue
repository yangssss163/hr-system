<template>
  <div class="menu-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增菜单
        </el-button>
      </div>
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        highlight-current
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span>{{ node.label }}</span>
            <span class="node-actions">
              <el-button size="small" @click.stop="handleAddChild(data)">添加子菜单</el-button>
              <el-button size="small" @click.stop="handleEdit(data)">编辑</el-button>
              <el-button size="small" type="danger" @click.stop="handleDelete(data)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="上级菜单">
          <el-select v-model="form.parentId" placeholder="请选择上级菜单" clearable>
            <el-option v-for="item in menuOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="form.type">
            <el-radio :value="'M'">目录</el-radio>
            <el-radio :value="'C'">菜单</el-radio>
            <el-radio :value="'F'">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入菜单路径" />
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.permission" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/system/menu'
import type { Menu, MenuForm } from '@/api/types'

const treeData = ref<Menu[]>([])
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const parentId = ref(0)

const form = reactive<MenuForm>({
  name: '',
  parentId: 0,
  type: 2,
  path: '',
  component: '',
  permission: '',
  icon: '',
  sort: 0,
  status: 1
})

const treeProps = {
  children: 'children',
  label: 'name'
}

const rules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入菜单路径', trigger: 'blur' }]
}

const menuOptions = computed(() => {
  const result: { id: number; label: string }[] = []
  const traverse = (nodes: Menu[], prefix = '') => {
    nodes.forEach(node => {
      if (node.id !== editId.value) {
        result.push({ id: node.id, label: `${prefix}${node.name}` })
      }
      if (node.children && node.children.length) {
        traverse(node.children, prefix + '　└─ ')
      }
    })
  }
  traverse(treeData.value)
  return result
})

const dialogTitle = computed(() => isEdit.value ? '编辑菜单' : '新增菜单')

const sortTree = (nodes: Menu[]) => {
  nodes.sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  nodes.forEach(n => { if (n.children?.length) sortTree(n.children) })
}

const loadData = async () => {
  const res = await getMenuTree()
  const data = res.data || []
  sortTree(data)
  treeData.value = data
}

const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  parentId.value = 0
  Object.assign(form, { name: '', parentId: 0, type: 'C', path: '', component: '', permission: '', icon: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleAddChild = (data: Menu) => {
  isEdit.value = false
  editId.value = 0
  parentId.value = data.id
  Object.assign(form, { name: '', parentId: data.id, type: 'C', path: '', component: '', permission: '', icon: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (data: Menu) => {
  isEdit.value = true
  editId.value = data.id
  Object.assign(form, {
    name: data.name,
    parentId: data.parentId,
    type: data.type,
    path: data.path,
    component: data.component,
    permission: data.permission,
    icon: data.icon,
    sort: data.sort,
    status: data.status
  })
  dialogVisible.value = true
}

const handleDelete = async (data: Menu) => {
  if (data.children && data.children.length > 0) {
    ElMessage.warning('请先删除子菜单')
    return
  }
  await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', { type: 'warning' })
  await deleteMenu(data.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateMenu(editId.value, form)
      } else {
        await createMenu(form)
      }
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    } catch {
      // http.ts 已显示错误信息
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.menu-manage {
  .toolbar {
    margin-bottom: 16px;
  }

  .custom-tree-node {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .node-actions {
      display: none;
    }
  }

  :deep(.el-tree-node:hover .node-actions) {
    display: inline-flex;
    gap: 8px;
  }
}
</style>