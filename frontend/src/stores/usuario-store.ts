import { defineStore } from 'pinia'
import {
  listarUsuarios,
  crearUsuario,
  actualizarUsuario,
  eliminarUsuario,
  toggleActivo,
  type UsuarioRequest,
  type UsuarioResponse,
} from '../api/usuario.api'
import type { ListarParams } from '../api/types'

interface UsuarioState {
  usuarios: UsuarioResponse[]
  loading: boolean
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export const useUsuarioStore = defineStore('usuario', {
  state: (): UsuarioState => ({
    usuarios: [],
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
        const res = await listarUsuarios(params)
        this.usuarios = res.data.content
        this.page = res.data.number
        this.size = res.data.size
        this.totalElements = res.data.totalElements
        this.totalPages = res.data.totalPages
      } finally {
        this.loading = false
      }
    },

    async crear(data: UsuarioRequest) {
      await crearUsuario(data)
      await this.listar({ page: this.page, size: this.size })
    },

    async actualizar(id: number, data: Partial<UsuarioRequest>) {
      await actualizarUsuario(id, data)
      await this.listar({ page: this.page, size: this.size })
    },

    async eliminar(id: number) {
      await eliminarUsuario(id)
      await this.listar({ page: this.page, size: this.size })
    },

    async toggleActivo(id: number) {
      await toggleActivo(id)
      await this.listar({ page: this.page, size: this.size })
    },
  },
})
