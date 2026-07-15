<template>
  <div class="onboarding-form">
    <el-card>
      <template #header><span>创建入职</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="工号" prop="empNo"><el-input v-model="form.empNo" placeholder="请输入工号" /></el-form-item>
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender"><el-radio :value="1">男</el-radio><el-radio :value="2">女</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" placeholder="请输入手机号" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="请输入邮箱" /></el-form-item>
        <el-form-item label="身份证号"><el-input v-model="form.idCard" placeholder="请输入身份证号" /></el-form-item>
        <el-form-item label="出生日期"><el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" placeholder="请选择出生日期" style="width: 100%" /></el-form-item>
        <el-form-item label="入职日期" prop="entryDate"><el-date-picker v-model="form.entryDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择入职日期" style="width: 100%" /></el-form-item>
        <el-form-item label="所属公司" prop="companyId">
          <el-select v-model="form.companyId" placeholder="请选择公司" style="width: 100%">
            <el-option v-for="item in companyOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择部门" style="width: 100%">
            <el-option v-for="item in deptOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位" prop="positionId">
          <el-select v-model="form.positionId" placeholder="请选择职位" style="width: 100%">
            <el-option v-for="item in positionOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="3">试用</el-radio><el-radio :value="1">正式</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <div style="display:flex;justify-content:flex-end;gap:12px;margin-top:24px;padding-top:16px;border-top:1px solid #ebeef5;">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交入职</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createEmployee } from '@/api/modules/employee'
import { getDeptTree } from '@/api/system/dept'
import { getPositionList } from '@/api/system/position'
import { getCompanyTree } from '@/api/system/company'
import type { EmployeeForm, Dept, Position, Company } from '@/api/types'
import router from '@/router'

const formRef = ref()
const deptOptions = ref<{ id: number; label: string }[]>([])
const positionOptions = ref<{ id: number; label: string }[]>([])
const companyOptions = ref<{ id: number; label: string }[]>([])

const form = reactive<EmployeeForm>({ empNo: '', name: '', gender: 1, idCard: '', birthday: '', phone: '', email: '', deptId: 0, positionId: 0, companyId: 0, entryDate: '', status: 3 })

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

const loadCompany = async () => {
  const res = await getCompanyTree()
  const result: { id: number; label: string }[] = []
  const traverse = (nodes: Company[], prefix = '') => {
    nodes.forEach(node => { result.push({ id: node.id, label: `${prefix}${node.name}` }); if (node.children) traverse(node.children, prefix + '　└─ ') })
  }
  traverse(res.data || [])
  companyOptions.value = result
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try { await createEmployee(form); ElMessage.success('入职创建成功'); router.push('/employee/list') } catch {}
  })
}

const handleCancel = () => { router.push('/employee/list') }

onMounted(() => { loadDept(); loadPosition(); loadCompany() })
</script>

<style lang="scss" scoped>
.onboarding-form {}
</style>