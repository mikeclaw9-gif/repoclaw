import { api } from '../boot/axios'
import type { PageResponse, ListarParams } from './types'

export type RolUsuario = 'ROLE_ADMIN' | 'ROLE_USER' | 'ROLE_VENDEDOR'

export interface UsuarioRequest {
  nombre: string
  apellido: string
  email: string
  password?: string
  rol: RolUsuario
  telefono?: string
}

export interface UsuarioResponse {
  id: number
  nombre: string
  apellido: string
  email: string
  rol: RolUsuario
  telefono: string
  activo: boolean
  createdAt: string
  updatedAt: string
}

export function listarUsuarios(params?: ListarParams & {
  texto?: string
  rol?: RolUsuario
  activo?: boolean
}) {
  return api.get<PageResponse<UsuarioResponse>>('/usuarios', { params })
}

export function obtenerUsuario(id: number) {
  return api.get<UsuarioResponse>(`/usuarios/${id}`)
}

export function buscarPorEmail(email: string) {
  return api.get<UsuarioResponse>(`/usuarios/email/${email}`)
}

export function crearUsuario(data: UsuarioRequest) {
  return api.post<UsuarioResponse>('/usuarios', data)
}

export function actualizarUsuario(id: number, data: Partial<UsuarioRequest>) {
  return api.put<UsuarioResponse>(`/usuarios/${id}`, data)
}

export function eliminarUsuario(id: number) {
  return api.delete(`/usuarios/${id}`)
}

export function toggleActivo(id: number) {
  return api.patch<UsuarioResponse>(`/usuarios/${id}/toggle-activo`)
}
