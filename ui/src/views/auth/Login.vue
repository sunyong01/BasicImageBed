<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>登录</h2>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            class="submit-btn" 
            :loading="loading"
            @click="handleSubmit"
          >
            登录
          </el-button>
        </el-form-item>

        <!-- 根据配置显示注册按钮 -->
        <div v-if="allowRegister" class="register-link">
          <el-button link type="primary" @click="handleRegister">
            还没有账号？立即注册
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { auth } from '@/api'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const formRef = ref(null)
    const loading = ref(false)
    const allowRegister = ref(false)

    const form = reactive({
      username: '',
      password: ''
    })

    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    }

    // 获取前端配置
    const getFrontConfig = () => {
      const config = localStorage.getItem('frontConfig')
      if (config) {
        const { allowRegister: register } = JSON.parse(config)
        allowRegister.value = register
      }
    }

    // 处理登录
    const handleSubmit = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        loading.value = true
        
        const response = await auth.login(form)
        if (response.success) {
          ElMessage.success('登录成功')
          router.push('/')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }

    // 处理注册
    const handleRegister = () => {
      router.push('/register')
    }

    onMounted(() => {
      getFrontConfig()
    })

    return {
      formRef,
      form,
      rules,
      loading,
      allowRegister,
      handleSubmit,
      handleRegister
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.submit-btn {
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 16px;
}

:deep(.el-card__header) {
  text-align: center;
  padding: 20px;
}

:deep(.el-card__header h2) {
  margin: 0;
  font-size: 24px;
  color: #303133;
}
</style> 