import axios, { AxiosInstance, Method } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from './types'

const BASE_URL = '/api'

const instance: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // FormData 上传时删除 Content-Type，让浏览器自动设置 multipart/form-data + boundary
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

interface RequestConfig {
  params?: Record<string, any>
  headers?: Record<string, string>
}

const request = async <T = any>(
  method: Method,
  url: string,
  data?: any,
  config?: RequestConfig
): Promise<ApiResponse<T>> => {
  try {
    const response = await instance({
      method,
      url,
      data,
      params: config?.params,
      headers: config?.headers
    })
    const res = response.data as ApiResponse<T>
    // 兼容后端未用 Result<> 包装的响应（如 workflow 模块部分接口）
    if (res.code === undefined && response.status === 200) {
      return { code: 200, message: '成功', data: res as unknown as T }
    }
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401 || res.code === 1003) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
      const err = new Error(res.message || 'Error') as any
      err.isAuthError = true
      throw err
    }
    return res
  } catch (error: any) {
    if (error.isAuthError) throw error
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      const err = new Error(error.response?.data?.message || '认证已过期') as any
      err.isAuthError = true
      throw err
    }
    if (error.response) {
      const message = error.response?.data?.message || '网络错误'
      ElMessage.error(message)
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时')
    }
    throw error
  }
}

const http = {
  get: <T = any>(url: string, config?: RequestConfig) => request<T>('get', url, undefined, config),
  post: <T = any>(url: string, data?: any, config?: RequestConfig) => request<T>('post', url, data, config),
  put: <T = any>(url: string, data?: any, config?: RequestConfig) => request<T>('put', url, data, config),
  delete: <T = any>(url: string, data?: any, config?: RequestConfig) => request<T>('delete', url, data, config),
  rawGet: (url: string, config?: RequestConfig & { responseType?: 'blob' }) => instance.get(url, {
    params: config?.params,
    headers: config?.headers,
    responseType: config?.responseType
  }),
  rawPost: (url: string, data?: any, config?: RequestConfig & { responseType?: 'blob' }) => instance.post(url, data, {
    params: config?.params,
    headers: config?.headers,
    responseType: config?.responseType
  })
}

export default http