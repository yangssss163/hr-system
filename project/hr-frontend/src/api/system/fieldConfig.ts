import http from '../http'
import type { Field } from '../types'

export const getFieldConfigs = (module?: string) => {
  return http.get<Field[]>('/field-configs', { params: { module } })
}

export const saveFieldConfigs = (data: Field[]) => {
  return http.put('/field-configs', data)
}
