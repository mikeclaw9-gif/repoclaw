import { api } from '../boot/axios'
import type { LoginRequest, LoginResponse } from './types'

export function login(data: LoginRequest) {
  return api.post<LoginResponse>('/auth/login', data)
}
