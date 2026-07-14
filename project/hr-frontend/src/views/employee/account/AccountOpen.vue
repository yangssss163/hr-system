<template>
  <div class="account-open">
    <el-card>
      <template #header><span>账号开通</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="form.employeeId" placeholder="请选择员工" style="width: 100%" @change="handleEmployeeChange">
            <el-option v-for="item in employeeOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="item in roleOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div style="display:flex;justify-content:flex-end;gap:12px;margin-top:24px;padding-top:16px;border-top:1px solid #ebeef5;">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">开通账号</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { openAccount, getEmployeeList } from '@/api/modules/employee'
import { getRoleList } from '@/api/system/role'
import type { AccountOpenForm, Employee, Role } from '@/api/types'
import router from '@/router'

const formRef = ref()
const employeeOptions = ref<{ id: number; label: string }[]>([])
const roleOptions = ref<{ id: number; name: string }[]>([])
const confirmPassword = ref('')

const form = reactive<AccountOpenForm>({ employeeId: 0, username: '', password: '', roleIds: [] })

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadEmployees = async () => {
  const res = await getEmployeeList({ pageSize: 1000, status: 1 })
  employeeOptions.value = (res.data?.records || []).map((item: Employee) => ({ id: item.id, label: `${item.name} (${item.empNo})` }))
}

const loadRoles = async () => {
  const res = await getRoleList({ pageSize: 1000 })
  roleOptions.value = (res.data?.records || []).map((item: Role) => ({ id: item.id, name: item.name }))
}

const handleEmployeeChange = async () => {
  if (form.employeeId) {
    const res = await getEmployeeList({ pageSize: 1, keyword: '' })
    const employee = res.data?.records?.find((e: Employee) => e.id === form.employeeId)
    if (employee) form.username = employee.name.toLowerCase().replace(/\s+/g, '')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  if (form.password !== confirmPassword.value) { ElMessage.error('两次密码输入不一致'); return }
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try { await openAccount(form); ElMessage.success('账号开通成功'); router.push('/employee/list') } catch {}
  })
}

const handleCancel = () => { router.push('/employee/list') }

onMounted(() => { loadEmployees(); loadRoles() })
</script>

<style lang="scss" scoped>.account-open {}</style>