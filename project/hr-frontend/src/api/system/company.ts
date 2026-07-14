import http from '../http'
import type { Company, CompanyForm } from '../types'

export const getCompanyTree = () => {
  return http.get<Company[]>('/companies/tree')
}

export const getCompanyById = (id: number) => {
  return http.get<Company>(`/companies/${id}`)
}

export const createCompany = (data: CompanyForm) => {
  return http.post('/companies', data)
}

export const updateCompany = (id: number, data: CompanyForm) => {
  return http.put(`/companies/${id}`, data)
}

export const deleteCompany = (id: number) => {
  return http.delete(`/companies/${id}`)
}