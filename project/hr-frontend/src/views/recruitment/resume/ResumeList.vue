<template>
  <div class="resume-list">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增简历</el-button>
        <el-button @click="handleImport">批量导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="applyPosition" label="应聘职位" width="120" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" @click="handleInvite(row)">邀请</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑简历' : '新增简历'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="学历"><el-select v-model="form.education"><el-option label="高中" value="high_school" /><el-option label="大专" value="junior_college" /><el-option label="本科" value="bachelor" /><el-option label="硕士" value="master" /><el-option label="博士" value="doctor" /></el-select></el-form-item>
        <el-form-item label="应聘职位"><el-input v-model="form.applyPosition" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
    <el-dialog v-model="importVisible" title="批量导入简历" width="500px">
      <div class="import-area">
        <el-upload ref="uploadRef" class="upload-demo" :show-file-list="false" :auto-upload="false" :on-change="handleFileChange" accept=".xlsx,.xls,.csv" :multiple="false">
          <el-button type="primary">点击上传文件</el-button>
        </el-upload>
        <div class="import-tips">
          <p>支持格式：.xlsx、.xls、.csv</p>
          <p>请先下载模板，按模板格式填写数据后再上传</p>
        </div>
        <div v-if="importResult" class="import-result">
          <el-table :data="importResult.details" size="small" border>
            <el-table-column prop="row" label="行号" width="60" />
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }"><el-tag :type="row.status === 'success' ? 'success' : 'danger'">{{ row.status === 'success' ? '成功' : '失败' }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="message" label="原因" min-width="150" />
          </el-table>
          <div class="result-summary">
            <span>成功：{{ importResult.successCount }} 条</span>
            <span>失败：{{ importResult.failCount }} 条</span>
          </div>
        </div>
      </div>
      <template #footer><el-button @click="handleCloseImport">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'
import { resumeApi } from '@/api/modules/recruitment'
import type { Resume, ResumeForm } from '@/api/types'

const tableData = ref<Resume[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const importVisible = ref(false)
const formRef = ref()
const uploadRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const importResult = ref<{ successCount: number; failCount: number; details: { row: number; name: string; phone: string; status: 'success' | 'fail'; message: string }[] } | null>(null)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<ResumeForm>({ name: '', phone: '', email: '', gender: 1, education: '', school: '', major: '', workYears: 0, applyPosition: '', source: '', status: 'new' })
const rules = { name: [{ required: true, message: '请输入姓名', trigger: 'blur' }], phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }] }

const educationMap: Record<string, string> = { '高中': 'high_school', '大专': 'junior_college', '本科': 'bachelor', '硕士': 'master', '博士': 'doctor' }

const loadData = async () => {
  loading.value = true
  try {
    const res = await resumeApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', phone: '', education: '', applyPosition: '' }); dialogVisible.value = true }
const handleEdit = (row: Resume) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Resume) => { await ElMessageBox.confirm('确定删除？'); await resumeApi.delete(row.id); ElMessage.success('删除成功'); loadData() }
const handleInvite = (_row: Resume) => {}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await resumeApi.update(editId.value, form)
    else await resumeApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

const handleImport = () => { importVisible.value = true; importResult.value = null }
const handleCloseImport = () => { importVisible.value = false; importResult.value = null; uploadRef.value?.clearFiles() }

const handleDownloadTemplate = () => {
  const headers = ['姓名', '手机号', '邮箱', '性别', '学历', '毕业院校', '专业', '工作年限', '应聘职位', '来源渠道']
  const data = [['张三', '13800138001', 'zhangsan@example.com', '男', '本科', '北京大学', '计算机科学', '3', '前端开发', '招聘网站']]
  const worksheet = XLSX.utils.aoa_to_sheet([headers, ...data])
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '简历导入模板')
  const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
  const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  saveAs(blob, '简历导入模板.xlsx')
}

const handleFileChange = async (file: any) => {
  const validExtensions = ['.xlsx', '.xls', '.csv']
  const ext = file.name.toLowerCase().substring(file.name.lastIndexOf('.'))
  if (!validExtensions.includes(ext)) {
    ElMessage.error('请上传 xlsx、xls 或 csv 格式的文件')
    return
  }
  const parsedData = await parseExcel(file.raw)
  await submitImportData(parsedData)
}

const submitImportData = async (data: ResumeForm[]) => {
  if (data.length === 0) {
    ElMessage.warning('未解析到有效数据')
    return
  }
  const details: { row: number; name: string; phone: string; status: 'success' | 'fail'; message: string }[] = data.map((item, index) => ({ row: index + 2, name: item.name, phone: item.phone, status: 'success', message: '' }))
  let successCount = 0
  let failCount = 0
  for (let i = 0; i < data.length; i++) {
    try {
      await resumeApi.create(data[i])
      successCount++
    } catch {
      details[i].status = 'fail'
      details[i].message = '导入失败'
      failCount++
    }
  }
  importResult.value = { successCount, failCount, details }
  loadData()
}

const parseExcel = (file: File): Promise<ResumeForm[]> => {
  return new Promise((resolve) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      try {
        const data = e.target?.result
        const workbook = XLSX.read(data, { type: typeof data === 'string' ? 'string' : 'array', cellText: true, cellDates: false })
        const sheetName = workbook.SheetNames[0]
        const worksheet = workbook.Sheets[sheetName]
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 }) as string[][]
        const result: ResumeForm[] = []
        for (let i = 1; i < jsonData.length; i++) {
          const row = jsonData[i]
          if (!row || !row[0] || !row[1]) continue
          const name = sanitizeString(row[0])
          const phone = sanitizeString(row[1])
          const email = sanitizeString(row[2] || '')
          const genderText = sanitizeString(row[3] || '')
          const educationText = sanitizeString(row[4] || '')
          const school = sanitizeString(row[5] || '')
          const major = sanitizeString(row[6] || '')
          const workYears = parseInt(row[7]?.toString().trim() || '0')
          const applyPosition = sanitizeString(row[8] || '')
          const source = sanitizeString(row[9] || '')
          const gender = genderText === '男' ? 1 : genderText === '女' ? 2 : 1
          const education = educationMap[educationText] || educationText || ''
          result.push({ name, phone, email, gender, education, school, major, workYears, applyPosition, source, status: 'new' })
        }
        resolve(result)
      } catch {
        ElMessage.error('文件解析失败')
        resolve([])
      }
    }
    reader.readAsArrayBuffer(file)
  })
}

const sanitizeString = (value: any): string => {
  if (!value) return ''
  let str = String(value).trim()
  str = str.replace(/\r/g, '')
  str = str.replace(/[\x00-\x1F\x7F]/g, '')
  return str
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.resume-list {
  .toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
  .import-area {
    .upload-demo { margin-bottom: 16px; }
    .import-tips { color: #999; font-size: 12px; line-height: 1.8; }
    .import-result { margin-top: 20px; }
    .result-summary { margin-top: 12px; text-align: right; span { margin-left: 16px; } }
  }
}
</style>