import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  build: {
    chunkSizeWarningLimit: 600,
    rollupOptions: {
      output: {
        manualChunks: {
          'vue-vendor': ['vue', 'vue-router'],
          'element-plus': ['element-plus'],
          'element-plus-icons': ['@element-plus/icons-vue'],
          'utils-vendor': ['axios', 'dayjs'],
          'views': [
            './src/views/album/Index.vue',
            './src/views/api/Index.vue',
            './src/views/auth/Login.vue',
            './src/views/auth/Register.vue',
            './src/views/dashboard/Index.vue',
            './src/views/gallery/Index.vue',
            './src/views/image/Index.vue',
            './src/views/init/Index.vue',
            './src/views/profile/Index.vue',
            './src/views/stats/Index.vue',
            './src/views/storage/Index.vue',
            './src/views/system/Index.vue',
            './src/views/user/Index.vue'
          ]
        }
      }
    },
    cssCodeSplit: true,
    sourcemap: false,
    minify: 'esbuild',
    esbuild: {
      drop: ['console', 'debugger']
    }
  },
  optimizeDeps: {
    include: [
      'vue',
      'vue-router',
      'element-plus',
      '@element-plus/icons-vue',
      'axios'
    ]
  }
})
