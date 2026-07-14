import http from '../http'
import type { PageResponse, Customer, CustomerForm, Opportunity, OpportunityForm, Order, OrderForm, Payment, PaymentForm, Refund, RefundForm, CrmExpense, CrmExpenseForm } from '../types'

export const customerApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Customer>>('/customers', { params }),
  detail: (id: number) => http.get<Customer>(`/customers/${id}`),
  create: (data: CustomerForm) => http.post<Customer>('/customers', data),
  update: (id: number, data: CustomerForm) => http.put<Customer>(`/customers/${id}`, data),
  delete: (id: number) => http.delete(`/customers/${id}`)
}

export const opportunityApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Opportunity>>('/opportunities', { params }),
  detail: (id: number) => http.get<Opportunity>(`/opportunities/${id}`),
  create: (data: OpportunityForm) => http.post<Opportunity>('/opportunities', data),
  update: (id: number, data: OpportunityForm) => http.put<Opportunity>(`/opportunities/${id}`, data),
  delete: (id: number) => http.delete(`/opportunities/${id}`)
}

export const orderApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Order>>('/orders', { params }),
  detail: (id: number) => http.get<Order>(`/orders/${id}`),
  create: (data: OrderForm) => http.post<Order>('/orders', data),
  update: (id: number, data: OrderForm) => http.put<Order>(`/orders/${id}`, data),
  delete: (id: number) => http.delete(`/orders/${id}`)
}

export const paymentApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Payment>>('/payments', { params }),
  detail: (id: number) => http.get<Payment>(`/payments/${id}`),
  create: (data: PaymentForm) => http.post<Payment>('/payments', data),
  update: (id: number, data: PaymentForm) => http.put<Payment>(`/payments/${id}`, data),
  delete: (id: number) => http.delete(`/payments/${id}`)
}

export const refundApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Refund>>('/refunds', { params }),
  detail: (id: number) => http.get<Refund>(`/refunds/${id}`),
  create: (data: RefundForm) => http.post<Refund>('/refunds', data),
  update: (id: number, data: RefundForm) => http.put<Refund>(`/refunds/${id}`, data),
  delete: (id: number) => http.delete(`/refunds/${id}`)
}

export const expenseApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<CrmExpense>>('/expenses', { params }),
  detail: (id: number) => http.get<CrmExpense>(`/expenses/${id}`),
  create: (data: CrmExpenseForm) => http.post<CrmExpense>('/expenses', data),
  update: (id: number, data: CrmExpenseForm) => http.put<CrmExpense>(`/expenses/${id}`, data),
  delete: (id: number) => http.delete(`/expenses/${id}`)
}