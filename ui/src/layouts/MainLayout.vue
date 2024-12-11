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
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <div class="user-dropdown">
              <el-avatar 
                :size="40" 
                :src="userInfo.avatar || '/imgs/default.jpg'"
                class="user-avatar"
              />
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
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navigation from '../components/Navigation.vue'
import { user, auth } from '@/api'

export default {
  name: 'MainLayout',
  components: {
    Navigation,
    ArrowDown
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

    // 添加修改密码相关的响应式变量
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

    // 修改密码提交方法
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
      frontConfig
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
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.user-dropdown:hover .user-avatar {
  transform: scale(1.05);
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
</style> 