import http from '../http'
import type { PageResponse, Notice, NoticeForm, Document, DocumentForm, Task, TaskForm, Schedule, ScheduleForm, Message, MessageForm } from '../types'

export const noticeApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Notice>>('/notices', { params }),
  detail: (id: number) => http.get<Notice>(`/notices/${id}`),
  create: (data: NoticeForm) => http.post<Notice>('/notices', data),
  update: (id: number, data: NoticeForm) => http.put<Notice>(`/notices/${id}`, data),
  delete: (id: number) => http.delete(`/notices/${id}`)
}

export const documentApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Document>>('/documents', { params }),
  detail: (id: number) => http.get<Document>(`/documents/${id}`),
  create: (data: DocumentForm) => http.post<Document>('/documents', data),
  update: (id: number, data: DocumentForm) => http.put<Document>(`/documents/${id}`, data),
  delete: (id: number) => http.delete(`/documents/${id}`)
}

export const taskApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Task>>('/tasks', { params }),
  detail: (id: number) => http.get<Task>(`/tasks/${id}`),
  create: (data: TaskForm) => http.post<Task>('/tasks', data),
  update: (id: number, data: TaskForm) => http.put<Task>(`/tasks/${id}`, data),
  delete: (id: number) => http.delete(`/tasks/${id}`)
}

export const scheduleApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Schedule>>('/schedules', { params }),
  detail: (id: number) => http.get<Schedule>(`/schedules/${id}`),
  create: (data: ScheduleForm) => http.post<Schedule>('/schedules', data),
  update: (id: number, data: ScheduleForm) => http.put<Schedule>(`/schedules/${id}`, data),
  delete: (id: number) => http.delete(`/schedules/${id}`)
}

export const messageApi = {
  list: (params: Record<string, any>) => http.get<PageResponse<Message>>('/messages', { params }),
  detail: (id: number) => http.get<Message>(`/messages/${id}`),
  create: (data: MessageForm) => http.post<Message>('/messages', data),
  markRead: (id: number) => http.patch(`/messages/${id}/read`),
  delete: (id: number) => http.delete(`/messages/${id}`)
}