import http from '../http'
import type { PageResponse, AttendanceRecord, BatchFixRequest, AttendanceException, OaFlow, CardRule, CardRuleForm, Shift, ShiftForm, Holiday, HolidayForm, HolidayListResponse, LeaveQuota, LeaveType, LeaveTypeForm, AttendanceSummaryReport, AttendanceDeduction, AttendanceSummaryItem } from '../types'

export const attendanceRecordApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<AttendanceRecord>>('/attendance-records', { params }),
  import: (data: FormData) => http.post('/attendance-records/import', data),
  batchFix: (data: BatchFixRequest) => http.put('/attendance-records/batch-fix', data)
}

export const attendanceExceptionApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<AttendanceException>>('/attendance-exceptions', { params })
}

export const oaFlowApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<OaFlow>>('/oa-flows', { params }),
  import: (data: FormData) => http.post('/oa-flows/import', data),
  detail: (id: number) => http.get<OaFlow>(`/oa-flows/${id}`)
}

export const cardRuleApi = {
  list: () => http.get<CardRule[]>('/card-rules'),
  detail: (id: number) => http.get<CardRule>(`/card-rules/${id}`),
  create: (data: CardRuleForm) => http.post<CardRule>('/card-rules', data),
  update: (id: number, data: CardRuleForm) => http.put<CardRule>(`/card-rules/${id}`, data),
  delete: (id: number) => http.delete(`/card-rules/${id}`)
}

export const shiftApi = {
  list: () => http.get<Shift[]>('/shifts'),
  detail: (id: number) => http.get<Shift>(`/shifts/${id}`),
  create: (data: ShiftForm) => http.post<Shift>('/shifts', data),
  update: (id: number, data: ShiftForm) => http.put<Shift>(`/shifts/${id}`, data),
  delete: (id: number) => http.delete(`/shifts/${id}`)
}

export const holidayApi = {
  list: (year: number) => http.get<HolidayListResponse>('/holidays', { params: { year } }),
  create: (data: HolidayForm) => http.post<Holiday>('/holidays', data),
  update: (id: number, data: HolidayForm) => http.put<Holiday>(`/holidays/${id}`, data),
  copy: (id: number, targetYear: number) => http.post(`/holidays/${id}/copy?targetYear=${targetYear}`),
  delete: (id: number) => http.delete(`/holidays/${id}`)
}

export const leaveQuotaApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<LeaveQuota>>('/leave-quotas', { params }),
  adjust: (id: number, data: { totalDays: number }) => http.put(`/leave-quotas/${id}`, data)
}

export const leaveTypeApi = {
  list: () => http.get<LeaveType[]>('/leave-types'),
  create: (data: LeaveTypeForm) => http.post<LeaveType>('/leave-types', data),
  update: (id: number, data: LeaveTypeForm) => http.put<LeaveType>(`/leave-types/${id}`, data),
  delete: (id: number) => http.delete(`/leave-types/${id}`)
}

export const attendanceReportApi = {
  detail: (params: Record<string, any>) => http.get<PageResponse<AttendanceRecord>>('/attendance-reports/detail', { params }),
  summary: (params: Record<string, any>) => http.get<AttendanceSummaryItem[]>('/attendance-reports/summary', { params }),
}

export const attendanceDeductionApi = {
  list: () => http.get<AttendanceDeduction[]>('/attendance-deductions'),
  update: (id: number, data: { deduction: number; remark: string }) => http.put(`/attendance-deductions/${id}`, data)
}