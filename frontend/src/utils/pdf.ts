import { jsPDF } from 'jspdf'
import 'jspdf-autotable'

const rolMap: Record<string, string> = {
  ROLE_ADMIN: 'Admin',
  ROLE_USER: 'Usuario',
  ROLE_VENDEDOR: 'Vendedor',
}

function formatValor(key: string, val: unknown): string {
  if (key === 'precioCompra' || key === 'precioVenta') {
    return `$ ${(val as number).toFixed(2)}`
  }
  if (key === 'activo') {
    return val ? 'Activo' : 'Inactivo'
  }
  if (key === 'rol') {
    return rolMap[val as string] ?? String(val ?? '')
  }
  if (key === 'nombreCompleto') {
    return String(val ?? '')
  }
  return String(val ?? '')
}

export function generarPdf(
  titulo: string,
  columnas: { label: string; dataKey: string }[],
  datos: Record<string, unknown>[],
  nombreArchivo: string,
  filtros?: Record<string, string>
) {
  const doc = new jsPDF('landscape', 'mm', 'a4')

  doc.setFontSize(18)
  doc.text(titulo, 14, 22)

  doc.setFontSize(10)
  const fecha = new Date().toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
  doc.text(`Generado: ${fecha}`, 14, 30)

  let startY = 36
  if (filtros && Object.keys(filtros).length > 0) {
    doc.setFontSize(9)
    const textoFiltros = Object.entries(filtros)
      .map(([k, v]) => `${k}: ${v}`)
      .join(' | ')
    doc.text(`Filtros: ${textoFiltros}`, 14, 36)
    startY = 42
  }

  const body = datos.map((row) =>
    columnas.map((col) => formatValor(col.dataKey, row[col.dataKey]))
  )

  doc.autoTable({
    columns: columnas.map((c) => ({ header: c.label, dataKey: c.dataKey })),
    body: datos.map((row) => {
      const obj: Record<string, unknown> = {}
      columnas.forEach((c) => {
        obj[c.dataKey] = formatValor(c.dataKey, row[c.dataKey])
      })
      return obj
    }),
    startY,
    styles: { fontSize: 7 },
    headStyles: { fillColor: [25, 118, 210] },
  })

  window.open(doc.output('bloburl'))
}
