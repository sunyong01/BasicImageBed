import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

export const useUserStore = defineStore('user', () => {
  const router = useRouter()
  const token = ref(localStorage.getItem('token'))
  const username = ref('')
  
  const login = async (credentials) => {
    try {
      const response = await api.login(credentials)
      token.value = response.data
      localStorage.setItem('token', response.data)
      router.push('/')
    } catch (error) {
      ElMessage.error(error.message || '登录失败')
    }
  }
  
  const logout = () => {
    token.value = null
    localStorage.removeItem('token')
    router.push('/auth/login')
  }
  
  return {
    token,
    username,
    login,
    logout
  }
}) 