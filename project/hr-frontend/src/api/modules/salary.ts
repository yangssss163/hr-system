import http from '../http'
import type { PageResponse, SalaryRule, SalaryField, SalaryFieldForm, SalaryAdjust, SalaryAdjustForm, SalarySheet } from '../types'

export const salaryRuleApi = {
  list: () => http.get<SalaryRule[]>('/salary-rules'),
  update: (id: number, data: { value: number; remark: string }) => http.put(`/salary-rules/${id}`, data)
}

export const salaryFieldApi = {
  list: () => http.get<SalaryField[]>('/salary-fields'),
  detail: (id: number) => http.get<SalaryField>(`/salary-fields/${id}`),
  create: (data: SalaryFieldForm) => http.post<SalaryField>('/salary-fields', data),
  update: (id: number, data: SalaryFieldForm) => http.put<SalaryField>(`/salary-fields/${id}`, data),
  delete: (id: number) => http.delete(`/salary-fields/${id}`)
}

export const salaryAdjustApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<SalaryAdjust>>('/salary-adjusts', { params }),
  detail: (id: number) => http.get<SalaryAdjust>(`/salary-adjusts/${id}`),
  create: (data: SalaryAdjustForm) => http.post<SalaryAdjust>('/salary-adjusts', data)
}

export const salarySheetApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<SalarySheet>>('/salary-sheets', { params }),
  sync: (data: { month: string; deptId?: number }) => http.post('/salary-sheets/sync', data),
  generate: (data: { month: string; employeeIds: number[] }) => http.post('/salary-sheets/generate', data),
  update: (id: number, data: { perfSalary?: number; subsidy?: number; overtimePay?: number }) =>
    http.put(`/salary-sheets/${id}`, data),
  import: (data: FormData) => http.post<number>('/salary-sheets/import', data),
  export: (params: Record<string, any>) => http.rawGet('/salary-sheets/export', { params, responseType: 'blob' }),
  clear: (status: number) => http.delete<number>(`/salary-sheets/clear?status=${status}`),
}