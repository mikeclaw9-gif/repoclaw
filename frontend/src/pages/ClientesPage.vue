<template>
  <div class="q-pa-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4">Clientes</div>
      <div class="row q-gutter-sm">
        <q-btn label="Imprimir" color="secondary" icon="print" @click="imprimir" />
        <q-btn label="Nuevo cliente" color="primary" icon="add" @click="abrirDialog()" />
      </div>
    </div>

    <q-expansion-item label="Filtros" icon="filter_list" class="q-mb-md">
      <div class="row q-gutter-sm q-pa-sm">
        <q-input v-model="filtros.texto" label="Buscar" debounce="300" style="min-width: 200px" @update:model-value="buscar" />
        <q-select v-model="filtros.estado" :options="estados" label="Estado" clearable style="width: 130px" @update:model-value="buscar" />
        <q-btn label="Limpiar filtros" flat @click="limpiarFiltros" />
      </div>
    </q-expansion-item>

    <q-table
      :rows="clienteStore.clientes"
      :columns="columnas"
      row-key="id"
      :loading="clienteStore.loading"
      :pagination="pagination"
      @request="onRequest"
      binary-state-sort
    >
      <template v-slot:body-cell-nombreCompleto="props">
        <q-td>{{ props.row.nombre }} {{ props.row.apellido }}</q-td>
      </template>

      <template v-slot:body-cell-activo="props">
        <q-td>
          <q-chip :color="props.row.activo ? 'green' : 'red'" text-color="white" dense>
            {{ props.row.activo ? 'Activo' : 'Inactivo' }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-acciones="props">
        <q-td>
          <q-btn dense flat icon="edit" color="primary" @click="abrirDialog(props.row)" />
          <q-btn dense flat icon="toggle_on" :color="props.row.activo ? 'negative' : 'positive'" @click="toggleActivo(props.row.id)" />
          <q-btn dense flat icon="delete" color="negative" @click="eliminar(props.row.id)" />
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="dialog" persistent>
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">{{ editando ? 'Editar cliente' : 'Nuevo cliente' }}</div>
        </q-card-section>

        <q-card-section>
          <q-form @submit="guardar" class="q-gutter-md">
            <q-input v-model="form.nombre" label="Nombre" :rules="[requiredRule]" outlined lazy-rules />
            <q-input v-model="form.apellido" label="Apellido" :rules="[requiredRule]" outlined lazy-rules />
            <q-input v-model="form.email" label="Email" type="email" :rules="[requiredRule, emailRule]" outlined lazy-rules />
            <q-input v-model="form.telefono" label="Teléfono" outlined />
            <q-input v-model="form.direccion" label="Dirección" outlined />
            <q-input v-model="form.documento" label="Documento (DNI/RUC)" outlined />

            <div class="row justify-end q-gutter-sm">
              <q-btn label="Cancelar" v-close-popup />
              <q-btn label="Guardar" type="submit" color="primary" />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
import { useClienteStore } from '../stores/cliente-store'
import { Notify } from 'quasar'
import { generarPdf } from '../utils/pdf'
import type { ClienteResponse } from '../api/cliente.api'

interface ClienteForm {
  nombre: string
  apellido: string
  email: string
  telefono: string
  direccion: string
  documento: string
}

const initialForm = (): ClienteForm => ({
  nombre: '',
  apellido: '',
  email: '',
  telefono: '',
  direccion: '',
  documento: '',
})

export default {
  name: 'ClientesPage',
  setup() {
    const clienteStore = useClienteStore()
    const dialog = ref(false)
    const editando = ref(false)
    const editingId = ref<number | null>(null)
    const form = ref<ClienteForm>(initialForm())

    const filtros = ref({
      texto: '',
      estado: null as string | null,
    })

    const estados = ['Activo', 'Inactivo']

    const columnas = [
      { name: 'nombreCompleto', label: 'Nombre completo', field: 'nombre', align: 'left', sortable: true },
      { name: 'email', label: 'Email', field: 'email', align: 'left', sortable: true },
      { name: 'telefono', label: 'Teléfono', field: 'telefono', align: 'left', sortable: false },
      { name: 'direccion', label: 'Dirección', field: 'direccion', align: 'left', sortable: false },
      { name: 'documento', label: 'Documento', field: 'documento', align: 'left', sortable: false },
      { name: 'activo', label: 'Estado', field: 'activo', align: 'center', sortable: false },
      { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center', sortable: false },
    ]

    const pagination = ref({
      sortBy: 'id',
      descending: false,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
    })

    onMounted(() => {
      clienteStore.listar({ page: 0, size: pagination.value.rowsPerPage })
    })

    function onRequest(props: any) {
      const { page, rowsPerPage, sortBy, descending } = props.pagination
      pagination.value.page = page
      pagination.value.rowsPerPage = rowsPerPage
      pagination.value.sortBy = sortBy
      pagination.value.descending = descending

      clienteStore.listar({
        page: page - 1,
        size: rowsPerPage,
        sortBy: sortBy || undefined,
        sortDir: descending ? 'desc' : 'asc',
        texto: filtros.value.texto || undefined,
        activo: filtros.value.estado === 'Activo' ? true : filtros.value.estado === 'Inactivo' ? false : undefined,
      })
    }

    function buscar() {
      pagination.value.page = 1
      clienteStore.listar({
        page: 0,
        size: pagination.value.rowsPerPage,
        texto: filtros.value.texto || undefined,
        activo: filtros.value.estado === 'Activo' ? true : filtros.value.estado === 'Inactivo' ? false : undefined,
      })
    }

    function limpiarFiltros() {
      filtros.value = { texto: '', estado: null }
      buscar()
    }

    function requiredRule(val: any) {
      return !!val || 'Campo requerido'
    }

    function emailRule(val: string) {
      return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Email inválido'
    }

    function abrirDialog(cliente?: ClienteResponse) {
      if (cliente) {
        editando.value = true
        editingId.value = cliente.id
        form.value = {
          nombre: cliente.nombre,
          apellido: cliente.apellido,
          email: cliente.email,
          telefono: cliente.telefono,
          direccion: cliente.direccion,
          documento: cliente.documento,
        }
      } else {
        editando.value = false
        editingId.value = null
        form.value = initialForm()
      }
      dialog.value = true
    }

    async function guardar() {
      try {
        if (editando.value && editingId.value) {
          await clienteStore.actualizar(editingId.value, form.value)
          Notify.create({ type: 'positive', message: 'Cliente actualizado' })
        } else {
          await clienteStore.crear(form.value)
          Notify.create({ type: 'positive', message: 'Cliente creado' })
        }
        dialog.value = false
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al guardar' })
      }
    }

    async function toggleActivo(id: number) {
      try {
        await clienteStore.toggleActivo(id)
        Notify.create({ type: 'positive', message: 'Estado actualizado' })
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al cambiar estado' })
      }
    }

    async function eliminar(id: number) {
      try {
        await clienteStore.eliminar(id)
        Notify.create({ type: 'positive', message: 'Cliente eliminado' })
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al eliminar' })
      }
    }

    function imprimir() {
      const filtrosActivos: Record<string, string> = {}
      if (filtros.value.texto) filtrosActivos['Texto'] = filtros.value.texto
      if (filtros.value.estado) filtrosActivos['Estado'] = filtros.value.estado

      const datos = clienteStore.clientes.map((c) => ({
        ...c,
        nombreCompleto: `${c.nombre} ${c.apellido}`,
      }))

      generarPdf(
        'Clientes',
        [
          { label: 'Nombre completo', dataKey: 'nombreCompleto' },
          { label: 'Email', dataKey: 'email' },
          { label: 'Teléfono', dataKey: 'telefono' },
          { label: 'Dirección', dataKey: 'direccion' },
          { label: 'Documento', dataKey: 'documento' },
          { label: 'Estado', dataKey: 'activo' },
        ],
        datos,
        'clientes.pdf',
        Object.keys(filtrosActivos).length > 0 ? filtrosActivos : undefined
      )
    }

    return {
      clienteStore,
      dialog,
      editando,
      form,
      filtros,
      estados,
      columnas,
      pagination,
      onRequest,
      buscar,
      limpiarFiltros,
      requiredRule,
      emailRule,
      abrirDialog,
      guardar,
      toggleActivo,
      eliminar,
      imprimir,
    }
  },
}
</script>
