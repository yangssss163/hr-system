<template>
  <div class="table-page">
    <el-card class="search-card" v-if="$slots.search">
      <slot name="search" />
    </el-card>
    <el-card>
      <div class="toolbar" v-if="$slots.toolbar">
        <slot name="toolbar" :selectedRows="selectedRows" />
      </div>
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="(rows: any[]) => selectedRows = rows"
        border
        stripe
      >
        <slot name="table" />
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

const loading = ref(false)
const tableData = ref<any[]>([])
const selectedRows = ref<any[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.current = 1
}

const handlePageChange = (page: number) => {
  pagination.current = page
}

defineExpose({
  loading,
  tableData,
  selectedRows,
  pagination,
  handleSizeChange,
  handlePageChange
})
</script>

<style lang="scss" scoped>
.table-page {
  .search-card {
    margin-bottom: 16px;
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>