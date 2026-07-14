import http from './http'
import type { ApiResponse, UploadResponse, OptionItem } from './types'

export const uploadFile = (formData: FormData) => {
  return http.rawPost('/common/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export const downloadFile = (fileKey: string) => {
  return http.rawGet(`/common/download/${fileKey}`, { responseType: 'blob' })
}

export const getOptions = (type: string) => {
  return http.get<OptionItem[]>('/common/options', { params: { type } })
}