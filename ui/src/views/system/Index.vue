<template>
  <div class="system-container">
    <el-card class="system-card">
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
          <div class="header-actions">
            <el-button 
              type="warning" 
              @click="handleRestart"
              :loading="restarting"
            >
              <el-icon><Refresh /></el-icon>重启系统
            </el-button>
            <el-button @click="resetForm">重置</el-button>
            <el-button type="primary" @click="submitForm">保存设置</el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="160px"
        class="settings-form"
      >
        <h3>基础设置</h3>
        <div class="form-row">
          <el-form-item label="系统站点名称" prop="siteName">
            <el-input 
              v-model="form.siteName" 
              placeholder="例如：我的图床"
              class="medium-input"
            >
              <template #append>
                <el-tooltip content="显示在浏览器标签页和系统顶部的站点名称" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="服务访问地址" prop="serverUrl">
            <el-input 
              v-model="form.serverUrl" 
              placeholder="例如：https://img.example.com/"
              class="medium-input"
            >
              <template #append>
                <el-tooltip content="影响图片链接。错误填写会导致所有图片链接无法访问" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <h3>上传限制</h3>
        <div class="form-row">
          <el-form-item label="最大文件大小(KB)" prop="maxSize">
            <el-input-number 
              v-model="form.maxSize" 
              :min="1"
              :max="102400"
              class="small-input"
            />
          </el-form-item>
          <el-form-item label="允许的文件类型" prop="allowedTypes">
            <el-input 
              v-model="form.allowedTypes" 
              placeholder="用逗号分隔，例如：jpg,png,gif"
              class="medium-input"
            />
          </el-form-item>
        </div>

        <h3>访问控制</h3>
        <div class="form-row">
          <el-form-item label="允许游客访问" prop="allowGuest" class="switch-item">
            <el-switch v-model="form.allowGuest">
              <template #append>
                <el-tooltip content="是否允许未登录用户访问图片" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-switch>
          </el-form-item>
          <el-form-item label="允许注册" prop="allowRegister" class="switch-item">
            <el-switch v-model="form.allowRegister">
              <template #append>
                <el-tooltip content="是否允许新用户注册" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-switch>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="启用Referer控制" prop="enableRefererControl" class="switch-item">
            <el-switch v-model="form.enableRefererControl" />
          </el-form-item>
          <el-form-item 
            label="允许空Referer" 
            prop="allowEmptyReferer" 
            class="switch-item"
            :disabled="!form.enableRefererControl"
          >
            <el-switch v-model="form.allowEmptyReferer">
              <template #append>
                <el-tooltip content="是否允许直接访问图片（不带来源网站信息）" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-switch>
          </el-form-item>
        </div>

        <el-form-item 
          label="允许的Referer" 
          prop="allowedReferers"
          class="full-width-item"
          :disabled="!form.enableRefererControl"
        >
          <div class="referer-list">
            <div 
              v-for="(referer, index) in form.allowedReferers" 
              :key="index"
              class="referer-item"
            >
              <el-input 
                v-model="form.allowedReferers[index]" 
                placeholder="例如：*.example.com"
              >
                <template #append>
                  <el-button 
                    type="danger" 
                    :icon="Delete" 
                    @click="removeReferer(index)"
                  />
                </template>
              </el-input>
            </div>
            <el-button 
              type="primary" 
              plain 
              :icon="Plus"
              @click="addReferer"
            >
              添加Referer
            </el-button>
          </div>
          <div class="form-tip">
            <div class="tip-content">
              <p>支持通配符 *，例如：*.example.com 将允许所有 example.com 的子域名。</p>
              <p>{{ form.enableRefererControl ? '留空则不限制来源。' : 'Referer控未启用，所有来源都被允许。' }}</p>
            </div>
          </div>
        </el-form-item>

        <h3>安全设置</h3>
        <div class="form-row">
          <el-form-item label="JWT密钥" prop="jwtSecret">
            <el-input 
              v-model="form.jwtSecret" 
              type="password" 
              show-password
              class="medium-input"
            />
          </el-form-item>
          <el-form-item label="JWT过期时间(秒)" prop="jwtExpiration">
            <el-input-number 
              v-model="form.jwtExpiration" 
              :min="300"
              :max="2592000"
              class="small-input"
            />
          </el-form-item>
        </div>

        <h3>调试设置</h3>
        <div class="form-row">
          <el-form-item label="调试模式" prop="debug" class="switch-item">
            <el-switch v-model="form.debug" />
          </el-form-item>
          <el-form-item label="启用默认Swagger" prop="enableDefaultSwaggerAccess" class="switch-item">
            <el-switch v-model="form.enableDefaultSwaggerAccess">
              <template #append>
                <el-tooltip content="是否允许通过默认路径访问Swagger文档" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </template>
            </el-switch>
          </el-form-item>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled, Plus, Delete, Refresh } from '@element-plus/icons-vue'
