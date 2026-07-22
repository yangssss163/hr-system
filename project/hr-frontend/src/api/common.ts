import http from './http'
import { ElMessage } from 'element-plus'
import type { ApiResponse, UploadResponse, OptionItem } from './types'

export const uploadFile = async (formData: FormData): Promise<UploadResponse> => {
  const res = await http.rawPost('/common/upload', formData)
  const body = res.data as ApiResponse<UploadResponse>
  if (body.code !== 200) {
    throw new Error(body.message || '文件上传失败')
  }
  return body.data
}

export const downloadFile = (fileKey: string) => {
  return http.rawGet(`/common/download/${fileKey}`, { responseType: 'blob' })
}

/**
 * 安全下载 Blob 文件（带内容校验，防止下载到 JSON 错误）
 * @param blob  响应 Blob
 * @param fileName 下载文件名
 * @param failMsg  失败提示信息
 * @returns true 表示下载成功，false 表示内容为错误 JSON
 */
export const safeDownloadBlob = async (blob: Blob, fileName: string, failMsg = '下载失败'): Promise<boolean> => {
  const type = blob.type || ''

  // 1. Content-Type 明确为 JSON/HTML 错误响应 → 解析并提示
  if (type.includes('json') || type.includes('html')) {
    try {
      const text = await blob.text()
      const err = JSON.parse(text)
      ElMessage.warning(err.message || failMsg)
    } catch {
      ElMessage.error(failMsg)
    }
    return false
  }

  // 2. 明确的二进制类型 → 直接下载，不做任何转换
  const BINARY_TYPES = [
    'application/vnd.openxmlformats-officedocument.spreadsheetml',  // .xlsx
    'application/vnd.openxmlformats-officedocument.wordprocessingml', // .docx
    'application/vnd.ms-',       // .xls, .doc, .ppt
    'application/pdf',
    'application/zip',
    'application/octet-stream',
    'image/',
    'video/',
    'audio/',
  ]
  const isBinary = BINARY_TYPES.some(t => type.includes(t))
  if (isBinary) {
    triggerDownload(blob, fileName)
    return true
  }

  // 3. 未知类型 → 只 peek 前 100 字节检查是否为 JSON 错误，不消费整个 Blob
  try {
    const peek = await blob.slice(0, 100).text()
    if (peek.trimStart().startsWith('{') && peek.includes('"code"')) {
      try {
        const err = JSON.parse(peek)
        ElMessage.warning(err.message || failMsg)
        return false
      } catch { /* JSON 不完整，继续 */ }
    }
    // 不是 JSON 错误，直接下载原始 Blob
    triggerDownload(blob, fileName)
    return true
  } catch {
    // peek 也失败了，直接下载
    triggerDownload(blob, fileName)
    return true
  }
}

/** 触发浏览器下载 */
const triggerDownload = (blob: Blob, fileName: string) => {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  a.click()
  window.URL.revokeObjectURL(url)
}

export const getOptions = (type: string) => {
  return http.get<OptionItem[]>('/common/options', { params: { type } })
}