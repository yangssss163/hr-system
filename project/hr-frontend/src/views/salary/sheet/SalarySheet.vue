<template>
  <div class="salary-sheet">
    <!-- ==================== 表A - 草稿工资 ==================== -->
    <el-card class="draft-card">
      <template #header>
        <span class="card-title">草稿工资表（{{ currentMonth }}）</span>
      </template>
      <div class="toolbar">
        <el-button v-permission="'salary:sheet:import'" type="info" @click="handleImportClick">
          导入数据
        </el-button>
        <el-button v-permission="'salary:sheet:generate'" type="success" @click="handleGenerate"
          :disabled="draftSelectedRows.length === 0">
          生成工资({{ draftSelectedRows.length }})
        </el-button>
        <el-button type="danger" plain @click="handleClearDraft">清除草稿</el-button>
      </div>
      <el-table :data="draftData" v-loading="draftLoading" @selection-change="onDraftSelect" border stripe>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="baseSalary" label="基本工资" width="110">
          <template #default="{ row }">{{ formatMoney(row.baseSalary) }}</template>
        </el-table-column>
        <el-table-column prop="perfSalary" label="绩效工资" width="110">
          <template #default="{ row }">{{ formatMoney(row.perfSalary) }}</template>
        </el-table-column>
        <el-table-column prop="subsidy" label="补贴" width="100">
          <template #default="{ row }">{{ formatMoney(row.subsidy) }}</template>
        </el-table-column>
        <el-table-column prop="overtimePay" label="加班费" width="100">
          <template #default="{ row }">{{ formatMoney(row.overtimePay) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }"><el-tag type="info">草稿</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small"
              v-permission="'salary:sheet:edit'"
              @click="openDraftEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- ==================== 表B - 已生成工资 ==================== -->
    <el-card class="gen-card">
      <template #header>
        <div class="gen-header">
          <span class="card-title">已生成工资表</span>
          <div class="gen-search">
            <el-date-picker v-model="genMonth" type="month" value-format="yyyy-MM" placeholder="选择月份" />
            <el-button type="primary" @click="loadGenData">查询</el-button>
            <el-button v-permission="'salary:sheet:export'" type="warning" @click="handleExport">
              导出工资表
            </el-button>
            <el-button type="danger" plain @click="handleClearGen">清除已生成</el-button>
          </div>
        </div>
      </template>
      <el-table :data="genData" v-loading="genLoading" border stripe>
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="baseSalary" label="基本工资" width="110">
          <template #default="{ row }">{{ formatMoney(row.baseSalary) }}</template>
        </el-table-column>
        <el-table-column prop="perfSalary" label="绩效工资" width="110">
          <template #default="{ row }">{{ formatMoney(row.perfSalary) }}</template>
        </el-table-column>
        <el-table-column prop="subsidy" label="补贴" width="100">
          <template #default="{ row }">{{ formatMoney(row.subsidy) }}</template>
        </el-table-column>
        <el-table-column prop="overtimePay" label="加班费" width="100">
          <template #default="{ row }">{{ formatMoney(row.overtimePay) }}</template>
        </el-table-column>
        <!-- 自定义字段列（动态） -->
        <el-table-column
          v-for="cf in customFieldColumns"
          :key="cf.code"
          :prop="'custom_' + cf.code"
          :label="cf.name"
          :width="110"
        >
          <template #default="{ row }">
            {{ formatMoney(getCustomFieldValue(row, cf.code)) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalIncome" label="应发合计" width="110">
          <template #default="{ row }">{{ formatMoney(row.totalIncome) }}</template>
        </el-table-column>
        <el-table-column prop="socialInsurance" label="社保扣除" width="110">
          <template #default="{ row }">{{ formatMoney(row.socialInsurance) }}</template>
        </el-table-column>
        <el-table-column prop="tax" label="个税" width="90">
          <template #default="{ row }">{{ formatMoney(row.tax) }}</template>
        </el-table-column>
        <el-table-column prop="totalDeduction" label="应扣合计" width="110">
          <template #default="{ row }">{{ formatMoney(row.totalDeduction) }}</template>
        </el-table-column>
        <el-table-column prop="netSalary" label="实发工资" width="120">
          <template #default="{ row }">
            <el-tag type="danger">{{ formatMoney(row.netSalary) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="warning">已生成</el-tag>
            <el-tag v-else-if="row.status === 2" type="success">已导出</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="genPagination.page"
        v-model:page-size="genPagination.pageSize"
        :total="genPagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadGenData"
        @current-change="loadGenData"
        style="margin-top: 16px;"
      />
    </el-card>

    <!-- ==================== 编辑弹窗（表A草稿编辑） ==================== -->
    <el-dialog v-model="editVisible" title="编辑工资项" width="420px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="员工">
          <span>{{ editRow?.empNo }} {{ editRow?.employeeName }}</span>
        </el-form-item>
        <el-form-item label="绩效工资">
          <el-input-number v-model="editForm.perfSalary" :min="0" :precision="2" controls-position="right"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="补贴">
          <el-input-number v-model="editForm.subsidy" :min="0" :precision="2" controls-position="right"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="加班费">
          <el-input-number v-model="editForm.overtimePay" :min="0" :precision="2" controls-position="right"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditConfirm" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 导入弹窗 ==================== -->
    <el-dialog v-model="importVisible" title="导入草稿工资" width="650px">
      <el-upload
        ref="uploadRef"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".xlsx,.xls"
      >
        <el-icon><UploadFilled /></el-icon>
        <div class="el-upload__text">拖拽文件到这里或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">
            支持列名：<b>工号（必填）</b>、<b>姓名（必填）</b>、月份、绩效工资、补贴、加班费<br/>
            <span style="color:#999">表头按名称匹配，列顺序不限。工号和姓名须与花名册一致，多余列自动忽略</span>
          </div>
        </template>
      </el-upload>

      <!-- 解析预览 -->
      <div v-if="previewData.length > 0" style="margin-top: 16px;">
        <el-alert
          :title="`已解析 ${previewData.length} 条数据`"
          type="success"
          :closable="false"
          show-icon
        />
        <el-table :data="previewData.slice(0, 5)" border size="small" style="margin-top: 8px;">
          <el-table-column prop="empNo" label="工号" width="90" />
          <el-table-column prop="employeeName" label="姓名" width="90" />
          <el-table-column prop="yearMonth" label="月份" width="90" />
          <el-table-column prop="baseSalary" label="基本工资" width="100">
            <template #default="{ row }">{{ formatMoney(row.baseSalary) }}</template>
          </el-table-column>
          <el-table-column prop="perfSalary" label="绩效工资" width="100">
            <template #default="{ row }">{{ formatMoney(row.perfSalary) }}</template>
          </el-table-column>
          <el-table-column prop="subsidy" label="补贴" width="80">
            <template #default="{ row }">{{ formatMoney(row.subsidy) }}</template>
          </el-table-column>
          <el-table-column prop="overtimePay" label="加班费" width="80">
            <template #default="{ row }">{{ formatMoney(row.overtimePay) }}</template>
          </el-table-column>
        </el-table>
        <p v-if="previewData.length > 5" style="color:#999; text-align:center; margin-top: 4px;">
          仅显示前 5 条，共 {{ previewData.length }} 条
        </p>
      </div>

      <!-- 解析错误 -->
      <el-alert
        v-if="parseError"
        :title="parseError"
        type="error"
        :closable="false"
        show-icon
        style="margin-top: 12px;"
      />

      <template #footer>
        <el-button @click="handleCloseImport">取消</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
        <el-button type="primary" @click="handleImportConfirm" :loading="importing"
          :disabled="!previewData.length">确认导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type UploadFile, type UploadInstance } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'
import { salarySheetApi } from '@/api/modules/salary'
import type { SalarySheet } from '@/api/types'

// ===== 当前月份 =====
const currentMonth = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
})

// ===== 表A - 草稿 =====
const draftData = ref<SalarySheet[]>([])
const draftLoading = ref(false)
const draftSelectedRows = ref<SalarySheet[]>([])

const onDraftSelect = (rows: SalarySheet[]) => { draftSelectedRows.value = rows }

const loadDraftData = async (withMonth = true) => {
  draftLoading.value = true
  try {
    const params: Record<string, any> = { status: 0, page: 1, pageSize: 200 }
    if (withMonth) params.month = currentMonth.value
    const res = await salarySheetApi.list(params)
    draftData.value = res.data.records || []
    console.log('loadDraftData:', draftData.value.length, '条记录', 'monthFilter:', withMonth ? currentMonth.value : '无')
  } catch (e) { console.error('加载草稿数据失败:', e) }
  finally { draftLoading.value = false }
}

// ===== 表B - 已生成 =====
const genData = ref<SalarySheet[]>([])
const genLoading = ref(false)
const genMonth = ref('')
const genPagination = reactive({ page: 1, pageSize: 10, total: 0 })

const loadGenData = async (withMonth = true) => {
  genLoading.value = true
  try {
    const params: Record<string, any> = { page: genPagination.page, pageSize: genPagination.pageSize, status: 1 }
    if (withMonth && genMonth.value) params.month = genMonth.value
    const res = await salarySheetApi.list(params)
    genData.value = res.data.records
    genPagination.total = res.data.total
    console.log('loadGenData:', genData.value.length, '条记录', 'monthFilter:', withMonth && genMonth.value ? genMonth.value : '无')
  } catch (e) { console.error('加载已生成数据失败:', e) }
  finally { genLoading.value = false }
}

// ===== 编辑草稿 =====
const saving = ref(false)
const editVisible = ref(false)
const editRow = ref<SalarySheet | null>(null)
const editForm = reactive({ perfSalary: 0, subsidy: 0, overtimePay: 0 })

const openDraftEdit = (row: SalarySheet) => {
  editRow.value = row
  editForm.perfSalary = row.perfSalary ?? 0
  editForm.subsidy = row.subsidy ?? 0
  editForm.overtimePay = row.overtimePay ?? 0
  editVisible.value = true
}

const handleEditConfirm = async () => {
  if (!editRow.value) return
  saving.value = true
  try {
    await salarySheetApi.update(editRow.value.id, { ...editForm })
    ElMessage.success('保存成功')
    editVisible.value = false
    loadDraftData()
  } catch (err: any) {
    const msg = err?.response?.data?.message || '保存失败'
    ElMessage.error(msg)
  } finally { saving.value = false }
}

// ===== 生成工资 =====
const handleGenerate = async () => {
  if (draftSelectedRows.value.length === 0) { ElMessage.warning('请先勾选草稿员工'); return }
  try {
    await ElMessageBox.confirm(
      `确认要为选中的 ${draftSelectedRows.value.length} 名员工生成 ${currentMonth.value} 工资表吗？`,
      '确认生成', { type: 'warning' })
  } catch { return }
  try {
    const employeeIds = draftSelectedRows.value.map(r => r.employeeId)
    await salarySheetApi.generate({ month: currentMonth.value, employeeIds })
    ElMessage.success('工资表生成成功')
    loadDraftData()
    loadGenData()
  } catch {
    ElMessage.error('生成失败')
  }
}

// ===== 导入 =====
const importVisible = ref(false)
const importFile = ref<File | null>(null)
const importing = ref(false)
const uploadRef = ref<UploadInstance>()

// 解析结果
interface ParsedSalaryRow {
  empNo: string
  employeeName: string
  yearMonth: string
  baseSalary: number
  perfSalary: number
  subsidy: number
  overtimePay: number
}
const previewData = ref<ParsedSalaryRow[]>([])
const parseError = ref('')

// 表头名称 → 字段代码映射（按名称匹配，不依赖列顺序）
const HEADER_MAP: Record<string, string> = {
  '工号': 'empNo',
  '姓名': 'employeeName',
  '月份': 'yearMonth',
  '基本工资': 'baseSalary',
  '绩效工资': 'perfSalary',
  '补贴': 'subsidy',
  '加班费': 'overtimePay',
}
// 必填列
const REQUIRED_HEADERS = ['工号', '姓名']
// 下载模板时的列顺序
const TEMPLATE_HEADERS = ['工号', '姓名', '月份', '绩效工资', '补贴', '加班费']

const handleImportClick = () => {
  importFile.value = null
  previewData.value = []
  parseError.value = ''
  uploadRef.value?.clearFiles()
  importVisible.value = true
}

const handleCloseImport = () => {
  importVisible.value = false
  importFile.value = null
  previewData.value = []
  parseError.value = ''
  uploadRef.value?.clearFiles()
}

const handleFileChange = (file: UploadFile) => {
  importFile.value = file.raw ?? null
  previewData.value = []
  parseError.value = ''
  if (importFile.value) {
    parseExcelFile(importFile.value)
  }
}

/** 解析 Excel 文件，按表头名称匹配 */
const parseExcelFile = (file: File) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = e.target?.result
      const workbook = XLSX.read(data, { type: 'array', cellText: true, cellDates: false })
      const sheetName = workbook.SheetNames[0]
      const worksheet = workbook.Sheets[sheetName]
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 }) as string[][]

      if (jsonData.length < 2) {
        parseError.value = '文件内容为空或只有表头，请检查文件'
        return
      }

      // 读取表头行，建立 名称→列索引 映射
      const headerRow = jsonData[0] as string[]
      const colMap: Record<string, number> = {}  // 字段代码 → 列索引
      const unrecognized: string[] = []

      headerRow.forEach((h, idx) => {
        const trimmed = (h ?? '').trim()
        if (!trimmed) return
        const fieldCode = HEADER_MAP[trimmed]
        if (fieldCode) {
          colMap[fieldCode] = idx
        } else {
          unrecognized.push(trimmed)
        }
      })

      // 校验必填列
      const missing: string[] = []
      for (const req of REQUIRED_HEADERS) {
        const code = HEADER_MAP[req]
        if (!(code in colMap)) {
          missing.push(req)
        }
      }
      if (missing.length > 0) {
        parseError.value = `缺少必填列：${missing.join('、')}`
        if (unrecognized.length > 0) {
          parseError.value += `；未识别的列：${unrecognized.join('、')}`
        }
        return
      }

      // 解析数据行
      const result: ParsedSalaryRow[] = []
      for (let i = 1; i < jsonData.length; i++) {
        const row = jsonData[i]
        if (!row || row.every((cell: any) => !cell || String(cell).trim() === '')) continue

        const getVal = (fieldCode: string): string => {
          const idx = colMap[fieldCode]
          if (idx === undefined) return ''
          const val = row[idx]
          return val !== undefined && val !== null ? String(val).trim() : ''
        }

        const empNo = getVal('empNo')
        const employeeName = getVal('employeeName')
        if (!empNo || !employeeName) continue  // 跳过工号或姓名为空的行

        result.push({
          empNo,
          employeeName,
          yearMonth: getVal('yearMonth') || currentMonth.value,
          baseSalary: parseFloat(getVal('baseSalary')) || 0,
          perfSalary: parseFloat(getVal('perfSalary')) || 0,
          subsidy: parseFloat(getVal('subsidy')) || 0,
          overtimePay: parseFloat(getVal('overtimePay')) || 0,
        })
      }

      if (result.length === 0) {
        parseError.value = '未解析到有效数据行，请检查工号和姓名列是否填写完整'
        return
      }

      previewData.value = result
      parseError.value = ''
    } catch {
      parseError.value = '文件解析失败，请检查文件是否为有效的 Excel 格式'
      previewData.value = []
    }
  }
  reader.readAsArrayBuffer(file)
}

