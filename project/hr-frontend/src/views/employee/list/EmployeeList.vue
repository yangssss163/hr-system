<template>
  <div class="employee-list">
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="姓名/工号">
          <el-input v-model="queryForm.keyword" placeholder="请输入姓名或工号" style="width: 200px" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="queryForm.deptId" placeholder="请选择部门" clearable style="width: 200px">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="queryForm.positionId" placeholder="请选择职位" clearable style="width: 200px">
            <el-option v-for="item in positionOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option :value="1" label="在职" />
            <el-option :value="2" label="离职" />
            <el-option :value="3" label="试用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增员工</el-button>
        <el-button type="success" @click="handleImport"><el-icon><Upload /></el-icon>批量导入</el-button>
        <el-button type="warning" @click="handleExport"><el-icon><Download /></el-icon>导出</el-button>
        <el-button type="danger" @click="handleBatchDeleteAction"><el-icon><Delete /></el-icon>批量删除</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="empNo" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="deptName" label="所属部门" width="150" />
        <el-table-column prop="positionName" label="职位" width="120" />
        <el-table-column prop="companyName" label="公司" width="120" />
        <el-table-column prop="entryDate" label="入职日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handlePageChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="工号" prop="empNo">
          <el-input v-model="form.empNo" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthday" type="date" placeholder="请选择出生日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="所属公司" prop="companyId">
          <el-select v-model="form.companyId" placeholder="请选择公司">
            <el-option v-for="item in companyOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择部门">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位" prop="positionId">
          <el-select v-model="form.positionId" placeholder="请选择职位">
            <el-option v-for="item in positionOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="entryDate">
          <el-date-picker v-model="form.entryDate" type="date" placeholder="请选择入职日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">在职</el-radio>
            <el-radio :value="2">离职</el-radio>
            <el-radio :value="3">试用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="员工详情" width="600px">
      <el-form :model="viewForm" label-width="100px" disabled>
        <el-form-item label="工号"><span>{{ viewForm.empNo }}</span></el-form-item>
        <el-form-item label="姓名"><span>{{ viewForm.name }}</span></el-form-item>
        <el-form-item label="性别"><span>{{ viewForm.gender === 1 ? '男' : '女' }}</span></el-form-item>
        <el-form-item label="手机号"><span>{{ viewForm.phone }}</span></el-form-item>
        <el-form-item label="邮箱"><span>{{ viewForm.email }}</span></el-form-item>
        <el-form-item label="身份证号"><span>{{ viewForm.idCard }}</span></el-form-item>
        <el-form-item label="出生日期"><span>{{ viewForm.birthday }}</span></el-form-item>
        <el-form-item label="所属公司"><span>{{ viewForm.companyName }}</span></el-form-item>
        <el-form-item label="所属部门"><span>{{ viewForm.deptName }}</span></el-form-item>
        <el-form-item label="职位"><span>{{ viewForm.positionName }}</span></el-form-item>
        <el-form-item label="入职日期"><span>{{ viewForm.entryDate }}</span></el-form-item>
        <el-form-item label="状态"><el-tag :type="getStatusType(viewForm.status)">{{ getStatusName(viewForm.status) }}</el-tag></el-form-item>
        <el-form-item label="创建时间"><span>{{ viewForm.createTime }}</span></el-form-item>
        <el-form-item label="更新时间"><span>{{ viewForm.updateTime }}</span></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="批量导入" width="500px">
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="导入文件">
          <el-upload
            class="upload-demo"
            drag
            action="/api/employees/import"
            :headers="uploadHeaders"
            :on-success="handleImportSuccess"
            :on-error="handleImportError"
            :before-upload="beforeUpload"
            :auto-upload="false"
            ref="uploadRef"
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持 .xlsx 和 .xls 格式文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitImport">开始导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload, Download, Delete } from '@element-plus/icons-vue'
import { getEmployeeList, createEmployee, updateEmployee, deleteEmployee, getEmployeeById, batchDeleteEmployees, importEmployees, exportEmployees } from '@/api/modules/employee'
import { getDeptTree } from '@/api/system/dept'
import { getPositionList } from '@/api/system/position'
import { getCompanyTree } from '@/api/system/company'
import { useCRUD } from '@/composables/useCRUD'
import type { Employee, EmployeeForm, EmployeeDetail, Dept, Position, Company } from '@/api/types'

// ---------- 下拉选项 ----------
const deptOptions = ref<{ id: number; label: string }[]>([])
const positionOptions = ref<{ id: number; label: string }[]>([])
const companyOptions = ref<{ id: number; label: string }[]>([])

// ---------- 搜索条件 ----------
const queryForm = reactive({
  keyword: '',
  deptId: undefined as number | undefined,
  positionId: undefined as number | undefined,
  status: undefined as number | undefined
})

// ---------- 使用 useCRUD ----------
const {
  tableData,
  loading,
  submitting,
  dialogVisible,
  isEdit,
  editId,
  form,
  pagination,
  loadData,
  handleAdd,
  handleEdit: crudHandleEdit,
  handleDelete: crudHandleDelete,
  handleBatchDelete,
  handleSubmit: crudHandleSubmit,
  handleSizeChange,
  handlePageChange
} = useCRUD<Employee, EmployeeForm>({
  fetchFn: (params) => getEmployeeList(params),
  createFn: (data) => createEmployee(data),
  updateFn: (id, data) => updateEmployee(id, data),
  deleteFn: (id) => deleteEmployee(id),
  extraParams: () => ({
    keyword: queryForm.keyword,
    deptId: queryForm.deptId,
    positionId: queryForm.positionId,
    status: queryForm.status
  }),
  defaultForm: () => ({
    empNo: '',
    name: '',
    gender: 1,
    idCard: '',
    birthday: '',
    phone: '',
    email: '',
    deptId: 0,
    positionId: 0,
    companyId: 0,
    entryDate: '',
    status: 1
  })
})