import { system } from '@/api'

export default {
  name: 'SystemSettings',
  components: {
    InfoFilled,
    Plus,
    Delete,
    Refresh
  },
  setup() {
    const formRef = ref(null)
    const originalConfig = ref(null)
    const restarting = ref(false)

    const form = reactive({
      siteName: '',
      serverUrl: '',
      maxSize: 5120,
      allowedTypes: '',
      allowGuest: false,
      allowRegister: true,
      jwtSecret: '',
      jwtExpiration: 86400,
      debug: false,
      allowEmptyReferer: true,
      allowedReferers: [],
      enableRefererControl: false,
      enableDefaultSwaggerAccess: false
    })

    const rules = {
      siteName: [
        { required: true, message: '请输入站点名称', trigger: 'blur' },
        { max: 50, message: '站点名称不能超过50个字符', trigger: 'blur' }
      ],
      serverUrl: [
        { required: true, message: '请输入服务访问地址', trigger: 'blur' },
        { 
          validator: (rule, value, callback) => {
            if (!value) {
              callback()
              return
            }
            // 检查是否是有效的URL且以/结尾
            try {
              new URL(value)
              if (!value.endsWith('/')) {
                callback(new Error('服务访问地址必须以/结尾'))
                return
              }
              callback()
            } catch (e) {
              callback(new Error('请输入有效的URL地址'))
            }
          }, 
          trigger: 'blur' 
        }
      ],
      maxSize: [
        { required: true, message: '请输入最大文件大小', trigger: 'blur' },
        { type: 'number', min: 1, message: '大小必须大于0', trigger: 'blur' }
      ],
      allowedTypes: [
        { required: true, message: '请输入允许的文件类型', trigger: 'blur' },
        { pattern: /^[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*$/, message: '请输入正确的格式，用逗号分隔', trigger: 'blur' }
      ],
      jwtExpiration: [
        { required: true, message: '请输入JWT过期时间', trigger: 'blur' },
        { type: 'number', min: 300, message: '过期时间不能小于300秒', trigger: 'blur' }
      ]
    }

    // 获取系统配置
    const fetchConfig = async () => {
      try {
        const { data } = await system.getConfig()
        if (data.success) {
          originalConfig.value = { ...data.data }
          Object.assign(form, data.data)
        }
      } catch (error) {
        ElMessage.error('获取系统配置失败')
      }
    }

    // 重置表单
    const resetForm = () => {
      if (originalConfig.value) {
        Object.assign(form, originalConfig.value)
      }
      ElMessage.success('已重置为上一次保存的配置')
    }

    // 提交表单
    const submitForm = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        // 确保服务器地址以/结尾
        if (!form.serverUrl.endsWith('/')) {
          form.serverUrl += '/'
        }
        await system.updateConfig(form)
        ElMessage.success('系统配置更新成功')
        originalConfig.value = { ...form }
      } catch (error) {
        ElMessage.error('系统配置更新失败')
      }
    }

    // 添加Referer
    const addReferer = () => {
      form.allowedReferers.push('')
    }

    // 删除Referer
    const removeReferer = (index) => {
      form.allowedReferers.splice(index, 1)
    }

    // 添加重启系统方法
    const handleRestart = () => {
      ElMessageBox.confirm(
        '确定要重启系统吗？重启期间���统将暂时无法访问。',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        try {
          restarting.value = true
          await system.restart()
          ElMessage.success('系统正在重启，请稍后刷新页面')
        } catch (error) {
          ElMessage.error('重启失败')
        } finally {
          restarting.value = false
        }
      })
    }

    onMounted(() => {
      fetchConfig()
    })

    return {
      formRef,
      form,
      rules,
      resetForm,
      submitForm,
      addReferer,
      removeReferer,
      Plus,
      Delete,
      restarting,
      handleRestart
    }
  }
}
</script>

