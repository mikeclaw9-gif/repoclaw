import { fileURLToPath } from 'url'
import path from 'path'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

export default function (ctx) {
  return {
    boot: ['axios'],
    css: ['app.scss'],
    extras: ['material-icons'],
    build: {
      distDir: 'dist',
      vueRouterMode: 'hash'
    },
    devServer: {
      port: 9000,
      host: '0.0.0.0',
      open: false,
      proxy: {
        '/api': {
          target: 'http://localhost:8090',
          changeOrigin: true
        }
      }
    },
    framework: {
      iconSet: 'material-icons',
      lang: 'es',
      plugins: ['Dialog', 'Notify', 'Loading', 'LocalStorage']
    },
    vite: {
      server: {
        allowedHosts: true
      },
      resolve: {
        alias: {
          '@': path.resolve(__dirname, 'src')
        }
      }
    }
  }
}
