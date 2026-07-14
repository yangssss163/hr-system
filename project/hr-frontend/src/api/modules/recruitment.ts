import http from '../http'
import type { PageResponse, Resume, ResumeForm, InviteRequest, Interview, InterviewForm, InterviewEvaluateForm, EliminateRequest, OfferRequest, Question, QuestionForm, NotifyTemplate, NotifyTemplateForm, Blacklist, BlacklistForm, RecruitmentReport } from '../types'

export const resumeApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Resume>>('/resumes', { params }),
  detail: (id: number) => http.get<Resume>(`/resumes/${id}`),
  create: (data: ResumeForm) => http.post<Resume>('/resumes', data),
  update: (id: number, data: ResumeForm) => http.put<Resume>(`/resumes/${id}`, data),
  delete: (id: number) => http.delete(`/resumes/${id}`),
  import: (data: FormData) => http.post('/resumes/import', data, { headers: { 'Content-Type': 'multipart/form-data' } }),
  batchDelete: (data: { ids: number[] }) => http.delete('/resumes/batch', data),
  invite: (id: number, data: InviteRequest) => http.post(`/resumes/${id}/invite`, data)
}

export const interviewApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Interview>>('/interviews', { params }),
  detail: (id: number) => http.get<Interview>(`/interviews/${id}`),
  create: (data: InterviewForm) => http.post<Interview>('/interviews', data),
  update: (id: number, data: Partial<InterviewForm>) => http.put<Interview>(`/interviews/${id}`, data),
  checkin: (id: number) => http.post(`/interviews/${id}/checkin`),
  evaluate: (id: number, data: InterviewEvaluateForm) => http.put(`/interviews/${id}/evaluate`, data),
  pass: (id: number) => http.put(`/interviews/${id}/pass`),
  eliminate: (id: number, data: EliminateRequest) => http.put(`/interviews/${id}/eliminate`, data),
  offer: (id: number, data: OfferRequest) => http.put(`/interviews/${id}/offer`, data),
  confirmHire: (id: number) => http.put(`/interviews/${id}/confirm-hire`)
}

export const questionApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Question>>('/questions', { params }),
  detail: (id: number) => http.get<Question>(`/questions/${id}`),
  create: (data: QuestionForm) => http.post<Question>('/questions', data),
  update: (id: number, data: QuestionForm) => http.put<Question>(`/questions/${id}`, data),
  delete: (id: number) => http.delete(`/questions/${id}`)
}

export const notifyTemplateApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<NotifyTemplate>>('/notify-templates', { params }),
  detail: (id: number) => http.get<NotifyTemplate>(`/notify-templates/${id}`),
  create: (data: NotifyTemplateForm) => http.post<NotifyTemplate>('/notify-templates', data),
  update: (id: number, data: NotifyTemplateForm) => http.put<NotifyTemplate>(`/notify-templates/${id}`, data),
  delete: (id: number) => http.delete(`/notify-templates/${id}`)
}

export const blacklistApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Blacklist>>('/blacklists', { params }),
  create: (data: BlacklistForm) => http.post<Blacklist>('/blacklists', data),
  delete: (id: number) => http.delete(`/blacklists/${id}`)
}

export const recruitmentReportApi = {
  summary: () => http.get<RecruitmentReport>('/recruitment-reports/summary')
}