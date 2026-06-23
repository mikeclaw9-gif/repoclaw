<template>
  <q-layout view="hHh Lpr lFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="leftDrawerOpen = !leftDrawerOpen" />
        <q-toolbar-title>Pventafront</q-toolbar-title>
        <q-space />
        <q-btn flat no-caps>
          <q-item-label>{{ authStore.nombreCompleto }}</q-item-label>
        </q-btn>
        <q-btn dense flat round icon="logout" @click="logout" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" bordered>
      <q-list>
        <q-item clickable v-ripple to="/dashboard">
          <q-item-section avatar>
            <q-icon name="dashboard" />
          </q-item-section>
          <q-item-section>Dashboard</q-item-section>
        </q-item>
        <q-item clickable v-ripple to="/inventario">
          <q-item-section avatar>
            <q-icon name="inventory_2" />
          </q-item-section>
          <q-item-section>Inventario</q-item-section>
        </q-item>
        <q-item clickable v-ripple to="/clientes">
          <q-item-section avatar>
            <q-icon name="people" />
          </q-item-section>
          <q-item-section>Clientes</q-item-section>
        </q-item>
        <q-item clickable v-ripple to="/usuarios">
          <q-item-section avatar>
            <q-icon name="manage_accounts" />
          </q-item-section>
          <q-item-section>Usuarios</q-item-section>
        </q-item>

        <q-separator />

        <q-item>
          <q-item-section avatar>
            <q-icon name="dark_mode" />
          </q-item-section>
          <q-item-section>
            <q-toggle v-model="darkMode" label="Tema oscuro" @update:model-value="toggleDark" />
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth-store'
import { useQuasar, LocalStorage } from 'quasar'

export default {
  name: 'MainLayout',
  setup() {
    const $q = useQuasar()
    const router = useRouter()
    const authStore = useAuthStore()
    const leftDrawerOpen = ref(false)
    const darkMode = ref(false)

    onMounted(() => {
      const stored = LocalStorage.getItem('darkMode')
      if (stored === true) {
        darkMode.value = true
        $q.dark.set(true)
      }
    })

    function toggleDark(val: boolean) {
      darkMode.value = val
      $q.dark.set(val)
      LocalStorage.set('darkMode', val)
    }

    function logout() {
      authStore.logout()
      router.push('/login')
    }

    return {
      leftDrawerOpen,
      darkMode,
      toggleDark,
      logout,
      authStore,
    }
  },
}
</script>
