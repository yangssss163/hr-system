<template>
  <div class="dept-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增部门
        </el-button>
        <el-select v-model="companyId" placeholder="选择公司" clearable style="width: 180px" @change="loadData">
          <el-option v-for="item in companyList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
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
              <el-button size="small" @click.stop="handleEdit(data)">编辑</el-button>
              <el-button size="small" type="danger" @click.stop="handleDelete(data)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属公司" prop="companyId">
          <el-select v-model="form.companyId" placeholder="请选择公司">
            <el-option v-for="item in companyList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="上级部门">
          <el-select v-model="form.parentId" placeholder="请选择上级部门" clearable>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getDeptTree, createDept, updateDept, deleteDept } from '@/api/system/dept'
import { getCompanyTree } from '@/api/system/company'
import type { Dept, DeptForm, Company } from '@/api/types'

const treeData = ref<Dept[]>([])
const companyList = ref<Company[]>([])
const companyId = ref<number | undefined>()
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<DeptForm>({
  companyId: 0,
  name: '',
  parentId: 0,
  sort: 0,
  status: 1
})

const treeProps = {
  children: 'children',
  label: 'name'
}

const rules = {
  companyId: [{ required: true, message: '请选择公司', trigger: 'change' }],
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const deptOptions = computed(() => {
  const result: { id: number; label: string }[] = []
  const traverse = (nodes: Dept[], prefix = '') => {
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

const dialogTitle = computed(() => isEdit.value ? '编辑部门' : '新增部门')

const loadCompany = async () => {
  const res = await getCompanyTree()
  const flatten = (nodes: Company[]): Company[] => {
    const result: Company[] = []
    const traverse = (ns: Company[]) => {
      ns.forEach(n => {
        result.push(n)
        if (n.children) traverse(n.children)
      })
    }
    traverse(res.data || [])
    return result
  }
  companyList.value = flatten(res.data || [])
}

const sortTree = (nodes: Dept[]) => {
  nodes.sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  nodes.forEach(n => { if (n.children?.length) sortTree(n.children) })
}

const loadData = async () => {
  const res = await getDeptTree({ companyId: companyId.value })
  const data = res.data || []
  sortTree(data)
  treeData.value = data
}

const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  Object.assign(form, { companyId: companyId.value || 0, name: '', parentId: 0, sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (data: Dept) => {
  isEdit.value = true
  editId.value = data.id
  Object.assign(form, {
    companyId: data.companyId,
    name: data.name,
    parentId: data.parentId,
    sort: data.sort,
    status: data.status
  })
  dialogVisible.value = true
}

const handleDelete = async (data: Dept) => {
  if (data.children && data.children.length > 0) {
    ElMessage.warning('请先删除子部门')
    return
  }
  await ElMessageBox.confirm('确定要删除该部门吗？', '提示', { type: 'warning' })
  await deleteDept(data.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateDept(editId.value, form)
      } else {
        await createDept(form)
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
  loadCompany()
  loadData()
})
</script>

<style lang="scss" scoped>
.dept-manage {
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
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