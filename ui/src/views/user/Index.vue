<template>
  <div class="user-container">
    <!-- 顶部操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>创建用户
      </el-button>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-input
          v-model="searchForm.username"
          placeholder="用户名"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <el-input
          v-model="searchForm.email"
          placeholder="邮箱"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <el-select v-model="searchForm.enabled" placeholder="状态" clearable @change="handleSearch">
          <el-option :value="true" label="启用" />
          <el-option :value="false" label="禁用" />
        </el-select>
      </div>
    </div>

    <!-- 用户列表 -->
    <el-table :data="users" style="width: 100%" v-loading="loading">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column label="角色">
        <template #default="{ row }">
          <div class="roles-list">
            <el-tag 
              v-for="role in row.roles" 
              :key="role.id"
              :type="getRoleTagType(role.roleCode)"
              effect="plain"
              size="small"
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
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态">
        <template #default="{ row }">
          <el-switch
            v-model="row.enabled"
            @change="(val) => handleStatusChange(row, val)"
            :loading="row.statusLoading"
          />
        </template>
      </el-table-column>
      <el-table-column label="存储容量">
        <template #default="{ row }">
          <div>
            <div>总容量：{{ formatCapacity(row.capacity) }}</div>
            <div>已用量：{{ formatCapacity(row.capacityUsed) }}</div>
            <el-progress 
              :percentage="calculateUsagePercentage(row.capacity, row.capacityUsed)" 
              :status="calculateUsageStatus(row.capacity, row.capacityUsed)"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" label="最后登录" width="180">
        <template #default="{ row }">
          {{ formatDate(row.lastLoginTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button-group>
            <el-button link type="primary" @click="handleEdit(row)">
              编辑容量
            </el-button>
            <el-button link type="warning" @click="handleResetPassword(row)">
              重置密码
            </el-button>
            <el-button 
              link 
              :type="hasAdminRole(row) ? 'warning' : 'success'"
              @click="handleAdminRole(row)"
            >
              {{ hasAdminRole(row) ? '取消管理权限' : '设为管理员' }}
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 创建用户对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建用户"
      width="500px"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="createForm.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="createForm.password" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCreate">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑容量对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑容量"
      width="500px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="容量" prop="capacity">
          <div class="capacity-input">
            <el-input-number 
              v-model="capacityValue" 
              :min="1"
              :precision="2"
              @change="handleCapacityChange"
            />
            <el-select v-model="capacityUnit" @change="handleCapacityChange" style="width: 80px">
              <el-option label="KB" value="KB" />
              <el-option label="MB" value="MB" />
              <el-option label="GB" value="GB" />
            </el-select>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordDialogVisible"
      title="重置密码"
      width="500px"
    >
      <el-form
        ref="resetPasswordFormRef"
        :model="resetPasswordForm"
        :rules="resetPasswordRules"
        label-width="80px"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetPasswordForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetPasswordForm.confirmPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitResetPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, InfoFilled } from '@element-plus/icons-vue'
import { userManage } from '@/api'

export default {
  name: 'UserManagement',
  components: {
    Search,
    Plus,
    InfoFilled
  },
  setup() {
    const loading = ref(false)
    const users = ref([])
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)
    const searchForm = reactive({
      username: '',
      email: '',
      enabled: null
    })

    // 获取用户列表
    const fetchUsers = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          page_size: pageSize.value,
          ...searchForm
        }
        const { data } = await userManage.getList(params)
        if (data.success) {
          users.value = data.data.data
          total.value = data.data.total
        }
      } catch (error) {
        ElMessage.error('获取用户列表失败')
      } finally {
        loading.value = false
      }
    }

    // 处理搜索
    const handleSearch = () => {
      currentPage.value = 1
      fetchUsers()
    }

    // 处理分页
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchUsers()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchUsers()
    }

    // 格式化日期
    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleString()
    }

    // 创建用户相关
    const createDialogVisible = ref(false)
    const createFormRef = ref(null)
    const createForm = reactive({
      username: '',
      email: '',
      password: ''
    })
    const createRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
      ]
    }

    const handleCreate = () => {
      createForm.username = ''
      createForm.email = ''
      createForm.password = ''
      createDialogVisible.value = true
    }

    const submitCreate = async () => {
      try {
        await createFormRef.value.validate()
        await userManage.create(createForm)
        ElMessage.success('创建成功')
        createDialogVisible.value = false
        fetchUsers()
      } catch (error) {
        ElMessage.error(error.message || '创建失')
      }
    }

    // 编辑容量相关
    const editDialogVisible = ref(false)
    const editFormRef = ref(null)
    const editForm = reactive({
      userId: null,
      capacity: 0
    })
    const editRules = {
      capacity: [
        { required: true, message: '请输入容量', trigger: 'blur' }
      ]
    }

    const capacityValue = ref(0)
    const capacityUnit = ref('MB')

    const handleEdit = (user) => {
      editForm.userId = user.userId
      
      if (user.capacity >= 1024 * 1024) {
        capacityValue.value = Number((user.capacity / (1024 * 1024)).toFixed(2))
        capacityUnit.value = 'GB'
      } else if (user.capacity >= 1024) {
        capacityValue.value = Number((user.capacity / 1024).toFixed(2))
        capacityUnit.value = 'MB'
      } else {
        capacityValue.value = Number(user.capacity)
        capacityUnit.value = 'KB'
      }
      
      editDialogVisible.value = true
    }

    const handleCapacityChange = () => {
      switch (capacityUnit.value) {
        case 'GB':
          editForm.capacity = Number(capacityValue.value) * 1024 * 1024
          break
        case 'MB':
          editForm.capacity = Number(capacityValue.value) * 1024
          break
        case 'KB':
        default:
          editForm.capacity = Number(capacityValue.value)
      }
    }

    const submitEdit = async () => {
      try {
        await editFormRef.value.validate()
        await userManage.updateCapacity(editForm.userId, editForm.capacity)
        ElMessage.success('更新成功')
        editDialogVisible.value = false
        fetchUsers()
      } catch (error) {
        ElMessage.error('更新失败')
      }
    }

    // 重置密码相关
    const resetPasswordDialogVisible = ref(false)
    const resetPasswordFormRef = ref(null)
    const resetPasswordForm = reactive({
      userId: null,
      newPassword: '',
      confirmPassword: ''
    })
    const resetPasswordRules = {
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== resetPasswordForm.newPassword) {
              callback(new Error('两次输入密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }

    const handleResetPassword = (user) => {
      resetPasswordForm.userId = user.userId
      resetPasswordForm.newPassword = ''
      resetPasswordForm.confirmPassword = ''
      resetPasswordDialogVisible.value = true
    }

    const submitResetPassword = async () => {
      try {
        await resetPasswordFormRef.value.validate()
        await userManage.resetPassword(
          resetPasswordForm.userId,
          resetPasswordForm.newPassword
        )
        ElMessage.success('密码重置成功')
        resetPasswordDialogVisible.value = false
      } catch (error) {
        ElMessage.error('密码重置失败')
      }
    }

    // 更新用户状态
    const handleStatusChange = async (user, status) => {
      try {
        user.statusLoading = true
        await userManage.updateStatus(user.userId, status)
        ElMessage.success('状态更新成功')
      } catch (error) {
        user.enabled = !status
        ElMessage.error('状态更新失败')
      } finally {
        user.statusLoading = false
      }
    }

    // 格式化容量显示
    const formatCapacity = (bytes) => {
      if (!bytes) return '0 KB'
      if (bytes < 1024) return bytes.toFixed(2) + ' KB'
      if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' MB'
      return (bytes / (1024 * 1024)).toFixed(2) + ' GB'
    }

    // 计算使用百分比
    const calculateUsagePercentage = (capacity, used) => {
      if (!capacity || !used) return 0
      const percentage = Math.round((used / capacity) * 100)
      return isNaN(percentage) ? 0 : percentage
    }

    // 计算使用状态
    const calculateUsageStatus = (capacity, used) => {
      if (!capacity || !used) return ''
      const percentage = (used / capacity)
      return percentage > 0.9 ? 'exception' : ''
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

    // 检查是否有管理员角色
    const hasAdminRole = (user) => {
      return user.roles?.some(role => 
        role.roleCode === 'ROLE_ADMIN' || role.roleCode === 'ROLE_SUPER_ADMIN'
      )
    }

    // 处理管理员权限切换
    const handleAdminRole = (user) => {
      const isAdmin = hasAdminRole(user)
      const action = isAdmin ? '取消' : '设置'
      
      ElMessageBox.confirm(
        `确定要${action}该用户的管理员权限吗？`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          confirmButtonClass: isAdmin ? 'el-button--warning' : 'el-button--success'
        }
      ).then(async () => {
        try {
          await userManage.setUserAsAdmin(user.userId)
          ElMessage.success(`${action}管理员权限成功`)
          fetchUsers()  // 刷新用户列表
        } catch (error) {
          ElMessage.error(`${action}管理员权限失败`)
        }
      })
    }

    onMounted(() => {
      fetchUsers()
    })

    return {
      loading,
      users,
      currentPage,
      pageSize,
      total,
      searchForm,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      formatDate,
      createDialogVisible,
      createFormRef,
      createForm,
      createRules,
      editDialogVisible,
      editFormRef,
      editForm,
      editRules,
      resetPasswordDialogVisible,
      resetPasswordFormRef,
      resetPasswordForm,
      resetPasswordRules,
      handleCreate,
      submitCreate,
      handleEdit,
      submitEdit,
      handleResetPassword,
      submitResetPassword,
      handleStatusChange,
      formatCapacity,
      calculateUsagePercentage,
      calculateUsageStatus,
      getRoleTagType,
      hasAdminRole,
      handleAdminRole,
      capacityValue,
      capacityUnit,
      handleCapacityChange
    }
  }
}
</script>

<style scoped>
.user-container {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-area {
  margin-left: auto;
  display: flex;
  gap: 12px;
}

.search-input {
  width: 200px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-switch) {
  margin-right: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.roles-list {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.role-info {
  margin-left: 2px;
  font-size: 12px;
  cursor: help;
}

:deep(.el-tag) {
  display: flex;
  align-items: center;
  padding: 0 6px;
}

:deep(.el-tag .el-icon) {
  margin-left: 2px;
  font-size: 12px;
}

.capacity-input {
  display: flex;
  gap: 8px;
  align-items: center;
}

:deep(.el-input-number) {
  flex: 1;
}
</style> 