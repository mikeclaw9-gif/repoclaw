import { boot } from 'quasar/wrappers'
import axios, { type AxiosInstance } from 'axios'
import { LocalStorage } from 'quasar'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $axios: AxiosInstance
    $api: AxiosInstance
  }
}

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
  const token = LocalStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      LocalStorage.remove('token')
      LocalStorage.remove('email')
      LocalStorage.remove('nombre')
      LocalStorage.remove('apellido')
      LocalStorage.remove('rol')
      window.location.href = '/#/login'
    }
    return Promise.reject(error)
  }
)

export default boot(({ app }) => {
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api
})

export { api }
