import { defineStore } from 'pinia'
import {
  listarClientes,
  crearCliente,
  actualizarCliente,
  eliminarCliente,
  toggleActivo,
  type ClienteRequest,
  type ClienteResponse,
} from '../api/cliente.api'
import type { ListarParams } from '../api/types'

interface ClienteState {
  clientes: ClienteResponse[]
  loading: boolean
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export const useClienteStore = defineStore('cliente', {
  state: (): ClienteState => ({
    clientes: [],
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
        const res = await listarClientes(params)
        this.clientes = res.data.content
        this.page = res.data.number
        this.size = res.data.size
        this.totalElements = res.data.totalElements
        this.totalPages = res.data.totalPages
      } finally {
        this.loading = false
      }
    },

    async crear(data: ClienteRequest) {
      await crearCliente(data)
      await this.listar({ page: this.page, size: this.size })
    },

    async actualizar(id: number, data: Partial<ClienteRequest>) {
      await actualizarCliente(id, data)
      await this.listar({ page: this.page, size: this.size })
    },

    async eliminar(id: number) {
      await eliminarCliente(id)
      await this.listar({ page: this.page, size: this.size })
    },

    async toggleActivo(id: number) {
      await toggleActivo(id)
      await this.listar({ page: this.page, size: this.size })
    },
  },
})
