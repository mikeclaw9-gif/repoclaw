import { api } from '../boot/axios'
import type { PageResponse, ListarParams } from './types'

export interface ProductoRequest {
  codigo: string
  nombre: string
  descripcion?: string
  precioCompra: number
  precioVenta: number
  existencia: number
  imagen?: string
}

export interface ProductoResponse {
  id: number
  codigo: string
  nombre: string
  descripcion: string
  precioCompra: number
  precioVenta: number
  existencia: number
  imagen: string
  activo: boolean
  createdAt: string
  updatedAt: string
}

export function listarProductos(params?: ListarParams & {
  texto?: string
  precioCompraMin?: number
  precioCompraMax?: number
  precioVentaMin?: number
  precioVentaMax?: number
  existenciaMin?: number
  existenciaMax?: number
  activo?: boolean
}) {
  return api.get<PageResponse<ProductoResponse>>('/productos', { params })
}

export function obtenerProducto(id: number) {
  return api.get<ProductoResponse>(`/productos/${id}`)
}

export function buscarPorCodigo(codigo: string) {
  return api.get<ProductoResponse>(`/productos/codigo/${codigo}`)
}

export function crearProducto(data: ProductoRequest) {
  return api.post<ProductoResponse>('/productos', data)
}

export function actualizarProducto(id: number, data: Partial<ProductoRequest>) {
  return api.put<ProductoResponse>(`/productos/${id}`, data)
}

export function eliminarProducto(id: number) {
  return api.delete(`/productos/${id}`)
}

export function toggleActivo(id: number) {
  return api.patch<ProductoResponse>(`/productos/${id}/toggle-activo`)
}

export function subirImagen(file: File, nombreProducto?: string) {
  const formData = new FormData()
  formData.append('file', file)
  if (nombreProducto) {
    formData.append('nombreProducto', nombreProducto)
  }
  return api.post<{ ruta: string; nombre: string }>('/productos/imagen', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
