<template>
  <div class="app-wrapper">
    <div class="sidebar">
      <div class="logo">
        <h1>{{ frontConfig?.data?.siteName || '图床管理系统' }}</h1>
      </div>
      <Navigation />
    </div>
    <div class="main-container">
      <div class="header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="header-right">
          <div class="proxy-switch">
            <div class="proxy-info">
              <el-tooltip
                v-if="showProxyWarning"
                content="当前访问图床的地址与配置的服务器地址不一致，如果管理系统中无法正确显示图片，请打开图片域名代理"
                placement="bottom"
              >
                <el-icon class="warning-icon"><Warning /></el-icon>
              </el-tooltip>
              <div class="url-info">
                <div class="url-row">
                  <span class="url-label">当前地址：</span>
                  <span class="url-value">{{ formattedServerUrl }}</span>
                </div>
                <div class="url-row">
                  <span class="url-label">访问地址：</span>
                  <span class="url-value">{{ formattedCurrentUrl }}</span>
                </div>
              </div>
            </div>
            <div class="switch-wrapper">
              <span class="switch-label">图片域名代理</span>
              <el-switch
                v-model="proxyEnabled"
                @change="handleProxyChange"
              >
                <template #default>
                  <el-tooltip
                    content="开启后，图片链接将使用系统配置的访问地址"
                    placement="bottom"
                  >
                    <el-icon><InfoFilled /></el-icon>
                  </el-tooltip>
                </template>
              </el-switch>
            </div>
          </div>
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <div class="user-dropdown">
                <el-image 
                  :size="40" 
                  :src="convertToProxyUrl(userInfo.avatar) || '/imgs/default.jpg'"
                  class="user-avatar"
                  fit="cover"
                >
                  <template #error>
                    <el-image
                      src="/imgs/default.jpg"
                      class="user-avatar"
                      fit="cover"
                    />
                  </template>
                </el-image>
                <el-icon><arrow-down /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <div class="user-info-header">
                    <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
                  </div>
                  <el-dropdown-item divided command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="password">修改密码</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
      <div class="main-content">
        <router-view></router-view>
      </div>
    </div>
    <!-- 添加修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="500px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            show-password
            placeholder="请输入原密码"
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPasswordForm" :loading="passwordLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, InfoFilled, Warning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navigation from '../components/Navigation.vue'
import { user, auth } from '@/api'
import { convertToProxyUrl } from '@/utils/proxyUrl'

