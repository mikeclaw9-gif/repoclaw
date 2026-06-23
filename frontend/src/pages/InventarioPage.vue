<template>
  <div class="q-pa-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4">Inventario</div>
      <div class="row q-gutter-sm">
        <q-btn label="Imprimir" color="secondary" icon="print" @click="imprimir" />
        <q-btn label="Nuevo producto" color="primary" icon="add" @click="abrirDialog()" />
      </div>
    </div>

    <q-expansion-item label="Filtros" icon="filter_list" class="q-mb-md">
      <div class="row q-gutter-sm q-pa-sm">
        <q-input v-model="filtros.texto" label="Buscar" debounce="300" style="min-width: 200px" @update:model-value="buscar" />
        <q-input v-model.number="filtros.precioCompraMin" label="Compra min" type="number" style="width: 120px" @update:model-value="buscar" />
        <q-input v-model.number="filtros.precioCompraMax" label="Compra max" type="number" style="width: 120px" @update:model-value="buscar" />
        <q-input v-model.number="filtros.precioVentaMin" label="Venta min" type="number" style="width: 120px" @update:model-value="buscar" />
        <q-input v-model.number="filtros.precioVentaMax" label="Venta max" type="number" style="width: 120px" @update:model-value="buscar" />
        <q-input v-model.number="filtros.existenciaMin" label="Stock min" type="number" style="width: 120px" @update:model-value="buscar" />
        <q-input v-model.number="filtros.existenciaMax" label="Stock max" type="number" style="width: 120px" @update:model-value="buscar" />
        <q-select v-model="filtros.estado" :options="estados" label="Estado" clearable style="width: 130px" @update:model-value="buscar" />
        <q-btn label="Limpiar filtros" flat @click="limpiarFiltros" />
      </div>
    </q-expansion-item>

    <q-table
      :rows="productoStore.productos"
      :columns="columnas"
      row-key="id"
      :loading="productoStore.loading"
      :pagination="pagination"
      @request="onRequest"
      binary-state-sort
    >
      <template v-slot:body-cell-imagen="props">
        <q-td>
          <q-img
            v-if="props.row.imagen"
            :src="`/api${props.row.imagen}`"
            style="width: 50px; height: 50px"
            fit="cover"
            class="rounded-borders"
          />
          <span v-else class="text-grey">—</span>
        </q-td>
      </template>

      <template v-slot:body-cell-descripcion="props">
        <q-td>
          <span v-if="!props.row._expandDesc && props.row.descripcion?.length > 50">
            {{ props.row.descripcion.slice(0, 50) }}...
            <q-btn dense flat icon="expand_more" size="sm" @click="props.row._expandDesc = true" />
          </span>
          <span v-else>
            {{ props.row.descripcion }}
            <q-btn v-if="props.row.descripcion?.length > 50" dense flat icon="expand_less" size="sm" @click="props.row._expandDesc = false" />
          </span>
        </q-td>
      </template>

      <template v-slot:body-cell-precioCompra="props">
        <q-td>$ {{ props.row.precioCompra.toFixed(2) }}</q-td>
      </template>

      <template v-slot:body-cell-precioVenta="props">
        <q-td>$ {{ props.row.precioVenta.toFixed(2) }}</q-td>
      </template>

      <template v-slot:body-cell-existencia="props">
        <q-td class="text-center">{{ props.row.existencia }}</q-td>
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
          <div class="text-h6">{{ editando ? 'Editar producto' : 'Nuevo producto' }}</div>
        </q-card-section>

        <q-card-section>
          <q-form @submit="guardar" class="q-gutter-md">
            <q-input v-model="form.codigo" label="Código" :rules="[requiredRule]" outlined lazy-rules />
            <q-input v-model="form.nombre" label="Nombre" :rules="[requiredRule]" outlined lazy-rules />
            <q-input v-model="form.descripcion" label="Descripción" type="textarea" outlined />
            <q-input v-model.number="form.precioCompra" label="Precio compra" type="number" :rules="[requiredRule, minZeroRule]" outlined lazy-rules />
            <q-input v-model.number="form.precioVenta" label="Precio venta" type="number" :rules="[requiredRule, minZeroRule]" outlined lazy-rules />
            <q-input v-model.number="form.existencia" label="Existencia" type="number" outlined />

            <div class="row items-center q-gutter-sm">
              <input ref="fileInputRef" type="file" accept="image/*" style="display:none" @change="onFileSelected" />
              <q-btn label="Seleccionar imagen" icon="image" outline @click="seleccionarImagen" />
              <q-btn label="Tomar foto" icon="camera_alt" outline @click="abrirCamara" />
              <q-btn v-if="form.imagen" label="Quitar imagen" flat dense color="negative" icon="delete" @click="quitarImagen" />
            </div>
            <div class="flex flex-center q-mt-sm">
              <div
                class="rounded-borders"
                style="width: 200px; height: 100px; border: 1px dashed #ccc; display: flex; align-items: center; justify-content: center; overflow: hidden;"
              >
                <q-img
                  v-if="imagenPreview"
                  :src="imagenPreview"
                  style="max-width: 200px; max-height: 100px"
                  fit="contain"
                />
                <span v-else class="text-grey-5 text-caption">Vista previa</span>
              </div>
            </div>

            <div class="row justify-end q-gutter-sm">
              <q-btn label="Cancelar" v-close-popup />
              <q-btn label="Guardar" type="submit" color="primary" />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <q-dialog v-model="cameraDialog" maximized>
      <q-card>
        <q-card-section class="text-h6">Tomar foto</q-card-section>
        <q-card-section class="flex flex-center">
          <video ref="videoRef" autoplay playsinline style="max-width: 100%; max-height: 70vh;" />
        </q-card-section>
        <q-card-actions align="center">
          <q-btn label="📷 Capturar" color="primary" size="lg" @click="capturarFoto" />
          <q-btn label="Cancelar" color="negative" v-close-popup @click="detenerCamara" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useProductoStore } from '../stores/producto-store'
