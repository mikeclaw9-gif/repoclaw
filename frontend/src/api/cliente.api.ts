import { api } from '../boot/axios'
import type { PageResponse, ListarParams } from './types'

export interface ClienteRequest {
  nombre: string
  apellido: string
  email: string
  telefono?: string
  direccion?: string
  documento?: string
}

export interface ClienteResponse {
  id: number
  nombre: string
  apellido: string
  email: string
  telefono: string
  direccion: string
  documento: string
  activo: boolean
  createdAt: string
  updatedAt: string
}

export function listarClientes(params?: ListarParams & {
  texto?: string
  activo?: boolean
}) {
  return api.get<PageResponse<ClienteResponse>>('/clientes', { params })
}

export function obtenerCliente(id: number) {
  return api.get<ClienteResponse>(`/clientes/${id}`)
}

export function buscarPorEmail(email: string) {
  return api.get<ClienteResponse>(`/clientes/email/${email}`)
}

export function crearCliente(data: ClienteRequest) {
  return api.post<ClienteResponse>('/clientes', data)
}

export function actualizarCliente(id: number, data: Partial<ClienteRequest>) {
  return api.put<ClienteResponse>(`/clientes/${id}`, data)
}

export function eliminarCliente(id: number) {
  return api.delete(`/clientes/${id}`)
}

export function toggleActivo(id: number) {
  return api.patch<ClienteResponse>(`/clientes/${id}/toggle-activo`)
}
