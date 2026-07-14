import http from '../http'
import type { PageResponse, ExpenseApproval, ExpenseApprovalForm, LeaveApproval, LeaveApprovalForm, LoanApproval, LoanApprovalForm, TravelApproval, TravelApprovalForm, ApprovalRequest } from '../types'

export const expenseApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<ExpenseApproval>>('/workflow/expenses', { params }),
  detail: (id: number) => http.get<ExpenseApproval>(`/workflow/expenses/${id}`),
  create: (data: ExpenseApprovalForm) => http.post<ExpenseApproval>('/workflow/expenses', data),
  update: (id: number, data: ExpenseApprovalForm) => http.put<ExpenseApproval>(`/workflow/expenses/${id}`, data),
  delete: (id: number) => http.delete(`/workflow/expenses/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put(`/workflow/expenses/${id}/approve`, data)
}

export const leaveApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<LeaveApproval>>('/workflow/leaves', { params }),
  detail: (id: number) => http.get<LeaveApproval>(`/workflow/leaves/${id}`),
  create: (data: LeaveApprovalForm) => http.post<LeaveApproval>('/workflow/leaves', data),
  update: (id: number, data: LeaveApprovalForm) => http.put<LeaveApproval>(`/workflow/leaves/${id}`, data),
  delete: (id: number) => http.delete(`/workflow/leaves/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put(`/workflow/leaves/${id}/approve`, data)
}

export const loanApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<LoanApproval>>('/workflow/loans', { params }),
  detail: (id: number) => http.get<LoanApproval>(`/workflow/loans/${id}`),
  create: (data: LoanApprovalForm) => http.post<LoanApproval>('/workflow/loans', data),
  update: (id: number, data: LoanApprovalForm) => http.put<LoanApproval>(`/workflow/loans/${id}`, data),
  delete: (id: number) => http.delete(`/workflow/loans/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put(`/workflow/loans/${id}/approve`, data)
}

export const travelApprovalApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<TravelApproval>>('/workflow/travels', { params }),
  detail: (id: number) => http.get<TravelApproval>(`/workflow/travels/${id}`),
  create: (data: TravelApprovalForm) => http.post<TravelApproval>('/workflow/travels', data),
  update: (id: number, data: TravelApprovalForm) => http.put<TravelApproval>(`/workflow/travels/${id}`, data),
  delete: (id: number) => http.delete(`/workflow/travels/${id}`),
  approve: (id: number, data: ApprovalRequest) => http.put(`/workflow/travels/${id}/approve`, data)
}