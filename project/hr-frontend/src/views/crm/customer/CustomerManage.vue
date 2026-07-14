<template>
  <div class="customer-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增客户</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="客户名称" width="150" />
        <el-table-column prop="contact" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="客户名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="联系人" prop="contact"><el-input v-model="form.contact" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="行业"><el-input v-model="form.industry" /></el-form-item>
        <el-form-item label="来源"><el-input v-model="form.source" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerApi } from '@/api/modules/crm'
import type { Customer, CustomerForm } from '@/api/types'

const tableData = ref<Customer[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<CustomerForm>({ name: '', contact: '', phone: '', email: '', address: '', industry: '', source: '', status: 1 })
const rules = { name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }], contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }], phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await customerApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { name: '', contact: '', phone: '', email: '', address: '', industry: '', source: '', status: 1 }); dialogVisible.value = true }
const handleEdit = (row: Customer) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Customer) => { await ElMessageBox.confirm('确定删除？'); await customerApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await customerApi.update(editId.value, form)
    else await customerApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.customer-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>