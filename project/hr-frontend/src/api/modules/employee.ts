import http from '@/api/http'
import type { PageResponse, Employee, EmployeeDetail, EmployeeForm, Transfer, TransferForm, AccountOpenForm, EmployeeImportResponse } from '@/api/types'

export const getEmployeeList = (params?: { page?: number; pageSize?: number; keyword?: string; deptId?: number; positionId?: number; companyId?: number; status?: number; entryDateStart?: string; entryDateEnd?: string }) => {
  return http.get<PageResponse<Employee>>('/employees', { params })
}

export const getEmployeeById = (id: number) => {
  return http.get<EmployeeDetail>(`/employees/${id}`)
}

export const createEmployee = (data: EmployeeForm) => {
  return http.post('/employees', data)
}

export const updateEmployee = (id: number, data: EmployeeForm) => {
  return http.put(`/employees/${id}`, data)
}

export const deleteEmployee = (id: number) => {
  return http.delete(`/employees/${id}`)
}

export const batchDeleteEmployees = (ids: number[]) => {
  return http.delete('/employees/batch', { ids })
}

export const importEmployees = (formData: FormData) => {
  return http.post<EmployeeImportResponse>('/employees/import', formData)
}

export const exportEmployees = (params?: { keyword?: string; deptId?: number; status?: number }) => {
  return http.rawGet('/employees/export', { params, responseType: 'blob' })
}

export const getTransferList = (params?: { page?: number; pageSize?: number; keyword?: string; employeeId?: number; transferType?: string; startDate?: string; endDate?: string }) => {
  return http.get<PageResponse<Transfer>>('/transfers', { params })
}

export const getTransferById = (id: number) => {
  return http.get<Transfer>(`/transfers/${id}`)
}

export const createTransfer = (data: TransferForm) => {
  return http.post('/transfers', data)
}

export const revokeTransfer = (id: number) => {
  return http.put(`/transfers/${id}/revoke`)
}

export const openAccount = (data: AccountOpenForm) => {
  return http.post('/accounts/open', data)
}

export const toggleAccount = (id: number) => {
  return http.put(`/accounts/${id}/toggle`)
}