/** 提交原始文件导入（后端 EasyExcel 支持按名称匹配，无需重建） */
const handleImportConfirm = async () => {
  if (previewData.value.length === 0) { ElMessage.warning('没有可导入的数据'); return }
  if (!importFile.value) { ElMessage.warning('请重新选择文件'); return }
  importing.value = true
  try {
    // 直接发送原始文件，后端 EasyExcel 按 @ExcelProperty 名称匹配列
    const fd = new FormData()
    fd.append('file', importFile.value)
    const res = await salarySheetApi.import(fd)
    const count = res.data ?? 0
    if (count > 0) {
      ElMessage.success(`导入完成，成功处理 ${count} 条记录`)
    } else {
      ElMessage.warning('导入完成，但所有行均处理失败，请检查数据')
    }
    handleCloseImport()
    // 导入后刷新两个表（不限制月份，确保数据可见）
    await loadDraftData(false)
    await loadGenData(false)
    // 调试：直接查全量数据对比
    debugImportResult()
  } catch (err: any) {
    const msg = err?.response?.data?.message || err?.message || '导入失败'
    ElMessage.error(msg)
  } finally { importing.value = false }
}

/** 导入后调试：查询全量数据（不限 status/月份），帮助定位数据加载问题 */
const debugImportResult = async () => {
  try {
    // 无状态过滤，查全部
    const allRes = await salarySheetApi.list({ page: 1, pageSize: 200 })
    const total = allRes.data?.total ?? 0
    const records = allRes.data?.records ?? []
    console.log('=== 导入后调试 ===')
    console.log('数据库全量记录数:', total)
    records.forEach((r: any, i: number) => {
      console.log(`  [${i}] id=${r.id} empNo=${r.empNo} name=${r.employeeName} month=${r.month || r.yearMonth} status=${r.status}`)
    })
    if (total === 0) {
      console.warn('警告：数据库中没有找到任何 SalSheet 记录！导入可能未持久化到数据库。')
    } else {
      const drafts = records.filter((r: any) => r.status === 0)
      const generated = records.filter((r: any) => r.status === 1)
      console.log(`status=0(草稿): ${drafts.length} 条, status=1(已生成): ${generated.length} 条`)
    }
  } catch (e) { console.error('调试查询失败:', e) }
}

