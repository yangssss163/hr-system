import http from '../http'
import type { PageResponse, PerfLevel, PerfLevelForm, PerfSalary, PerfSalaryForm, PerfPlan, PerfPlanForm, PerfRecord, PerfRecordForm, PerfRecordDetail } from '../types'

export const perfLevelApi = {
  list: () => http.get<PerfLevel[]>('/perf-levels'),
  detail: (id: number) => http.get<PerfLevel>(`/perf-levels/${id}`),
  create: (data: PerfLevelForm) => http.post('/perf-levels', data),
  update: (id: number, data: PerfLevelForm) => http.put(`/perf-levels/${id}`, data),
  delete: (id: number) => http.delete(`/perf-levels/${id}`)
}

export const perfSalaryApi = {
  list: () => http.get<PerfSalary[]>('/perf-salaries'),
  detail: (id: number) => http.get<PerfSalary>(`/perf-salaries/${id}`),
  create: (data: PerfSalaryForm) => http.post('/perf-salaries', data),
  update: (id: number, data: PerfSalaryForm) => http.put(`/perf-salaries/${id}`, data),
  delete: (id: number) => http.delete(`/perf-salaries/${id}`)
}

export const perfPlanApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<PerfPlan>>('/perf-plans', { params }),
  detail: (id: number) => http.get<PerfPlan>(`/perf-plans/${id}`),
  create: (data: PerfPlanForm) => http.post('/perf-plans', data),
  update: (id: number, data: PerfPlanForm) => http.put(`/perf-plans/${id}`, data),
  delete: (id: number) => http.delete(`/perf-plans/${id}`)
}

export const perfRecordApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<PerfRecord>>('/perf-records', { params }),
  detail: (id: number) => http.get<PerfRecordDetail>(`/perf-records/${id}`),
  create: (data: PerfRecordForm) => http.post('/perf-records', data),
  update: (id: number, data: PerfRecordForm) => http.put(`/perf-records/${id}`, data),
  delete: (id: number) => http.delete(`/perf-records/${id}`)
}

export const perfReportApi = {
  detail: (params: Record<string, any>) => http.get<PageResponse<PerfRecord>>('/perf-reports/detail', { params }),
  deptSummary: (params: Record<string, any>) => http.get('/perf-reports/dept-summary', { params }),
  employeeSummary: (params: Record<string, any>) => http.get('/perf-reports/employee-summary', { params })
}