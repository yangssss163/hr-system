<template>
  <div class="transfer-detail">
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="员工姓名">
          <el-input v-model="queryForm.keyword" placeholder="请输入员工姓名" style="width: 200px" />
        </el-form-item>
        <el-form-item label="异动类型">
          <el-select v-model="queryForm.transferType" placeholder="请选择异动类型" clearable style="width: 150px">
            <el-option value="regular" label="转正" />
            <el-option value="transfer" label="调岗" />
            <el-option value="promote" label="晋升" />
            <el-option value="dimission" label="离职" />
            <el-option value="rehire" label="返聘" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="toolbar">
        <el-button v-permission="'employee:create'" type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增异动</el-button>
      </div>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="empNo" label="工号" width="120" />
        <el-table-column prop="employeeName" label="员工姓名" width="120" />
        <el-table-column prop="transferTypeName" label="异动类型" width="100" />
        <el-table-column prop="beforeDeptName" label="异动前部门" width="150" />
        <el-table-column prop="beforePositionName" label="异动前职位" width="150" />
        <el-table-column prop="afterDeptName" label="异动后部门" width="150" />
        <el-table-column prop="afterPositionName" label="异动后职位" width="150" />
        <el-table-column prop="effectiveDate" label="生效日期" width="120" />
        <el-table-column prop="reason" label="异动原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-permission="'employee:update'" size="small" v-if="row.status === 1" @click="handleRevoke(row)">撤销</el-button>
            <el-button size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:16px;">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10,20,50,100]" layout="total,sizes,prev,pager,next,jumper" @size-change="handleSizeChange" @current-change="handlePageChange" />
      </div>
    </el-card>

    <el-dialog v-model="viewDialogVisible" title="异动详情" width="600px">
      <el-form :model="viewForm" label-width="120px" disabled>
        <el-form-item label="员工工号"><span>{{ viewForm.empNo }}</span></el-form-item>
        <el-form-item label="员工姓名"><span>{{ viewForm.employeeName }}</span></el-form-item>
        <el-form-item label="异动类型"><span>{{ viewForm.transferTypeName }}</span></el-form-item>
        <el-form-item label="异动前部门"><span>{{ viewForm.beforeDeptName }}</span></el-form-item>
        <el-form-item label="异动前职位"><span>{{ viewForm.beforePositionName }}</span></el-form-item>
        <el-form-item label="异动后部门"><span>{{ viewForm.afterDeptName }}</span></el-form-item>
        <el-form-item label="异动后职位"><span>{{ viewForm.afterPositionName }}</span></el-form-item>
        <el-form-item label="生效日期"><span>{{ viewForm.effectiveDate }}</span></el-form-item>
        <el-form-item label="异动原因"><span>{{ viewForm.reason }}</span></el-form-item>
        <el-form-item label="状态"><el-tag :type="getStatusType(viewForm.status)">{{ getStatusName(viewForm.status) }}</el-tag></el-form-item>
        <el-form-item label="创建时间"><span>{{ viewForm.createTime }}</span></el-form-item>
      </el-form>
      <template #footer><el-button @click="viewDialogVisible = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getTransferList, getTransferById, revokeTransfer } from '@/api/modules/employee'
import type { Transfer } from '@/api/types'
import router from '@/router'

const tableData = ref<Transfer[]>([])
const viewDialogVisible = ref(false)

const viewForm = reactive<Transfer>({ id: 0, employeeId: 0, empNo: '', employeeName: '', transferType: '', transferTypeName: '', beforeDeptId: 0, beforeDeptName: '', afterDeptId: 0, afterDeptName: '', beforePositionId: 0, beforePositionName: '', afterPositionId: 0, afterPositionName: '', effectiveDate: '', reason: '', status: 0, createTime: '' })

const queryForm = reactive({ keyword: '', transferType: undefined as string | undefined })
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const getStatusType = (status: number) => ({ 1: 'success', 0: 'danger', 2: 'warning' }[status] || 'info')
const getStatusName = (status: number) => ({ 1: '已生效', 0: '已撤销', 2: '待生效' }[status] || '未知')

const loadData = async () => {
  const res = await getTransferList({ page: pagination.current, pageSize: pagination.pageSize, keyword: queryForm.keyword, transferType: queryForm.transferType })
  tableData.value = res.data.records || []
  pagination.total = res.data.total || 0
}

const handleSizeChange = (size: number) => { pagination.pageSize = size; pagination.current = 1; loadData() }
const handlePageChange = (page: number) => { pagination.current = page; loadData() }
const handleReset = () => { queryForm.keyword = ''; queryForm.transferType = undefined; loadData() }
const handleAdd = () => { router.push('/employee/transfer') }

const handleView = async (row: Transfer) => {
  const res = await getTransferById(row.id)
  Object.assign(viewForm, res.data)
  viewDialogVisible.value = true
}

const handleRevoke = async (row: Transfer) => {
  await ElMessageBox.confirm('确定要撤销此异动吗？', '提示', { type: 'warning' })
  await revokeTransfer(row.id)
  ElMessage.success('撤销成功')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.transfer-detail {
  .search-card { margin-bottom: 16px; }
  .toolbar { margin-bottom: 16px; }
}
</style>