/** 下载导入模板 */
const handleDownloadTemplate = () => {
  const exampleRow = ['EMP001', '张三', currentMonth.value, '2000', '500', '300']
  const worksheet = XLSX.utils.aoa_to_sheet([TEMPLATE_HEADERS, exampleRow])
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '草稿工资导入模板')
  const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
  const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  saveAs(blob, '草稿工资导入模板.xlsx')
}

// ===== 导出 =====
const handleExport = async () => {
  const params: Record<string, any> = {}
  if (genMonth.value) params.month = genMonth.value
  try {
    const res: any = await salarySheetApi.export(params)
    const blob = res.data

    // 检查 Blob 是否为后端返回的 JSON 错误（而非真正的 Excel 文件）
    if (blob.type.includes('json')) {
      const text = await blob.text()
      try {
        const err = JSON.parse(text)
        ElMessage.warning(err.message || '导出失败')
      } catch { ElMessage.error('导出失败') }
      return
    }

    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `工资表_${genMonth.value || '全部'}.xlsx`)
    document.body.appendChild(link); link.click(); document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
    // 导出成功后刷新已生成表格（状态从1变为2）
    loadGenData()
  } catch (err: any) {
    if (err?.response?.status === 404) ElMessage.warning('没有可导出的数据，请先生成工资表')
    else if (err?.response?.status === 403) ElMessage.error('没有导出权限，请联系管理员')
    else ElMessage.error('导出失败')
  }
}

