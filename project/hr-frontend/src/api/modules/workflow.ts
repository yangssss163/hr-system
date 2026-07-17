import http from '../http'
import type { PageResponse, ExpenseApproval, ExpenseApprovalForm, LeaveApproval, LeaveApprovalForm, LoanApproval, LoanApprovalForm, TravelApproval, TravelApprovalForm, ApprovalRequest } from '../types'

export const expenseApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<ExpenseApproval>>('/workflow/expenses', { params }),
  detail: (id: number) => http.get<ExpenseApproval>(`/workflow/expenses/${id}`),
  create: (data: ExpenseApprovalForm) => http.post<void>('/workflow/expenses', data),
  update: (id: number, data: ExpenseApprovalForm) => http.put<void>(`/workflow/expenses/${id}`, data),
  delete: (id: number) => http.delete<void>(`/workflow/expenses/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put<void>(`/workflow/expenses/${id}/approve`, data)
}

export const leaveApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<LeaveApproval>>('/workflow/leaves', { params }),
  detail: (id: number) => http.get<LeaveApproval>(`/workflow/leaves/${id}`),
  create: (data: LeaveApprovalForm) => http.post<void>('/workflow/leaves', data),
  update: (id: number, data: LeaveApprovalForm) => http.put<void>(`/workflow/leaves/${id}`, data),
  delete: (id: number) => http.delete<void>(`/workflow/leaves/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put<void>(`/workflow/leaves/${id}/approve`, data)
}

export const loanApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<LoanApproval>>('/workflow/loans', { params }),
  detail: (id: number) => http.get<LoanApproval>(`/workflow/loans/${id}`),
  create: (data: LoanApprovalForm) => http.post<void>('/workflow/loans', data),
  update: (id: number, data: LoanApprovalForm) => http.put<void>(`/workflow/loans/${id}`, data),
  delete: (id: number) => http.delete<void>(`/workflow/loans/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put<void>(`/workflow/loans/${id}/approve`, data)
}

export const travelApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<TravelApproval>>('/workflow/travels', { params }),
  detail: (id: number) => http.get<TravelApproval>(`/workflow/travels/${id}`),
  create: (data: TravelApprovalForm) => http.post<void>('/workflow/travels', data),
  update: (id: number, data: TravelApprovalForm) => http.put<void>(`/workflow/travels/${id}`, data),
  delete: (id: number) => http.delete<void>(`/workflow/travels/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put<void>(`/workflow/travels/${id}/approve`, data)
}
