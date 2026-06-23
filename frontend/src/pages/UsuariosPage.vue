<template>
  <div class="q-pa-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4">Usuarios</div>
      <div class="row q-gutter-sm">
        <q-btn label="Imprimir" color="secondary" icon="print" @click="imprimir" />
        <q-btn label="Nuevo usuario" color="primary" icon="add" @click="abrirDialog()" />
      </div>
    </div>

    <q-expansion-item label="Filtros" icon="filter_list" class="q-mb-md">
      <div class="row q-gutter-sm q-pa-sm">
        <q-input v-model="filtros.texto" label="Buscar" debounce="300" style="min-width: 200px" @update:model-value="buscar" />
        <q-select v-model="filtros.rol" :options="roles" label="Rol" clearable style="width: 180px" @update:model-value="buscar" />
        <q-select v-model="filtros.estado" :options="estados" label="Estado" clearable style="width: 130px" @update:model-value="buscar" />
        <q-btn label="Limpiar filtros" flat @click="limpiarFiltros" />
      </div>
    </q-expansion-item>

    <q-table
      :rows="usuarioStore.usuarios"
      :columns="columnas"
      row-key="id"
      :loading="usuarioStore.loading"
      :pagination="pagination"
      @request="onRequest"
      binary-state-sort
    >
      <template v-slot:body-cell-nombreCompleto="props">
        <q-td>{{ props.row.nombre }} {{ props.row.apellido }}</q-td>
      </template>

      <template v-slot:body-cell-rol="props">
        <q-td>
          <q-chip :color="chipColor(props.row.rol)" text-color="white" dense>
            {{ labelRol(props.row.rol) }}
          </q-chip>
        </q-td>
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
      <q-card style="min-width: 450px">
        <q-card-section>
          <div class="text-h6">{{ editando ? 'Editar usuario' : 'Nuevo usuario' }}</div>
        </q-card-section>

        <q-card-section>
          <q-form @submit="guardar" class="q-gutter-md">
            <q-input v-model="form.nombre" label="Nombre" :rules="[requiredRule]" outlined lazy-rules />
            <q-input v-model="form.apellido" label="Apellido" :rules="[requiredRule]" outlined lazy-rules />
            <q-input v-model="form.email" label="Email" type="email" :rules="[requiredRule, emailRule]" outlined lazy-rules />
            <q-input
              v-model="form.password"
              label="Contraseña"
              :type="showPassword ? 'text' : 'password'"
              :rules="editando ? [] : [requiredRule]"
              outlined
              lazy-rules
            >
              <template v-slot:append>
                <q-icon
                  :name="showPassword ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="showPassword = !showPassword"
                />
              </template>
            </q-input>
            <q-badge v-if="editando" color="info" label="Dejar vacío para no cambiar" />
            <q-select v-model="form.rol" :options="roles" label="Rol" :rules="[requiredRule]" outlined lazy-rules />
            <q-input v-model="form.telefono" label="Teléfono" outlined />

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
import { useUsuarioStore } from '../stores/usuario-store'
import { Notify } from 'quasar'
import { generarPdf } from '../utils/pdf'
import type { UsuarioResponse, RolUsuario } from '../api/usuario.api'

interface UsuarioForm {
  nombre: string
  apellido: string
  email: string
  password: string
  rol: RolUsuario | null
  telefono: string
}

const initialForm = (): UsuarioForm => ({
  nombre: '',
  apellido: '',
  email: '',
  password: '',
  rol: null,
  telefono: '',
})

