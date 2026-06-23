import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/LoginPage.vue'),
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: { name: 'Dashboard' } },
      { path: 'dashboard', name: 'Dashboard', component: () => import('../pages/DashboardPage.vue') },
      { path: 'inventario', name: 'Inventario', component: () => import('../pages/InventarioPage.vue') },
      { path: 'usuarios', name: 'Usuarios', component: () => import('../pages/UsuariosPage.vue') },
      { path: 'clientes', name: 'Clientes', component: () => import('../pages/ClientesPage.vue') },
    ],
  },
]

export default routes
