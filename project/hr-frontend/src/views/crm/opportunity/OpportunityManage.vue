<template>
  <div class="opportunity-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增商机</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="name" label="商机名称" width="150" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="stage" label="阶段" width="100" />
        <el-table-column prop="probability" label="成功率" width="80">
          <template #default="{ row }">{{ row.probability }}%</template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="expectedCloseDate" label="预计成交日期" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '进行中' : '已关闭' }}</el-tag>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商机' : '新增商机'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="客户" prop="customerId"><el-select v-model="form.customerId"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="商机名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="阶段" prop="stage"><el-select v-model="form.stage"><el-option label="需求确认" value="need_confirm" /><el-option label="方案设计" value="design" /><el-option label="报价" value="quote" /><el-option label="商务谈判" value="negotiate" /><el-option label="成交" value="closed" /></el-select></el-form-item>
        <el-form-item label="成功率" prop="probability"><el-input-number v-model="form.probability" :min="0" :max="100" style="width:100%" /></el-form-item>
        <el-form-item label="负责人" prop="ownerId"><el-input-number v-model="form.ownerId" style="width:100%" /></el-form-item>
        <el-form-item label="预计成交日期" prop="expectedCloseDate"><el-date-picker v-model="form.expectedCloseDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="进行中" :value="1" /><el-option label="已关闭" :value="0" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { opportunityApi, customerApi } from '@/api/modules/crm'
import type { Opportunity, OpportunityForm, Customer } from '@/api/types'

const tableData = ref<Opportunity[]>([])
const customers = ref<Customer[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<OpportunityForm>({ customerId: 0, name: '', amount: 0, stage: '', probability: 0, ownerId: 0, expectedCloseDate: '', status: 1 })
const rules = { customerId: [{ required: true, message: '请选择客户', trigger: 'change' }], name: [{ required: true, message: '请输入商机名称', trigger: 'blur' }], amount: [{ required: true, message: '请输入金额', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await opportunityApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadCustomers = async () => {
  try {
    const res = await customerApi.list({ pageSize: 1000 })
    customers.value = res.data.records
  } catch {}
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { customerId: 0, name: '', amount: 0, stage: '', probability: 0, ownerId: 0, expectedCloseDate: '', status: 1 }); dialogVisible.value = true }
const handleEdit = (row: Opportunity) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Opportunity) => { await ElMessageBox.confirm('确定删除？'); await opportunityApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await opportunityApi.update(editId.value, form)
    else await opportunityApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadCustomers() })
</script>

<style lang="scss" scoped>
.opportunity-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>