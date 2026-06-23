<template>
  <div class="fullscreen flex flex-center bg-grey-2">
    <q-card style="width: 400px; max-width: 90vw">
      <q-card-section class="text-center q-pa-lg">
        <q-icon name="point_of_sale" size="64px" color="primary" />
        <div class="text-h5 q-mt-md">Iniciar sesión</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit="handleLogin" class="q-gutter-md">
          <q-input
            v-model="email"
            label="Email"
            type="email"
            :rules="[requiredRule]"
            outlined
            lazy-rules
          >
            <template v-slot:prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <q-input
            v-model="password"
            label="Contraseña"
            :type="showPassword ? 'text' : 'password'"
            :rules="[requiredRule]"
            outlined
            lazy-rules
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
            <template v-slot:append>
              <q-icon
                :name="showPassword ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="showPassword = !showPassword"
              />
            </template>
          </q-input>

          <q-btn
            type="submit"
            label="Iniciar sesión"
            color="primary"
            class="full-width"
            :loading="loading"
          />
        </q-form>
      </q-card-section>
    </q-card>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth-store'
import { Notify } from 'quasar'

export default {
  name: 'LoginPage',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const email = ref('')
    const password = ref('')
    const showPassword = ref(false)
    const loading = ref(false)

    function requiredRule(val: string) {
      return !!val || 'Campo requerido'
    }

    async function handleLogin() {
      loading.value = true
      try {
        await authStore.login({ email: email.value, password: password.value })
        router.push('/dashboard')
      } catch (err: any) {
        const message = err.response?.data?.message || 'Error al iniciar sesión'
        Notify.create({ type: 'negative', message })
      } finally {
        loading.value = false
      }
    }

    return {
      email,
      password,
      showPassword,
      loading,
      requiredRule,
      handleLogin,
    }
  },
}
</script>