import { Notify } from 'quasar'
import { generarPdf } from '../utils/pdf'
import { subirImagen } from '../api/producto.api'
import type { ProductoResponse } from '../api/producto.api'

interface ProductoForm {
  codigo: string
  nombre: string
  descripcion: string
  precioCompra: number | null
  precioVenta: number | null
  existencia: number | null
  imagen: string
}

const initialForm = (): ProductoForm => ({
  codigo: '',
  nombre: '',
  descripcion: '',
  precioCompra: null,
  precioVenta: null,
  existencia: null,
  imagen: '',
})

export default {
  name: 'InventarioPage',
  setup() {
    const productoStore = useProductoStore()
    const dialog = ref(false)
    const editando = ref(false)
    const editingId = ref<number | null>(null)
    const form = ref<ProductoForm>(initialForm())
    const filtrosTexto = ref('')
    const fileInputRef = ref<HTMLInputElement | null>(null)
    const videoRef = ref<HTMLVideoElement | null>(null)
    const cameraDialog = ref(false)
    const imagenPreview = ref('')
    let mediaStream: MediaStream | null = null

    const filtros = ref({
      texto: '',
      precioCompraMin: null as number | null,
      precioCompraMax: null as number | null,
      precioVentaMin: null as number | null,
      precioVentaMax: null as number | null,
      existenciaMin: null as number | null,
      existenciaMax: null as number | null,
      estado: null as string | null,
    })

    const estados = ['Activo', 'Inactivo']

    const columnas = [
      { name: 'codigo', label: 'Código', field: 'codigo', align: 'left', sortable: true },
      { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
      { name: 'imagen', label: 'Imagen', field: 'imagen', align: 'center', sortable: false },
      { name: 'descripcion', label: 'Descripción', field: 'descripcion', align: 'left', sortable: false },
      { name: 'precioCompra', label: 'Compra', field: 'precioCompra', align: 'right', sortable: true },
      { name: 'precioVenta', label: 'Venta', field: 'precioVenta', align: 'right', sortable: true },
      { name: 'existencia', label: 'Stock', field: 'existencia', align: 'center', sortable: true },
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
      productoStore.listar({ page: 0, size: pagination.value.rowsPerPage })
    })

    onUnmounted(() => {
      detenerCamara()
    })

    const rowsMapped = computed(() =>
      productoStore.productos.map((p) => ({ ...p, _expandDesc: false }))
    )

    function onRequest(props: any) {
      const { page, rowsPerPage, sortBy, descending } = props.pagination
      pagination.value.page = page
      pagination.value.rowsPerPage = rowsPerPage
      pagination.value.sortBy = sortBy
      pagination.value.descending = descending

      productoStore.listar({
        page: page - 1,
        size: rowsPerPage,
        sortBy: sortBy || undefined,
        sortDir: descending ? 'desc' : 'asc',
        texto: filtros.value.texto || undefined,
        precioCompraMin: filtros.value.precioCompraMin ?? undefined,
        precioCompraMax: filtros.value.precioCompraMax ?? undefined,
        precioVentaMin: filtros.value.precioVentaMin ?? undefined,
        precioVentaMax: filtros.value.precioVentaMax ?? undefined,
        existenciaMin: filtros.value.existenciaMin ?? undefined,
        existenciaMax: filtros.value.existenciaMax ?? undefined,
        activo: filtros.value.estado === 'Activo' ? true : filtros.value.estado === 'Inactivo' ? false : undefined,
      })
    }

    function buscar() {
      pagination.value.page = 1
      productoStore.listar({
        page: 0,
        size: pagination.value.rowsPerPage,
        texto: filtros.value.texto || undefined,
        precioCompraMin: filtros.value.precioCompraMin ?? undefined,
        precioCompraMax: filtros.value.precioCompraMax ?? undefined,
        precioVentaMin: filtros.value.precioVentaMin ?? undefined,
        precioVentaMax: filtros.value.precioVentaMax ?? undefined,
        existenciaMin: filtros.value.existenciaMin ?? undefined,
        existenciaMax: filtros.value.existenciaMax ?? undefined,
        activo: filtros.value.estado === 'Activo' ? true : filtros.value.estado === 'Inactivo' ? false : undefined,
      })
    }

    function limpiarFiltros() {
      filtros.value = {
        texto: '',
        precioCompraMin: null,
        precioCompraMax: null,
        precioVentaMin: null,
        precioVentaMax: null,
        existenciaMin: null,
        existenciaMax: null,
        estado: null,
      }
      buscar()
    }

    function requiredRule(val: any) {
      return !!val || 'Campo requerido'
    }

    function minZeroRule(val: number) {
      return val >= 0 || 'Debe ser mayor o igual a 0'
    }

    function seleccionarImagen() {
      fileInputRef.value?.click()
    }

    async function onFileSelected(event: Event) {
      const input = event.target as HTMLInputElement
      if (input.files && input.files[0]) {
        await uploadImage(input.files[0])
      }
      input.value = ''
    }

    async function abrirCamara() {
      try {
        mediaStream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' }, audio: false })
        cameraDialog.value = true
        setTimeout(() => {
          if (videoRef.value) {
            videoRef.value.srcObject = mediaStream
          }
        }, 100)
      } catch {
        Notify.create({ type: 'negative', message: 'No se pudo acceder a la cámara' })
      }
    }

    function capturarFoto() {
      const video = videoRef.value
      if (!video) return
      const canvas = document.createElement('canvas')
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      const ctx = canvas.getContext('2d')
      if (!ctx) return
      ctx.drawImage(video, 0, 0)
      canvas.toBlob(async (blob) => {
        if (blob) {
          const file = new File([blob], 'foto.jpg', { type: 'image/jpeg' })
          await uploadImage(file)
        }
        detenerCamara()
        cameraDialog.value = false
      }, 'image/jpeg')
    }

    function detenerCamara() {
      if (mediaStream) {
        mediaStream.getTracks().forEach((track) => track.stop())
        mediaStream = null
      }
      if (videoRef.value) {
        videoRef.value.srcObject = null
      }
    }

    async function uploadImage(file: File) {
      try {
        const res = await subirImagen(file, form.value.nombre || undefined)
        form.value.imagen = res.data.ruta
        imagenPreview.value = `/api${res.data.ruta}`
        Notify.create({ type: 'positive', message: 'Imagen subida correctamente' })
      } catch {
        Notify.create({ type: 'negative', message: 'Error al subir la imagen' })
      }
    }

    function quitarImagen() {
      form.value.imagen = ''
      imagenPreview.value = ''
    }

    function abrirDialog(producto?: ProductoResponse) {
      detenerCamara()
      if (producto) {
        editando.value = true
        editingId.value = producto.id
        form.value = {
          codigo: producto.codigo,
          nombre: producto.nombre,
          descripcion: producto.descripcion,
          precioCompra: producto.precioCompra,
          precioVenta: producto.precioVenta,
          existencia: producto.existencia,
          imagen: producto.imagen || '',
        }
        imagenPreview.value = producto.imagen ? `/api${producto.imagen}` : ''
      } else {
        editando.value = false
        editingId.value = null
        form.value = initialForm()
        imagenPreview.value = ''
      }
      dialog.value = true
    }

    async function guardar() {
      try {
        if (editando.value && editingId.value) {
          await productoStore.actualizar(editingId.value, form.value)
          Notify.create({ type: 'positive', message: 'Producto actualizado' })
        } else {
          await productoStore.crear(form.value as any)
          Notify.create({ type: 'positive', message: 'Producto creado' })
        }
        dialog.value = false
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al guardar' })
      }
    }

    async function toggleActivo(id: number) {
      try {
        await productoStore.toggleActivo(id)
        Notify.create({ type: 'positive', message: 'Estado actualizado' })
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al cambiar estado' })
      }
    }

    async function eliminar(id: number) {
      try {
        await productoStore.eliminar(id)
        Notify.create({ type: 'positive', message: 'Producto eliminado' })
      } catch (err: any) {
        Notify.create({ type: 'negative', message: err.response?.data?.message || 'Error al eliminar' })
      }
    }

    function imprimir() {
      const filtrosActivos: Record<string, string> = {}
      if (filtros.value.texto) filtrosActivos['Texto'] = filtros.value.texto
      if (filtros.value.estado) filtrosActivos['Estado'] = filtros.value.estado

      const datos = productoStore.productos.map((p) => ({
        ...p,
        nombreCompleto: p.nombre,
      }))

      generarPdf(
        'Inventario',
        [
          { label: 'Código', dataKey: 'codigo' },
          { label: 'Nombre', dataKey: 'nombre' },
          { label: 'Compra', dataKey: 'precioCompra' },
          { label: 'Venta', dataKey: 'precioVenta' },
          { label: 'Stock', dataKey: 'existencia' },
          { label: 'Estado', dataKey: 'activo' },
        ],
        datos,
        'inventario.pdf',
        Object.keys(filtrosActivos).length > 0 ? filtrosActivos : undefined
      )
    }

    return {
      productoStore,
      dialog,
      editando,
      form,
      filtros,
      estados,
      columnas,
      pagination,
      rowsMapped,
      fileInputRef,
      videoRef,
      cameraDialog,
      imagenPreview,
      onRequest,
      buscar,
      limpiarFiltros,
      requiredRule,
      minZeroRule,
      seleccionarImagen,
      onFileSelected,
      abrirCamara,
      capturarFoto,
      detenerCamara,
      quitarImagen,
      abrirDialog,
      guardar,
      toggleActivo,
      eliminar,
      imprimir,
    }
  },
}
</script>
