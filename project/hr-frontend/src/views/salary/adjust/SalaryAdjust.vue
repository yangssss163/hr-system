<template>
  <div class="salary-adjust">
    <el-card>
      <div class="toolbar">
        <el-button v-permission="'salary:adjust:create'" type="primary" @click="handleAdd">员工调薪</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="empNo" label="工号" width="100" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="beforeSalary" label="调薪前" width="120">
          <template #default="{ row }">{{ row.beforeSalary.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="afterSalary" label="调薪后" width="120">
          <template #default="{ row }">{{ row.afterSalary.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="adjustAmount" label="调整金额" width="120">
          <template #default="{ row }"><el-tag :type="row.adjustAmount > 0 ? 'success' : 'danger'">{{ row.adjustAmount > 0 ? '+' : '' }}{{ row.adjustAmount }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="adjustTypeName" label="调薪类型" width="120" />
        <el-table-column prop="effectiveDate" label="生效日期" width="120" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" title="员工调薪" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="员工" prop="employeeId"><el-select v-model="form.employeeId" filterable placeholder="搜索员工"><el-option v-for="e in employeeList" :key="e.id" :label="`${e.empNo} - ${e.name}`" :value="e.id" /></el-select></el-form-item>
        <el-form-item label="调薪后金额" prop="afterSalary"><el-input-number v-model="form.afterSalary" :min="0" :max="999999" style="width: 100%" /></el-form-item>
        <el-form-item label="调薪类型" prop="adjustType"><el-select v-model="form.adjustType"><el-option label="晋升调薪" value="promote" /><el-option label="调岗调薪" value="transfer" /><el-option label="年度调薪" value="annual" /><el-option label="特殊调薪" value="special" /></el-select></el-form-item>
        <el-form-item label="生效日期" prop="effectiveDate"><el-date-picker v-model="form.effectiveDate" type="date" value-format="yyyy-MM-dd" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { salaryAdjustApi } from '@/api/modules/salary'
import { getEmployeeList } from '@/api/modules/employee'
import type { SalaryAdjust, SalaryAdjustForm, Employee } from '@/api/types'

const tableData = ref<SalaryAdjust[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const employeeList = ref<Employee[]>([])
const form = reactive<SalaryAdjustForm>({ employeeId: 0, afterSalary: 0, adjustType: 'annual', effectiveDate: '', remark: '' })
const rules = { employeeId: [{ required: true, message: '请选择员工', trigger: 'blur' }], afterSalary: [{ required: true, message: '请输入调薪后金额', trigger: 'blur' }], adjustType: [{ required: true, message: '请选择调薪类型', trigger: 'blur' }], effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await salaryAdjustApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadEmployees = async () => { try { const res = await getEmployeeList({ page: 1, pageSize: 200 }); employeeList.value = (res.data.records || []).filter((e: Employee) => e.status === 1) } catch { /* ignore */ } }

const handleAdd = () => { Object.assign(form, { employeeId: 0, afterSalary: 0, adjustType: 'annual', effectiveDate: '', remark: '' }); loadEmployees(); dialogVisible.value = true }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    await salaryAdjustApi.create(form)
    ElMessage.success('调薪成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.salary-adjust {
  .toolbar { margin-bottom: 16px; }
}
</style>