export default {
  name: 'UsuariosPage',
  setup() {
    const usuarioStore = useUsuarioStore()
    const dialog = ref(false)
    const editando = ref(false)
    const editingId = ref<number | null>(null)
    const form = ref<UsuarioForm>(initialForm())
    const showPassword = ref(false)

    const filtros = ref({
      texto: '',
      rol: null as string | null,
      estado: null as string | null,
    })

    const roles = ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_VENDEDOR']
    const estados = ['Activo', 'Inactivo']

    const columnas = [
      { name: 'nombreCompleto', label: 'Nombre completo', field: 'nombre', align: 'left', sortable: true },
      { name: 'email', label: 'Email', field: 'email', align: 'left', sortable: true },
      { name: 'rol', label: 'Rol', field: 'rol', align: 'center', sortable: true },
      { name: 'telefono', label: 'Teléfono', field: 'telefono', align: 'left', sortable: false },
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
      usuarioStore.listar({ page: 0, size: pagination.value.rowsPerPage })
    })

    function chipColor(rol: string) {
      const map: Record<string, string> = {
        ROLE_ADMIN: 'purple',
        ROLE_USER: 'blue',
        ROLE_VENDEDOR: 'orange',
      }
      return map[rol] || 'grey'
    }

    function labelRol(rol: string) {
      const map: Record<string, string> = {
        ROLE_ADMIN: 'Admin',
        ROLE_USER: 'Usuario',
        ROLE_VENDEDOR: 'Vendedor',
      }
      return map[rol] || rol
    }

    function onRequest(props: any) {
      const { page, rowsPerPage, sortBy, descending } = props.pagination
      pagination.value.page = page
      pagination.value.rowsPerPage = rowsPerPage
      pagination.value.sortBy = sortBy
      pagination.value.descending = descending

      usuarioStore.listar({
        page: page - 1,
        size: rowsPerPage,
        sortBy: sortBy || undefined,
        sortDir: descending ? 'desc' : 'asc',
        texto: filtros.value.texto || undefined,
        rol: (filtros.value.rol as RolUsuario) || undefined,
        activo: filtros.value.estado === 'Activo' ? true : filtros.value.estado === 'Inactivo' ? false : undefined,
      })
    }

    function buscar() {
      pagination.value.page = 1
      usuarioStore.listar({
        page: 0,
        size: pagination.value.rowsPerPage,
        texto: filtros.value.texto || undefined,
        rol: (filtros.value.rol as RolUsuario) || undefined,
        activo: filtros.value.estado === 'Activo' ? true : filtros.value.estado === 'Inactivo' ? false : undefined,
      })
    }

    function limpiarFiltros() {
      filtros.value = { texto: '', rol: null, estado: null }
      buscar()
    }

    function requiredRule(val: any) {
      return !!val || 'Campo requerido'
    }

    function emailRule(val: string) {
      return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Email inválido'
    }

    function abrirDialog(usuario?: UsuarioResponse) {
      if (usuario) {
        editando.value = true
        editingId.value = usuario.id
        form.value = {
          nombre: usuario.nombre,
          apellido: usuario.apellido,
          email: usuario.email,
          password: '',
          rol: usuario.rol,
          telefono: usuario.telefono,
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
        const payload: any = {
          nombre: form.value.nombre,
          apellido: form.value.apellido,
          email: form.value.email,
          rol: form.value.rol,
          telefono: form.value.telefono || undefined,
        }
        if (!editando.value || form.value.password) {
          payload.password = form.value.password
        }
        if (editando.value && editingId.value) {
          await usuarioStore.actualizar(editingId.value, payload)
          Notify.create({ type: 'positive', message: 'Usuario actualizado' })
        } else {
          await usuarioStore.crear(payload)
          Notify.create({ type: 'positive', message: 'Usuario creado' })
        }
        dialog.value = false
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al guardar' })
      }
    }

    async function toggleActivo(id: number) {
      try {
        await usuarioStore.toggleActivo(id)
        Notify.create({ type: 'positive', message: 'Estado actualizado' })
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al cambiar estado' })
      }
    }

    async function eliminar(id: number) {
      try {
        await usuarioStore.eliminar(id)
        Notify.create({ type: 'positive', message: 'Usuario eliminado' })
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al eliminar' })
      }
    }

    function imprimir() {
      const filtrosActivos: Record<string, string> = {}
      if (filtros.value.texto) filtrosActivos['Texto'] = filtros.value.texto
      if (filtros.value.rol) filtrosActivos['Rol'] = labelRol(filtros.value.rol)
      if (filtros.value.estado) filtrosActivos['Estado'] = filtros.value.estado

      const datos = usuarioStore.usuarios.map((u) => ({
        ...u,
        nombreCompleto: `${u.nombre} ${u.apellido}`,
      }))

      generarPdf(
        'Usuarios',
        [
          { label: 'Nombre completo', dataKey: 'nombreCompleto' },
          { label: 'Email', dataKey: 'email' },
          { label: 'Rol', dataKey: 'rol' },
          { label: 'Teléfono', dataKey: 'telefono' },
          { label: 'Estado', dataKey: 'activo' },
        ],
        datos,
        'usuarios.pdf',
        Object.keys(filtrosActivos).length > 0 ? filtrosActivos : undefined
      )
    }

    return {
      usuarioStore,
      dialog,
      editando,
      form,
      showPassword,
      filtros,
      roles,
      estados,
      columnas,
      pagination,
      chipColor,
      labelRol,
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
