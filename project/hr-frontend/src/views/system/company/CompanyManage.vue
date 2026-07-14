<template>
  <div class="company-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增公司
        </el-button>
      </div>
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        highlight-current
        @node-click="handleNodeClick"
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
        <el-form-item label="公司名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="公司编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入公司编码" />
        </el-form-item>
        <el-form-item label="上级公司">
          <el-select v-model="form.parentId" placeholder="请选择上级公司" clearable>
            <el-option
              v-for="item in flatTreeOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
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
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCompanyTree, createCompany, updateCompany, deleteCompany } from '@/api/system/company'
import type { Company, CompanyForm } from '@/api/types'

const treeData = ref<Company[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const form = reactive<CompanyForm>({
  name: '',
  code: '',
  parentId: 0,
  sort: 0,
  status: 1
})

const treeProps = {
  children: 'children',
  label: 'name'
}

const rules = {
  name: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入公司编码', trigger: 'blur' }]
}

const flatTreeOptions = computed(() => {
  const result: { id: number; label: string }[] = []
  const traverse = (nodes: Company[], prefix = '') => {
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

const dialogTitle = computed(() => isEdit.value ? '编辑公司' : '新增公司')

const sortTree = (nodes: Company[]) => {
  nodes.sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  nodes.forEach(n => { if (n.children?.length) sortTree(n.children) })
}

const loadData = async () => {
  const res = await getCompanyTree()
  const data = res.data || []
  sortTree(data)
  treeData.value = data
}

const handleAdd = () => {
  isEdit.value = false
  editId.value = 0
  Object.assign(form, { name: '', code: '', parentId: 0, sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (data: Company) => {
  isEdit.value = true
  editId.value = data.id
  Object.assign(form, {
    name: data.name,
    code: data.code,
    parentId: data.parentId,
    sort: data.sort,
    status: data.status
  })
  dialogVisible.value = true
}

const handleDelete = async (data: Company) => {
  if (data.children && data.children.length > 0) {
    ElMessage.warning('请先删除子公司')
    return
  }
  await ElMessageBox.confirm('确定要删除该公司吗？', '提示', { type: 'warning' })
  await deleteCompany(data.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleNodeClick = (data: Company) => {
  console.log(data)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateCompany(editId.value, form)
      } else {
        await createCompany(form)
      }
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    } catch {
      // http.ts 已显示错误信息
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.company-manage {
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