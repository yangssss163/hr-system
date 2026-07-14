<template>
  <div class="order-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增订单</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="opportunityName" label="商机名称" width="150" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">{{ statusMap[row.status]?.label || row.status }}</el-tag>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑订单' : '新增订单'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="客户" prop="customerId"><el-select v-model="form.customerId"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="商机"><el-select v-model="form.opportunityId"><el-option v-for="o in opportunities" :key="o.id" :label="o.name" :value="o.id" /></el-select></el-form-item>
        <el-form-item label="订单编号" prop="orderNo"><el-input v-model="form.orderNo" /></el-form-item>
        <el-form-item label="金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="状态" prop="status"><el-select v-model="form.status"><el-option label="待付款" value="pending_payment" /><el-option label="已付款" value="paid" /><el-option label="已发货" value="shipped" /><el-option label="已完成" value="completed" /><el-option label="已取消" value="cancelled" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, customerApi, opportunityApi } from '@/api/modules/crm'
import type { Order, OrderForm, Customer, Opportunity } from '@/api/types'

const tableData = ref<Order[]>([])
const customers = ref<Customer[]>([])
const opportunities = ref<Opportunity[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const statusMap: Record<string, { label: string; type: string }> = {
  pending_payment: { label: '待付款', type: 'warning' },
  paid: { label: '已付款', type: 'success' },
  shipped: { label: '已发货', type: 'info' },
  completed: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'danger' }
}

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<OrderForm>({ customerId: 0, opportunityId: 0, orderNo: '', amount: 0, status: 'pending_payment' })
const rules = { customerId: [{ required: true, message: '请选择客户', trigger: 'change' }], orderNo: [{ required: true, message: '请输入订单编号', trigger: 'blur' }], amount: [{ required: true, message: '请输入金额', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await orderApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadOptions = async () => {
  try {
    const [customerRes, opportunityRes] = await Promise.all([customerApi.list({ pageSize: 1000 }), opportunityApi.list({ pageSize: 1000 })])
    customers.value = customerRes.data.records
    opportunities.value = opportunityRes.data.records
  } catch {}
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { customerId: 0, opportunityId: 0, orderNo: '', amount: 0, status: 'pending_payment' }); dialogVisible.value = true }
const handleEdit = (row: Order) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Order) => { await ElMessageBox.confirm('确定删除？'); await orderApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await orderApi.update(editId.value, form)
    else await orderApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadOptions() })
</script>

<style lang="scss" scoped>
.order-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>