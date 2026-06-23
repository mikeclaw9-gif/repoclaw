import { defineStore } from 'pinia'
import { LocalStorage } from 'quasar'
import { login as loginApi } from '../api/auth.api'
import type { LoginRequest } from '../api/types'

const TOKEN_KEY = 'token'
const EMAIL_KEY = 'email'
const NOMBRE_KEY = 'nombre'
const APELLIDO_KEY = 'apellido'
const ROL_KEY = 'rol'

interface AuthState {
  token: string | null
  email: string | null
  nombre: string | null
  apellido: string | null
  rol: string | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: LocalStorage.getItem(TOKEN_KEY) as string | null,
    email: LocalStorage.getItem(EMAIL_KEY) as string | null,
    nombre: LocalStorage.getItem(NOMBRE_KEY) as string | null,
    apellido: LocalStorage.getItem(APELLIDO_KEY) as string | null,
    rol: LocalStorage.getItem(ROL_KEY) as string | null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    nombreCompleto: (state) => `${state.nombre ?? ''} ${state.apellido ?? ''}`.trim(),
  },

  actions: {
    async login(credentials: LoginRequest) {
      const res = await loginApi(credentials)
      const data = res.data
      this.token = data.token
      this.email = data.email
      this.nombre = data.nombre
      this.apellido = data.apellido
      this.rol = data.rol
      LocalStorage.set(TOKEN_KEY, data.token)
      LocalStorage.set(EMAIL_KEY, data.email)
      LocalStorage.set(NOMBRE_KEY, data.nombre)
      LocalStorage.set(APELLIDO_KEY, data.apellido)
      LocalStorage.set(ROL_KEY, data.rol)
    },

    logout() {
      this.token = null
      this.email = null
      this.nombre = null
      this.apellido = null
      this.rol = null
      LocalStorage.remove(TOKEN_KEY)
      LocalStorage.remove(EMAIL_KEY)
      LocalStorage.remove(NOMBRE_KEY)
      LocalStorage.remove(APELLIDO_KEY)
      LocalStorage.remove(ROL_KEY)
    },
  },
})
