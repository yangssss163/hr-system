import { ref, reactive, type Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { PageResponse } from '@/api/types'

/**
 * 通用分页参数
 */
export interface PaginationState {
  page: number
  pageSize: number
  total: number
}

/**
 * useCRUD 配置
 */
export interface UseCRUDOptions<T, FormType> {
  /** 列表查询函数，返回 PageResponse */
  fetchFn: (params: Record<string, any>) => Promise<{ data: PageResponse<T> }>

  /** 新增函数，返回创建后的完整数据对象 */
  createFn: (form: FormType) => Promise<{ data: T }>

  /** 编辑函数 */
  updateFn: (id: number, form: FormType) => Promise<any>

  /** 删除函数 */
  deleteFn: (id: number) => Promise<any>

  /** 查询附加参数（如 keyword, deptId 等） */
  extraParams?: () => Record<string, any>

  /** 新增后跳转到第几页，默认 1 */
  addPage?: number

  /** 表单默认值 */
  defaultForm: () => FormType

  /** 数据 ID 字段名，默认 'id' */
  idKey?: string
}

/**
 * 通用的 CRUD 列表页面组合式函数
 *
 * 解决的问题：
 * 1. 新增后自动跳转到第 1 页（而非停留在当前页导致看不到新数据）
 * 2. 删除最后一条时自动回退到上一页
 * 3. 统一的分页、loading、错误处理
 * 4. 提交按钮 loading 防止重复点击
 */
export function useCRUD<T extends Record<string, any>, FormType extends Record<string, any>>(
  options: UseCRUDOptions<T, FormType>
) {
  const { fetchFn, createFn, updateFn, deleteFn, extraParams, defaultForm, idKey = 'id' } = options

  // ---------- 状态 ----------
  const tableData = ref<T[]>([]) as Ref<T[]>
  const loading = ref(false)
  const submitting = ref(false)
  const dialogVisible = ref(false)
  const isEdit = ref(false)
  const editId = ref<number>(0)
  const form = reactive<FormType>(defaultForm()) as FormType

  const pagination = reactive<PaginationState>({
    page: 1,
    pageSize: 10,
    total: 0
  })

  // ---------- 数据加载 ----------
  const loadData = async (params?: { page?: number; pageSize?: number }) => {
    loading.value = true
    try {
      const queryParams: Record<string, any> = {
        page: params?.page ?? pagination.page,
        pageSize: params?.pageSize ?? pagination.pageSize,
        ...(extraParams ? extraParams() : {})
      }
      const res = await fetchFn(queryParams)
      const data = res.data
      tableData.value = (data.records ?? []) as T[]
      pagination.total = data.total ?? 0
      if (params?.page !== undefined) pagination.page = params.page
      if (params?.pageSize !== undefined) pagination.pageSize = params.pageSize
    } finally {
      loading.value = false
    }
  }

  // ---------- 分页 ----------
  const handleSizeChange = (size: number) => {
    loadData({ page: 1, pageSize: size })
  }

  const handlePageChange = (page: number) => {
    loadData({ page })
  }

  // ---------- 新增 ----------
  const handleAdd = () => {
    isEdit.value = false
    editId.value = 0
    Object.assign(form, defaultForm())
    dialogVisible.value = true
  }

  // ---------- 编辑 ----------
  const handleEdit = (row: T) => {
    isEdit.value = true
    editId.value = row[idKey] as number
    Object.assign(form, row)
    dialogVisible.value = true
  }

  // ---------- 删除 ----------
  const handleDelete = async (row: T) => {
    await ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' })
    await deleteFn(row[idKey] as number)
    ElMessage.success('删除成功')

    // 如果当前页只剩一条且不是第一页，回退到上一页
    if (tableData.value.length === 1 && pagination.page > 1) {
      loadData({ page: pagination.page - 1 })
    } else {
      loadData()
    }
  }

  // ---------- 批量删除 ----------
  const handleBatchDelete = async (selectedRows: T[], batchDeleteFn?: (ids: number[]) => Promise<any>) => {
    if (!batchDeleteFn) return
    if (selectedRows.length === 0) {
      ElMessage.warning('请先选择要删除的记录')
      return
    }
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.length} 条记录吗？`,
      '提示',
      { type: 'warning' }
    )
    const ids = selectedRows.map(row => row[idKey] as number)
    await batchDeleteFn(ids)
    ElMessage.success('删除成功')

    // 如果删光了当前页，回退上一页
    if (tableData.value.length <= selectedRows.length && pagination.page > 1) {
      loadData({ page: pagination.page - 1 })
    } else {
      loadData()
    }
  }

  // ---------- 表单提交 ----------
  const handleSubmit = async (
    formRef: any,
    onSuccess?: (data: T) => void
  ) => {
    if (!formRef) return
    await formRef.validate(async (valid: boolean) => {
      if (!valid) return
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateFn(editId.value, form)
          ElMessage.success('修改成功')
        } else {
          await createFn(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false

        // 新增跳转到指定页（默认第 1 页），编辑保持当前页
        if (isEdit.value) {
          await loadData()
        } else {
          await loadData({ page: options.addPage ?? 1 })
        }

        onSuccess?.(form as unknown as T)
      } catch {
        // http.ts 已统一处理错误提示
      } finally {
        submitting.value = false
      }
    })
  }

  return {
    // 状态
    tableData,
    loading,
    submitting,
    dialogVisible,
    isEdit,
    editId,
    form,
    pagination,

    // 方法
    loadData,
    handleAdd,
    handleEdit,
    handleDelete,
    handleBatchDelete,
    handleSubmit,
    handleSizeChange,
    handlePageChange
  }
}
