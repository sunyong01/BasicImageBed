<template>
  <router-view></router-view>
</template>

<script>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { init } from '@/api'

export default {
  name: 'App',
  setup() {
    const router = useRouter()

    const fetchFrontConfig = async () => {
      try {
        const response = await init.getFrontConfig()
        if (response.success) {
          // 直接存储整个data对象
          localStorage.setItem('frontConfig', JSON.stringify(response.data))
          console.log('Front config updated:', response.data)
        }
      } catch (error) {
        console.error('Failed to load front config:', error)
      }
    }

    onMounted(() => {
      fetchFrontConfig()
    })
  }
}
</script>

<style>
#app {
  height: 100vh;
  margin: 0;
  padding: 0;
}
</style>