export default {
  name: 'MainLayout',
  components: {
    Navigation,
    ArrowDown,
    InfoFilled,
    Warning
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const userInfo = ref({})
    
    // 获取用户信息
    const fetchUserInfo = async () => {
      try {
        const { data } = await user.getProfile()
        if (data.success) {
          userInfo.value = data.data
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }

    const currentRoute = computed(() => {
      const routeMap = {
        '/': '仪表盘',
        '/images': '图片管理',
        '/albums': '相册管理',
        '/users': '用户管理',
        '/settings': '系统设置',
        '/storage': '存储策略',
        '/stats': '访问统计'
      }
      return routeMap[route.path] || ''
    })

    // 添加修改密码相关响应式变量
    const passwordDialogVisible = ref(false)
    const passwordFormRef = ref(null)
    const passwordLoading = ref(false)
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    // 密码验证规则
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else {
        if (passwordForm.confirmPassword !== '') {
          passwordFormRef.value?.validateField('confirmPassword')
        }
        callback()
      }
    }

    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'))
      } else if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    const passwordRules = {
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, validator: validatePass, trigger: 'blur' },
        { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, validator: validatePass2, trigger: 'blur' }
      ]
    }

    // 修改密提交方法
    const submitPasswordForm = async () => {
      if (!passwordFormRef.value) return
      
      try {
        await passwordFormRef.value.validate()
        passwordLoading.value = true
        
        await user.updatePassword(passwordForm.oldPassword, passwordForm.newPassword)
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
        
        // 清空表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '密码修改失败')
      } finally {
        passwordLoading.value = false
      }
    }

    const handleCommand = async (command) => {
      switch (command) {
        case 'logout':
          try {
            await auth.logout()  // 调用修改后的登出方法
            router.push('/login')
            ElMessage.success('已安全退出')
          } catch (error) {
            ElMessage.error('退出失败')
          }
          break
        case 'profile':
          router.push('/profile')
          break
        case 'password':
          passwordDialogVisible.value = true  // 显示修改密码对话框
          break
      }
    }

    // 获取前端配置
    const frontConfig = computed(() => {
      try {
        const config = localStorage.getItem('frontConfig')
        return config ? JSON.parse(config) : null
      } catch (e) {
        console.error('Parse frontConfig error:', e)
        return null
      }
    })

    // 修改页面标题
    watch(frontConfig, (newConfig) => {
      if (newConfig?.data?.siteName) {
        document.title = newConfig.data.siteName
      }
    }, { immediate: true })

    // 代理开关状态
    const proxyEnabled = ref(localStorage.getItem('imageProxyEnabled') === 'true')

    // 处理代理开关变化
    const handleProxyChange = (value) => {
      localStorage.setItem('imageProxyEnabled', value)
    }

    const currentUrl = ref(window.location.origin)

    // 格式化 URL 显示，确保带上斜杠
    const formatUrl = (url) => url?.endsWith('/') ? url : `${url}/`

    // 计算属性：格式化后的当前地址
    const formattedServerUrl = computed(() => 
      formatUrl(frontConfig.value?.data?.serverUrl) || '未设置'
    )

    // 计算属性：格式化后的访问地址
    const formattedCurrentUrl = computed(() => 
      formatUrl(currentUrl.value)
    )

    // 添加警告显示逻辑
    const showProxyWarning = computed(() => {
      if (proxyEnabled.value) return false
      
      const serverUrl = frontConfig.value?.data?.serverUrl
      if (!serverUrl || !currentUrl.value) return false

      try {
        // 标准化 URL，确保都以斜杠结尾
        const normalizeUrl = (url) => url.endsWith('/') ? url : `${url}/`
        const normalizedServer = normalizeUrl(serverUrl)
        const normalizedCurrent = normalizeUrl(currentUrl.value)
        return normalizedServer !== normalizedCurrent
      } catch (error) {
        return false
      }
    })

    onMounted(() => {
      fetchUserInfo()
    })

    return {
      currentRoute,
      handleCommand,
      userInfo,  // 返回用户信息
      passwordDialogVisible,
      passwordFormRef,
      passwordForm,
      passwordRules,
      passwordLoading,
      submitPasswordForm,
      frontConfig,
      proxyEnabled,
      handleProxyChange,
      currentUrl,
      formattedServerUrl,
      formattedCurrentUrl,
      convertToProxyUrl,
      showProxyWarning
    }
  }
}
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.sidebar {
  width: 240px;
  height: 100%;
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h1 {
  color: white;
  font-size: 18px;
  margin: 0;
  padding: 0;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.proxy-switch {
  display: flex;
  align-items: center;
  gap: 24px;
}

.proxy-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.url-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.url-row {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  white-space: nowrap;
}

.url-label {
  color: #909399;
  min-width: 70px;
}

.url-value {
  color: #409EFF;
  font-family: monospace;
}

.warning-icon,
.proxy-info:has(.warning-icon) .url-value {
  animation: pulse 2s infinite;
}

.warning-icon {
  color: #F56C6C;
  font-size: 20px;
  flex-shrink: 0;
  margin-right: 4px;
  transform: translateY(2px);
}

.proxy-info:has(.warning-icon) .url-value {
  color: #E6A23C;  /* 使用 Element Plus 的警告色 */
}

.switch-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.switch-label {
  font-size: 14px;
  color: #606266;
}

:deep(.el-switch) {
  margin-right: 4px;
}

:deep(.el-tooltip__trigger) {
  color: #909399;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  height: 60px;
  padding: 0 4px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info-header {
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

:deep(.el-dropdown-menu__item--divided) {
  margin-top: 0;
  border-top: none;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  background-color: transparent;
}

.main-content {
  flex: 1;
  overflow: auto;
  background-color: #f0f2f5;
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}
</style> 