<style scoped>
.system-container {
  padding: 20px;
}

.system-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h3 {
  margin: 20px 0 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  color: #409EFF;
}

:deep(.el-form-item__content) {
  display: flex;
  align-items: center;
}

:deep(.el-input-number) {
  width: 200px;
}

:deep(.el-tooltip) {
  margin-left: 8px;
}

:deep(.el-input-group__append .el-icon) {
  margin: 0 4px;
}

.referer-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.referer-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}

.tip-content {
  flex: 1;
}

.tip-content p {
  margin: 0;
  line-height: 1.6;
}

.tip-content p + p {
  margin-top: 4px;
}

:deep(.el-input-group__append .el-button) {
  padding: 5px 8px;
  margin: 0;
}

:deep(.el-input-group__append .el-icon) {
  margin: 0;
}

.settings-form {
  max-width: 800px;
}

/* 中等宽度输入框 */
:deep(.medium-input) {
  width: 400px;
}

/* 小型输入框 */
:deep(.small-input) {
  width: 180px;
}

/* 开关类型的表单项 */
.switch-item {
  max-width: 400px;
}

/* 全宽的表单项 */
.full-width-item {
  max-width: 100%;
}

/* 调整输入框组的样式 */
:deep(.el-input-group) {
  width: auto;
}

/* 调整表单项的布局 */
:deep(.el-form-item__content) {
  flex-wrap: wrap;
  gap: 8px;
}

/* 调整提示图标的位置 */
:deep(.el-tooltip) {
  margin-left: 4px;
  flex-shrink: 0;
}

/* 优化Referer列表的布局 */
.referer-list {
  width: 100%;
  max-width: 600px;
}

/* 调整卡片的最大宽度 */
.system-card {
  max-width: 1000px;
  margin: 0 auto;
}

/* 添加两列布局样式 */
.form-row {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}

.form-row > :deep(.el-form-item) {
  flex: 1;
  min-width: 300px;  /* 确保在空间不小时换行 */
  margin-bottom: 18px;
}

/* 调整开关类型的表单项宽度 */
.form-row .switch-item {
  min-width: 250px;
  flex: 0 1 auto;
}

/* 调整入框宽度 */
:deep(.medium-input) {
  width: 100%;  /* 让输入框填满表单项 */
  max-width: 400px;
}

:deep(.small-input) {
  width: 180px;
}

/* 全宽的表单项样式 */
.full-width-item {
  width: 100%;
}

/* 确保提示图标位置正确 */
:deep(.el-form-item__content) {
  justify-content: flex-start;
}

/* 禁用状态的表单项样式 */
:deep(.el-form-item.is-disabled) {
  opacity: 0.7;
}

:deep(.el-form-item.is-disabled .el-form-item__content) {
  cursor: not-allowed;
}

:deep(.el-form-item.is-disabled .el-input__wrapper) {
  background-color: var(--el-disabled-bg-color);
}

.header-actions {
  display: flex;
  gap: 12px;
}
</style> 