// ===== 自定义字段列（从生成数据中提取列元数据） =====
const customFieldColumns = computed(() => {
  for (const row of genData.value) {
    if (row.customFields && row.customFields.length > 0) {
      return row.customFields.map(cf => ({ name: cf.name, code: cf.code, type: cf.type }))
    }
  }
  return []
})

const getCustomFieldValue = (row: SalarySheet, code: string) => {
  if (!row.customFields) return 0
  const cf = row.customFields.find(f => f.code === code)
  return cf ? cf.value : 0
}

// ===== 辅助 =====
const formatMoney = (val: any) => {
  if (val === null || val === undefined) return '0'
  return Number(val).toLocaleString()
}

// ===== 清除数据 =====
const handleClearDraft = async () => {
  try {
    await ElMessageBox.confirm('确认清除所有草稿工资数据吗？此操作不可恢复。', '清除草稿', { type: 'warning' })
  } catch { return }
  try {
    const res = await salarySheetApi.clear(0)
    ElMessage.success(`已清除 ${res.data} 条草稿记录`)
    loadDraftData()
  } catch { ElMessage.error('清除失败') }
}

const handleClearGen = async () => {
  try {
    await ElMessageBox.confirm('确认清除所有已生成工资数据吗？此操作不可恢复。', '清除已生成', { type: 'warning' })
  } catch { return }
  try {
    const res = await salarySheetApi.clear(1)
    ElMessage.success(`已清除 ${res.data} 条记录`)
    loadGenData()
  } catch { ElMessage.error('清除失败') }
}

onMounted(() => { loadDraftData(); loadGenData() })
</script>

<style lang="scss" scoped>
.salary-sheet {
  display: flex; flex-direction: column; gap: 20px;
  .card-title { font-size: 16px; font-weight: 600; }
  .toolbar { display: flex; gap: 8px; margin-bottom: 12px; flex-wrap: wrap; }
  .draft-card, .gen-card { }
  .gen-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px; }
  .gen-search { display: flex; gap: 8px; align-items: center; }
}
</style>
