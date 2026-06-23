<template>
  <div class="q-pa-md">
    <div class="text-h4 q-mb-md">Dashboard</div>
    <div class="row q-gutter-md">
      <q-card class="cursor-pointer" v-ripple style="width: 250px" @click="$router.push('/inventario')">
        <q-card-section class="text-center q-pa-lg">
          <q-icon name="inventory_2" size="48px" color="primary" />
          <div class="text-h6 q-mt-md">Inventario</div>
          <div class="text-caption text-grey">Gestiona los productos del sistema</div>
        </q-card-section>
      </q-card>

      <q-card class="cursor-pointer" v-ripple style="width: 250px" @click="$router.push('/usuarios')">
        <q-card-section class="text-center q-pa-lg">
          <q-icon name="people" size="48px" color="primary" />
          <div class="text-h6 q-mt-md">Usuarios</div>
          <div class="text-caption text-grey">Administra los usuarios del sistema</div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'DashboardPage',
}
</script>
