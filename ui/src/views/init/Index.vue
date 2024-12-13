<template>
  <div class="init-container">
    <el-card class="init-card">
      <template #header>
        <h2>系统初始化配置</h2>
      </template>
      
      <el-form :model="form" label-width="180px">
        <h3>数据库配置</h3>
        <el-form-item label="数据库地址">
          <el-input v-model="form.dbUrl" placeholder="例如：jdbc:mysql://localhost:3306/imagebed" />
        </el-form-item>
        <el-form-item label="数据库用户名">
          <el-input v-model="form.dbUsername" />
        </el-form-item>
        <el-form-item label="数据库密码">
          <el-input v-model="form.dbPassword" type="password" />
        </el-form-item>

        <h3>存储配置</h3>
        <el-form-item label="服务访问地址">
          <div class="server-url-input">
            <el-input 
              v-model="form.serverUrl" 
              placeholder="例如：https://img.example.com/"
            >
              <template #append>
                <el-tooltip 
                  content="如果通过反向代理访问，请手动填写实际访问的域名地址" 
                  placement="top"
                >
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-input>
            <el-button @click="detectServerUrl">
              使用当前地址
            </el-button>
          </div>
          <div class="url-tip" style="margin-left: -180px">
            <div class="current-url">
              <span class="label">当前访问地址:</span>
              <span class="url-value">{{ currentUrl }}</span>
            </div>
            <el-alert
              type="warning"
              :closable="false"
              show-icon
            >
              如果您通过反向代理访问（如 Nginx），请填写实际对外访问的地址，而不是内部地址
            </el-alert>
          </div>
        </el-form-item>
        
        <h3>上传限制</h3>
        <el-form-item label="单文件大小限制(KB)">
          <el-input-number v-model="form.allowSizeKb" :min="1" />
        </el-form-item>
        <el-form-item label="允许的文件类型">
          <el-input v-model="form.allowSuffix" placeholder="用逗号分隔，例如：jpg,png,gif" />
        </el-form-item>

        <h3>认证设置</h3>
        <el-form-item label="允许游客访问">
          <el-switch v-model="form.authAllowGuest" />
        </el-form-item>
        <el-form-item label="允许注册">
          <el-switch v-model="form.authAllowRegister" />
        </el-form-item>
        
        <h3>系统设置</h3>
        <el-form-item label="JWT密钥">
          <el-input 
            v-model="form.jwtSecret" 
            placeholder="不填写将自动生成随机密钥"
          />
        </el-form-item>
        <el-form-item label="JWT过期时间(秒)">
          <el-input-number v-model="form.jwtExpiration" :min="1" />
        </el-form-item>
        <el-form-item label="调试模式">
          <el-switch v-model="form.debug" />
        </el-form-item>

        <h3>管理员账户</h3>
        <el-form-item label="用户名">
          <el-input 
            v-model="form.adminUsername" 
            placeholder="默认为：admin"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input 
            v-model="form.adminPassword" 
            type="password"
            placeholder="默认为admin"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input 
            v-model="form.adminEmail" 
            placeholder="管理员邮箱地址"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">初始化系统</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { init } from '@/api'

const router = useRouter()
const form = reactive({
  dbUrl: '',
  dbUsername: '',
  dbPassword: '',
  serverUrl: '',
  allowSizeKb: 5120,
  allowSuffix: 'jpg,jpeg,png,gif,bmp,webp',
  authAllowGuest: false,
  authAllowRegister: true,
  jwtSecret: '',
  jwtExpiration: 86400,
  debug: false,
  
  adminUsername: 'admin',
  adminPassword: 'admin',
  adminEmail: 'admin@local.host'
})

// 获取当前访问地址
const currentUrl = ref('')

// 检测当前访问地址
const detectCurrentUrl = () => {
  const location = window.location
  const protocol = location.protocol
  const hostname = location.hostname
  const port = location.port
  
  // 构建基础URL
  let baseUrl = `${protocol}//${hostname}`
  if (port && port !== '80' && port !== '443') {
    baseUrl += `:${port}`
  }
  
  // 如果有上下文路径，也加上
  const pathname = location.pathname.split('/').filter(Boolean)[0]
  if (pathname) {
    baseUrl += `/${pathname}`
  }
  
  baseUrl += '/'
  currentUrl.value = baseUrl
  return baseUrl
}

// 使用当前地址
const detectServerUrl = () => {
  form.serverUrl = detectCurrentUrl()
}

onMounted(() => {
  detectCurrentUrl()
})

const handleSubmit = async () => {
  try {
    if (!form.jwtSecret.trim()) {
      ElMessage.info('JWT密钥未填写，将自动生成随机密钥')
    }
    
    if (!form.adminUsername.trim()) {
      form.adminUsername = 'admin'
    }
    if (!form.adminPassword.trim()) {
      form.adminPassword = 'admin'
    }
    
    const response = await init.initSystem(form)
    if (response.success) {
      ElMessage.success('系统初始化成功')
      const frontConfig = localStorage.getItem('frontConfig')
      if (frontConfig) {
        const config = JSON.parse(frontConfig)
        config.systemInitialized = true
        localStorage.setItem('frontConfig', JSON.stringify(config))
      }
      router.push('/login')
    } else {
      ElMessage.error(response.message || '初始化失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '初始化失败')
  }
}
</script>

<style scoped>
.init-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}

.init-card {
  width: 800px;
}

h3 {
  margin: 20px 0 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  color: #409EFF;
}

.el-icon {
  cursor: help;
}

.server-url-input {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.url-tip {
  font-size: 13px;
  color: #606266;
  margin-top: 8px;
  width: calc(100% + 180px);
  padding: 0;
}

:deep(.el-alert) {
  margin-top: 8px;
  padding: 8px 12px;
}

:deep(.el-alert__content) {
  padding: 0 8px;
}

.current-url {
  display: flex;
  align-items: center;
  height: 32px;
  margin-bottom: 8px;
  padding: 0 0 0 180px;
}

.current-url .label {
  width: 180px;
  margin-left: -180px;
  text-align: right;
  padding-right: 12px;
}

.url-value {
  flex: 1;
  color: #303133;
  font-family: monospace;
  background-color: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  height: 32px;
  line-height: 24px;
  box-sizing: border-box;
}
</style> 