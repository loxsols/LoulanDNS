import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api/users': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/users/, '/admin/api/user/list/user')
      },
      '/api/user/get': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/user\/get/, '/admin/api/user/get/user')
      },
      '/api/user/create': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/user\/create/, '/admin/api/user/create/user')
      },
      '/api/user/update': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/user\/update/, '/admin/api/user/update/user')
      },
      '/api/user/delete': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/user\/delete/, '/admin/api/user/delete/user')
      },
      '/api/dns/services': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/dns\/services/, '/admin/api/dns/service/list/dns-service-instance')
      },
      '/api/dns/service': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => {
          // support /api/dns/service/:id  -> /admin/api/dns/service/get/dns-service-instance/:id
          const m = path.match(/^\/api\/dns\/service\/(\d+)(.*)$/)
          if (m) return '/admin/api/dns/service/get/dns-service-instance/' + m[1] + m[2]
          return path.replace(/^\/api\/dns\/service/, '/admin/api/dns/service')
        }
      },
      '/api/dns/service/create': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/dns\/service\/create/, '/admin/api/dns/service/create/dns-service-instance')
      },
      '/api/dns/service/update': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/dns\/service\/update/, '/admin/api/dns/service/update/dns-service-instance')
      },
      '/api/dns/service/delete': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/dns\/service\/delete/, '/admin/api/dns/service/delete/dns-service-instance')
      }
    }
  }
})