// ---------- 页面自定义状态 ----------
const selectedRows = ref<Employee[]>([])
const viewDialogVisible = ref(false)
const importDialogVisible = ref(false)
const formRef = ref()
const uploadRef = ref()
const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem('token')}` }

const viewForm = reactive<EmployeeDetail>({
  id: 0,
  userId: 0,
  empNo: '',
  name: '',
  gender: 1,
  phone: '',
  email: '',
  deptId: 0,
  deptName: '',
  positionId: 0,
  positionName: '',
  companyId: 0,
  companyName: '',
  entryDate: '',
  status: 1,
  createTime: '',
  idCard: '',
  birthday: '',
  updateTime: ''
})

const importForm = reactive({ file: '' })

const rules = {
  empNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  companyId: [{ required: true, message: '请选择公司', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  positionId: [{ required: true, message: '请选择职位', trigger: 'change' }],
  entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑员工' : '新增员工')

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 1: 'success', 2: 'danger', 3: 'warning' }
  return map[status] || 'info'
}

const getStatusName = (status: number) => {
  const map: Record<number, string> = { 1: '在职', 2: '离职', 3: '试用' }
  return map[status] || '未知'
}

// ---------- 下拉选项加载 ----------
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

const loadPosition = async () => {
  const res = await getPositionList({ pageSize: 1000 })
  positionOptions.value = (res.data?.records || []).map((item: Position) => ({ id: item.id, label: item.name }))
}

const loadCompany = async () => {
  const res = await getCompanyTree()
  const result: { id: number; label: string }[] = []
  const traverse = (nodes: Company[], prefix = '') => {
    nodes.forEach(node => {
      result.push({ id: node.id, label: `${prefix}${node.name}` })
      if (node.children) traverse(node.children, prefix + '　└─ ')
    })
  }
  traverse(res.data || [])
  companyOptions.value = result
}

// ---------- 搜索 ----------
const handleReset = () => {
  queryForm.keyword = ''
  queryForm.deptId = undefined
  queryForm.positionId = undefined
  queryForm.status = undefined
  loadData()
}

const handleSearch = () => {
  loadData({ page: 1 })
}

// ---------- 表格选择 ----------
const handleSelectionChange = (rows: Employee[]) => {
  selectedRows.value = rows
}

// ---------- 查看 ----------
const handleView = async (row: Employee) => {
  const res = await getEmployeeById(row.id)
  Object.assign(viewForm, res.data)
  viewDialogVisible.value = true
}

// ---------- 编辑（覆盖 useCRUD 的 handleEdit，需要先获取详情） ----------
const handleEdit = async (row: Employee) => {
  isEdit.value = true
  // editId 在 useCRUD 的 handleEdit 中设置，但这里需要先异步获取详情
  const res = await getEmployeeById(row.id)
  const detail = res.data
  Object.assign(form, {
    empNo: detail.empNo,
    name: detail.name,
    gender: detail.gender,
    phone: detail.phone,
    email: detail.email,
    idCard: detail.idCard || '',
    birthday: detail.birthday || '',
    deptId: detail.deptId,
    positionId: detail.positionId,
    companyId: detail.companyId,
    entryDate: detail.entryDate,
    status: detail.status
  })
  editId.value = row.id
  dialogVisible.value = true
}

// ---------- 导入 ----------
const handleImport = () => { importDialogVisible.value = true }

const beforeUpload = (file: File) => {
  const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
  if (!isExcel) { ElMessage.error('请上传 Excel 文件'); return false }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) { ElMessage.error('文件大小不能超过 10MB'); return false }
  return true
}

const submitImport = () => { uploadRef.value?.submit() }

const handleImportSuccess = (response: any) => {
  ElMessage.success(`导入成功：成功 ${response.data.successCount} 条，失败 ${response.data.failCount} 条`)
  importDialogVisible.value = false
  loadData()
}

const handleImportError = () => { ElMessage.error('导入失败') }

// ---------- 导出 ----------
const handleExport = () => {
  exportEmployees({ keyword: queryForm.keyword, deptId: queryForm.deptId, status: queryForm.status })
}

// ---------- 提交（包装 useCRUD 的 handleSubmit） ----------
const handleSubmit = () => {
  crudHandleSubmit(formRef.value)
}

// ---------- 删除（包装 useCRUD 的 handleDelete） ----------
const handleDelete = (row: Employee) => {
  crudHandleDelete(row)
}

// ---------- 批量删除（包装 useCRUD 的 handleBatchDelete） ----------
const handleBatchDeleteAction = () => {
  handleBatchDelete(selectedRows.value, batchDeleteEmployees)
}

onMounted(() => {
  loadDept()
  loadPosition()
  loadCompany()
  loadData()
})
</script>

<style lang="scss" scoped>
.employee-list {
  .search-card { margin-bottom: 16px; }
  .toolbar { display: flex; gap: 8px; margin-bottom: 16px; }
  .pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }
}
</style>