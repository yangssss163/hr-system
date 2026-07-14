<template>
  <div class="transfer-apply">
    <el-card>
      <template #header><span>异动申请</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="form.employeeId" placeholder="请选择员工" style="width: 100%" @change="handleEmployeeChange">
            <el-option v-for="item in employeeOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="异动类型" prop="transferType">
          <el-select v-model="form.transferType" placeholder="请选择异动类型" style="width: 100%">
            <el-option value="regular" label="转正" />
            <el-option value="transfer" label="调岗" />
            <el-option value="promote" label="晋升" />
            <el-option value="dimission" label="离职" />
            <el-option value="rehire" label="返聘" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showDeptPosition" label="异动后部门" prop="afterDeptId">
          <el-select v-model="form.afterDeptId" placeholder="请选择部门" style="width: 100%">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showDeptPosition" label="异动后职位" prop="afterPositionId">
          <el-select v-model="form.afterPositionId" placeholder="请选择职位" style="width: 100%">
            <el-option v-for="item in positionOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="生效日期" prop="effectiveDate">
          <el-date-picker v-model="form.effectiveDate" type="date" placeholder="请选择生效日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="异动原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入异动原因" :rows="3" />
        </el-form-item>
      </el-form>
      <div style="display:flex;justify-content:flex-end;gap:12px;margin-top:24px;padding-top:16px;border-top:1px solid #ebeef5;">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交申请</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createTransfer, getEmployeeList } from '@/api/modules/employee'
import { getDeptTree } from '@/api/system/dept'
import { getPositionList } from '@/api/system/position'
import type { TransferForm, Employee, Dept, Position } from '@/api/types'
import router from '@/router'

const formRef = ref()
const employeeOptions = ref<{ id: number; label: string }[]>([])
const deptOptions = ref<{ id: number; label: string }[]>([])
const positionOptions = ref<{ id: number; label: string }[]>([])
const currentEmployee = ref<Employee | null>(null)

const form = reactive<TransferForm>({ employeeId: 0, transferType: '', afterDeptId: 0, afterPositionId: 0, effectiveDate: '', reason: '' })

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  transferType: [{ required: true, message: '请选择异动类型', trigger: 'change' }],
  afterDeptId: [{ required: true, message: '请选择异动后部门', trigger: 'change' }],
  afterPositionId: [{ required: true, message: '请选择异动后职位', trigger: 'change' }],
  effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入异动原因', trigger: 'blur' }]
}

const showDeptPosition = computed(() => form.transferType !== 'dimission')

const loadEmployees = async () => {
  const res = await getEmployeeList({ pageSize: 1000, status: 1 })
  employeeOptions.value = (res.data?.records || []).map((item: Employee) => ({ id: item.id, label: `${item.name} (${item.empNo})` }))
}

const loadDept = async () => {
  const res = await getDeptTree()
  const result: { id: number; label: string }[] = []
  const traverse = (nodes: Dept[], prefix = '') => {
    nodes.forEach(node => { result.push({ id: node.id, label: `${prefix}${node.name}` }); if (node.children) traverse(node.children, prefix + '　└─ ') })
  }
  traverse(res.data || [])
  deptOptions.value = result
}

const loadPosition = async () => {
  const res = await getPositionList({ pageSize: 1000 })
  positionOptions.value = (res.data?.records || []).map((item: Position) => ({ id: item.id, label: item.name }))
}

const handleEmployeeChange = async () => {
  if (form.employeeId) {
    const res = await getEmployeeList({ pageSize: 1, keyword: '' })
    currentEmployee.value = res.data?.records?.find((e: Employee) => e.id === form.employeeId) || null
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try { await createTransfer(form); ElMessage.success('异动申请提交成功'); router.push('/employee/transfer-detail') } catch {}
  })
}

const handleCancel = () => { router.push('/employee/transfer-detail') }

onMounted(() => { loadEmployees(); loadDept(); loadPosition() })
</script>

<style lang="scss" scoped>.transfer-apply {}</style>