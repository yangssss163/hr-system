<template>
  <div class="payment-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增回款</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="amount" label="回款金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="paymentDate" label="回款日期" width="120" />
        <el-table-column prop="paymentMethod" label="付款方式" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '已确认' : '待确认' }}</el-tag>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑回款' : '新增回款'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="订单" prop="orderId"><el-select v-model="form.orderId"><el-option v-for="o in orders" :key="o.id" :label="o.orderNo" :value="o.id" /></el-select></el-form-item>
        <el-form-item label="回款金额" prop="amount"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="回款日期" prop="paymentDate"><el-date-picker v-model="form.paymentDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="付款方式" prop="paymentMethod"><el-select v-model="form.paymentMethod"><el-option label="银行转账" value="bank_transfer" /><el-option label="现金" value="cash" /><el-option label="支付宝" value="alipay" /><el-option label="微信支付" value="wechat" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="待确认" :value="0" /><el-option label="已确认" :value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { paymentApi, orderApi } from '@/api/modules/crm'
import type { Payment, PaymentForm, Order } from '@/api/types'

const tableData = ref<Payment[]>([])
const orders = ref<Order[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const form = reactive<PaymentForm>({ orderId: 0, amount: 0, paymentDate: '', paymentMethod: '', status: 0 })
const rules = { orderId: [{ required: true, message: '请选择订单', trigger: 'change' }], amount: [{ required: true, message: '请输入回款金额', trigger: 'blur' }], paymentDate: [{ required: true, message: '请选择回款日期', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await paymentApi.list({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const loadOrders = async () => {
  try {
    const res = await orderApi.list({ pageSize: 1000 })
    orders.value = res.data.records
  } catch {}
}

const handleAdd = () => { isEdit.value = false; editId.value = 0; Object.assign(form, { orderId: 0, amount: 0, paymentDate: '', paymentMethod: '', status: 0 }); dialogVisible.value = true }
const handleEdit = (row: Payment) => { isEdit.value = true; editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: Payment) => { await ElMessageBox.confirm('确定删除？'); await paymentApi.delete(row.id); ElMessage.success('删除成功'); loadData() }

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await paymentApi.update(editId.value, form)
    else await paymentApi.create(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  })
}

onMounted(() => { loadData(); loadOrders() })
</script>

<style lang="scss" scoped>
.payment-manage { .toolbar { margin-bottom: 16px; display: flex; gap: 12px; } }
</style>