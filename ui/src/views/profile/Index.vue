<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleEdit" v-if="!isEditing">
            编辑资料
          </el-button>
          <div v-else>
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" @click="submitForm">保存</el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        :disabled="!isEditing"
      >
        <el-form-item label="头像">
          <div class="avatar-wrapper">
            <div class="avatar-container">
              <el-image 
                :src="convertToProxyUrl(form.avatar) || '/imgs/default.jpg'"
                class="avatar-preview"
                fit="cover"
              >
                <template #error>
                  <el-image
                    src="/imgs/default.jpg"
                    class="avatar-preview"
                    fit="cover"
                  />
                </template>
              </el-image>
            </div>
            <div class="avatar-input" v-if="isEditing">
              <el-input 
                v-model="form.avatar" 
                placeholder="请输入头像URL"
                clearable
              >
                <template #append>
                  <el-tooltip content="可以填写任意图片URL作为头像" placement="top">
                    <el-icon><InfoFilled /></el-icon>
                  </el-tooltip>
                </template>
              </el-input>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>

        <el-form-item label="个人网站" prop="url">
          <el-input v-model="form.url" />
        </el-form-item>

        <el-form-item label="用户角色">
          <div class="roles-list">
            <el-tag 
              v-for="role in form.roles" 
              :key="role.id"
              :type="getRoleTagType(role.roleCode)"
              effect="plain"
            >
              {{ role.roleName }}
              <el-tooltip 
                v-if="role.description" 
                :content="role.description" 
                placement="top"
              >
                <el-icon class="role-info"><InfoFilled /></el-icon>
              </el-tooltip>
            </el-tag>
          </div>
        </el-form-item>

        <el-form-item label="存储容量">
          <div class="capacity-info">
            <div>已用：{{ formatSize(capacityUsed) }}</div>
            <div>总容量：{{ formatSize(form.capacity) }}</div>
            <el-progress 
              :percentage="calculateUsagePercentage()" 
              :status="getStorageStatus()"
            />
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { profile } from '@/api'
import { InfoFilled } from '@element-plus/icons-vue'
import { convertToProxyUrl } from '@/utils/proxyUrl'

export default {
  name: 'UserProfile',
  components: {
    InfoFilled
  },
  setup() {
    const formRef = ref(null)
    const isEditing = ref(false)
    const capacityUsed = ref(0)

    const form = reactive({
      username: '',
      nickname: '',
      email: '',
      url: '',
      avatar: '',
      capacity: 0,
      roles: []
    })

    const rules = {
      nickname: [
        { max: 50, message: '昵称不能超过50个字符', trigger: 'blur' }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      url: [
        { type: 'url', message: '请输入正确的URL地址', trigger: 'blur' }
      ]
    }

    // 获取个人信息
    const fetchProfile = async () => {
      try {
        const { data } = await profile.getProfile()
        if (data.success) {
          Object.assign(form, data.data)
          capacityUsed.value = data.data.capacityUsed || 0
        }
      } catch (error) {
        ElMessage.error('获取个人信息失败')
      }
    }

    // 编辑处理
    const handleEdit = () => {
      isEditing.value = true
    }

    const cancelEdit = () => {
      isEditing.value = false
      fetchProfile()
    }

    // 提交表单
    const submitForm = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        await profile.updateProfile(form)
        ElMessage.success('更新成功')
        isEditing.value = false
        fetchProfile()
      } catch (error) {
        ElMessage.error('更新失败')
      }
    }

    // 格式化容量
    const formatSize = (kb) => {
      if (!kb) return '0 KB'
      if (kb < 1024) return kb.toFixed(2) + ' KB'
      if (kb < 1024 * 1024) return (kb / 1024).toFixed(2) + ' MB'
      return (kb / 1024 / 1024).toFixed(2) + ' GB'
    }

    // 计算使用百分比
    const calculateUsagePercentage = () => {
      if (!form.capacity || !capacityUsed.value) return 0
      return Math.round((capacityUsed.value / form.capacity) * 100)
    }

    // 获取存储状态
    const getStorageStatus = () => {
      const percentage = calculateUsagePercentage()
      if (percentage > 90) return 'exception'
      if (percentage > 70) return 'warning'
      return 'success'
    }

    // 获取角色标签类型
    const getRoleTagType = (roleCode) => {
      const typeMap = {
        'ROLE_SUPER_ADMIN': 'danger',
        'ROLE_ADMIN': 'warning',
        'ROLE_USER': 'info'
      }
      return typeMap[roleCode] || 'info'
    }

    onMounted(() => {
      fetchProfile()
    })

    return {
      formRef,
      form,
      rules,
      isEditing,
      capacityUsed,
      handleEdit,
      cancelEdit,
      submitForm,
      formatSize,
      calculateUsagePercentage,
      getStorageStatus,
      getRoleTagType,
      convertToProxyUrl
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  display: flex;
  gap: 20px;
}

.profile-card {
  flex: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.avatar-container {
  flex-shrink: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.avatar-preview {
  width: 100%;
  height: 100%;
  display: block;
}

.avatar-input {
  flex: 1;
  min-width: 0;
}

.capacity-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

:deep(.el-progress) {
  margin-top: 8px;
}

.roles-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.role-info {
  margin-left: 4px;
  font-size: 12px;
  cursor: help;
}

:deep(.el-tag) {
  display: flex;
  align-items: center;
  padding: 0 8px;
  height: 28px;
}
</style> 