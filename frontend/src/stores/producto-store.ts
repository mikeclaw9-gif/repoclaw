import { defineStore } from 'pinia'
import {
  listarProductos,
  crearProducto,
  actualizarProducto,
  eliminarProducto,
  toggleActivo,
  subirImagen,
  type ProductoRequest,
  type ProductoResponse,
} from '../api/producto.api'
import type { ListarParams } from '../api/types'

interface ProductoState {
  productos: ProductoResponse[]
  loading: boolean
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export const useProductoStore = defineStore('producto', {
  state: (): ProductoState => ({
    productos: [],
    loading: false,
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  }),

  actions: {
    async listar(params?: ListarParams) {
      this.loading = true
      try {
        const res = await listarProductos(params)
        this.productos = res.data.content
        this.page = res.data.number
        this.size = res.data.size
        this.totalElements = res.data.totalElements
        this.totalPages = res.data.totalPages
      } finally {
        this.loading = false
      }
    },

    async crear(data: ProductoRequest) {
      await crearProducto(data)
      await this.listar({ page: this.page, size: this.size })
    },

    async actualizar(id: number, data: Partial<ProductoRequest>) {
      await actualizarProducto(id, data)
      await this.listar({ page: this.page, size: this.size })
    },

    async eliminar(id: number) {
      await eliminarProducto(id)
      await this.listar({ page: this.page, size: this.size })
    },

    async toggleActivo(id: number) {
      await toggleActivo(id)
      await this.listar({ page: this.page, size: this.size })
    },
  },
})
