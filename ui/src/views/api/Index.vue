<template>
  <div class="api-container">
    <!-- Token管理 -->
    <el-card class="token-card">
      <template #header>
        <div class="card-header">
          <span>API Token</span>
          <el-button type="primary" @click="handleCreateToken">
            <el-icon><Plus /></el-icon>创建Token
          </el-button>
        </div>
      </template>
      
      <el-table :data="tokens" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="Token ID" width="100" />
        <el-table-column prop="tokenName" label="名称" />
        <el-table-column prop="token" label="Token" min-width="300">
          <template #default="{ row }">
            <div class="token-value">
              <span>{{ `${row.id}|${row.token}` }}</span>
              <div class="token-actions">
                <el-tooltip
                  :content="`${row.id}|${row.token}`"
                  placement="top"
                  effect="light"
                >
                  <el-button link type="primary" @click="copyToken(row)">
                    复制Token
                  </el-button>
                </el-tooltip>
                <el-tooltip
                  :content="`Bearer ${row.id}|${row.token}`"
                  placement="top"
                  effect="light"
                >
                  <el-button link type="primary" @click="copyAuthHeader(row)">
                    复制授权头
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="120">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              @change="(val) => handleStatusChange(row, val)"
              :loading="row.statusLoading"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button 
              link 
              type="danger" 
              @click="handleDeleteToken(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- API文档 -->
    <el-card class="doc-card">
      <template #header>
        <div class="card-header">
          <span>API文档</span>
        </div>
      </template>

      <!-- 认证方式部分 -->
      <div class="auth-section">
        <h3>认证方式</h3>
        <p>所有API请求都需要在Header中携带Token（点击复制按钮复制的Token已经自带Token Id）：</p>
        <el-card class="code-block auth-code">
          <pre>格式：
Authorization: Bearer {tokenId}|{token}

示例：
Authorization: Bearer 1|2Cq8wBbGQxLma9BuKQ3qDqAOEhsdUViuYViOizQl</pre>
        </el-card>
      </div>

      <!-- 分割线 -->
      <el-divider />

      <!-- API列表部分 -->
      <div class="api-list">
        <h3>API列表</h3>
        <template v-if="apiDocs">
          <div v-for="(pathData, path) in apiDocs" :key="path" class="api-item">
            <div v-for="(operation, method) in pathData" :key="method" class="operation-item">
              <div class="operation-header">
                <el-tag :type="getMethodType(method)">{{ method.toUpperCase() }}</el-tag>
                <span class="path">{{ path }}</span>
                <span v-if="operation.summary" class="summary">{{ operation.summary }}</span>
              </div>
              
              <div class="operation-content">
                <p v-if="operation.description" class="description">{{ operation.description }}</p>
                
                <!-- 参数表格 -->
                <template v-if="operation.parameters?.length">
                  <h4>请求参数</h4>
                  <el-table :data="operation.parameters" border>
                    <el-table-column prop="name" label="参数名称" width="180" />
                    <el-table-column prop="in" label="位置" width="100">
                      <template #default="{ row }">
                        <el-tag size="small">{{ row.in }}</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="required" label="必填" width="80">
                      <template #default="{ row }">
                        <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                          {{ row.required ? '是' : '否' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="description" label="说明" />
                  </el-table>
                </template>

                <!-- 请求体 -->
                <template v-if="operation.requestBody?.content">
                  <h4>请求体</h4>
                  <el-table 
                    :data="formatSchema(operation.requestBody.content['application/json']?.schema)" 
                    border
                  >
                    <el-table-column prop="name" label="字段" width="250">
                      <template #default="{ row }">
                        <span :style="{ paddingLeft: (row.name.split('.').length - 1) * 20 + 'px' }">
                          {{ row.name.split('.').pop() }}
                        </span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="type" label="类型" width="200" />
                    <el-table-column prop="required" label="必填" width="80">
                      <template #default="{ row }">
                        <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                          {{ row.required ? '是' : '否' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="description" label="说明" min-width="200" />
                    <el-table-column prop="example" label="示例值" min-width="150" />
                  </el-table>
                </template>

                <!-- 响应 -->
                <template v-if="operation.responses?.['200']?.content">
                  <h4>响应</h4>
                  <el-table 
                    :data="formatSchema(operation.responses['200'].content['*/*']?.schema)"
                    border
                  >
                    <el-table-column prop="name" label="字段" width="250">
                      <template #default="{ row }">
                        <span :style="{ paddingLeft: (row.name.split('.').length - 1) * 20 + 'px' }">
                          {{ row.name.split('.').pop() }}
                        </span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="type" label="类型" width="200" />
                    <el-table-column prop="required" label="必填" width="80">
                      <template #default="{ row }">
                        <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                          {{ row.required ? '是' : '否' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="description" label="说明" min-width="200" />
                    <el-table-column prop="example" label="示例值" min-width="150" />
                  </el-table>
                </template>
              </div>
            </div>
          </div>
        </template>
        <div v-else class="loading-state">
          <el-empty description="加载API文档中..." />
        </div>
      </div>
    </el-card>

    <!-- 创建Token对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="创建API Token"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="tokenName">
          <el-input v-model="form.tokenName" placeholder="请输入Token名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useClipboard } from '@vueuse/core'
import request from '@/utils/request'

export default {
  name: 'ApiDocs',
  components: {
    Plus
  },
  setup() {
    const { copy } = useClipboard()
    const tokens = ref([])
    const loading = ref(false)
    const dialogVisible = ref(false)
    const formRef = ref(null)
    const apiDocs = ref(null)

    const form = reactive({
      tokenName: ''
    })

    const rules = {
      tokenName: [
        { required: true, message: '请输入Token名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
      ]
    }

    // 获取Token列表
    const fetchTokens = async () => {
      loading.value = true
      try {
        const response = await request.get('/v1/tokens')
        console.log('API Response:', response)
        tokens.value = response.data.map(item => ({
          id: item.tokenId,
          token: item.token,
          tokenName: item.tokenName,
          enabled: item.enabled
        }))
        console.log('Processed tokens:', tokens.value)
      } catch (error) {
        console.error('Error fetching tokens:', error)
        ElMessage.error('获取Token列表失败')
      } finally {
        loading.value = false
      }
    }

    // 复制Token
    const copyToken = async (row) => {
      try {
        const fullToken = `${row.id}|${row.token}`
        await copy(fullToken)
        ElMessage.success('Token已复制到剪贴板')
      } catch (error) {
        ElMessage.error('复制失败')
      }
    }

    // 创建Token
    const handleCreateToken = () => {
      form.tokenName = ''
      dialogVisible.value = true
    }

    // 提交表单
    const submitForm = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        console.log('发送请求:', { tokenName: form.tokenName })
        
        const response = await request.post('/v1/tokens', null, {
          params: { tokenName: form.tokenName }
        })
        console.log('收到响应:', response)
        
        if (response.status) {
          ElMessage.success('Token创建成功')
          dialogVisible.value = false
          await fetchTokens()
        } else {
          ElMessage.error(response.message || 'Token创建失败')
        }
      } catch (error) {
        console.error('创建Token失败:', error)
        ElMessage.error(error.response?.message || 'Token创建失败')
      }
    }

    // 更新Token状态
    const handleStatusChange = async (token, status) => {
      try {
        token.statusLoading = true
        const response = await request.put(`/v1/tokens/${token.id}/status`, null, {
          params: { enabled: status }
        })
        if (response.status) {
          ElMessage.success('状态更新成功')
        }
      } catch (error) {
        token.enabled = !status
        ElMessage.error('状态更新失败')
      } finally {
        token.statusLoading = false
      }
    }

    // 删除Token
    const handleDeleteToken = (token) => {
      ElMessageBox.confirm(
        '确定要删除该Token吗？删除后将无法使用此Token访问API。',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        try {
          const response = await request.delete(`/v1/tokens/${token.id}`)
          if (response.status) {
            ElMessage.success('Token删除成功')
            fetchTokens()
          }
        } catch (error) {
          ElMessage.error('Token删除失败')
        }
      })
    }

    // API文档数据
    const uploadParams = [
      { name: 'file', type: 'File', required: true, description: '图片文件' },
      { name: 'isPublic', type: 'Boolean', required: false, description: '是否公开，默认false' },
      { name: 'albumId', type: 'Number', required: false, description: '所属相册ID' }
    ]

    const uploadResponse = JSON.stringify({
      status: true,
      message: "success",
      data: {
        key: "abcd1234",
        name: "example.jpg",
        url: "https://example.com/image.jpg",
        thumbnailUrl: "https://example.com/thumb.jpg"
      }
    }, null, 2)

    // 复制Authorization标头
    const copyAuthHeader = async (token) => {
      try {
        const authHeader = `Bearer ${token.id}|${token.token}`
        await copy(authHeader)
        ElMessage.success('Authorization标头已复制到剪贴板')
      } catch (error) {
        ElMessage.error('复制失败')
      }
    }

    const processApiDocs = (docs) => {
      const schemas = docs.components?.schemas || {}
      
      // 解析$ref引用
      const resolveRef = (ref) => {
        if (!ref) return null
        const schemaName = ref.split('/').pop()
        return schemas[schemaName]
      }

      // 处理schema对象
      const processSchema = (schema) => {
        if (!schema) return null
        
        // 处理文件上传类型
        if (schema.type === 'object' && schema.properties?.file?.format === 'binary') {
          return {
            type: 'object',
            title: 'FileUpload',
            properties: {
              file: {
                type: 'file',
                format: 'binary',
                required: (schema.required || []).includes('file'),
                description: '要上传的文件'
              }
            }
          }
        }
        
        if (schema.$ref) {
          const refSchema = resolveRef(schema.$ref)
          return {
            type: 'object',
            title: schema.$ref.split('/').pop(),
            description: refSchema?.description || '',
            properties: Object.entries(refSchema?.properties || {}).reduce((acc, [key, prop]) => {
              acc[key] = {
                type: prop.type || 'object',
                description: prop.description || '',
                format: prop.format,
                required: (refSchema?.required || []).includes(key),
                example: prop.example,
                enum: prop.enum,
                $ref: prop.$ref
              }
              if (prop.$ref) {
                const nestedSchema = processSchema(prop)
                acc[key].refSchema = nestedSchema
              }
              return acc
            }, {})
          }
        }
        
        // 直接处理普通 schema
        if (schema.type === 'object' && schema.properties) {
          return {
            type: 'object',
            title: schema.title || 'Object',
            description: schema.description || '',
            properties: Object.entries(schema.properties).reduce((acc, [key, prop]) => {
              acc[key] = {
                type: prop.type || 'object',
                description: prop.description || '',
                format: prop.format,
                required: (schema.required || []).includes(key),
                example: prop.example,
                enum: prop.enum,
                $ref: prop.$ref
              }
              if (prop.$ref) {
                const nestedSchema = processSchema(prop)
                acc[key].refSchema = nestedSchema
              }
              return acc
            }, {})
          }
        }
        
        return schema
      }

      // 只保留需要的字段,简化文档结构
      const paths = docs.paths || {}
      const processed = {}
      
      for (const [path, methods] of Object.entries(paths)) {
        processed[path] = {}
        
        for (const [method, operation] of Object.entries(methods)) {
          if (!operation) continue
          
          processed[path][method] = {
            summary: operation.summary || '',
            description: operation.description || '',
            parameters: (operation.parameters || []).map(p => ({
              name: p.name,
              in: p.in,
              required: p.required,
              description: p.description,
              schema: processSchema(p.schema)
            })),
            requestBody: operation.requestBody ? {
              required: operation.requestBody.required,
              content: {
                'application/json': {
                  schema: processSchema(operation.requestBody.content['application/json']?.schema)
                }
              }
            } : null,
            responses: operation.responses ? {
              '200': {
                content: {
                  '*/*': {
                    schema: processSchema(operation.responses['200']?.content?.['*/*']?.schema)
                  }
                }
              }
            } : null
          }
        }
      }
      
      return processed
    }

    // 在 fetchApiDocs 中使用
    const fetchApiDocs = async () => {
      try {
        const response = await request.get('/v1/tokens/docs')
        apiDocs.value = processApiDocs(response.data)
        console.log('Processed API Docs:', apiDocs.value)
      } catch (error) {
        console.error('获取API文档失败:', error)
        apiDocs.value = null
      }
    }

    // HTTP方法对应的标签类型
    const getMethodType = (method) => {
      const types = {
        get: 'success',
        post: 'primary',
        put: 'warning',
        delete: 'danger'
      }
      return types[method.toLowerCase()] || 'info'
    }

    // 添加一个新的函数来扁平化schema
    const flattenSchema = (schema, parentKey = '', required = false) => {
      const rows = []
      
      if (!schema) return rows
      
      if (schema.title && schema.properties) {
        Object.entries(schema.properties).forEach(([key, prop]) => {
          const fullKey = parentKey ? `${parentKey}.${key}` : key
          
          // 特殊处理文件类型
          if (prop.type === 'file' && prop.format === 'binary') {
            rows.push({
              name: fullKey,
              type: 'file',
              required: prop.required,
              description: prop.description || '文件上传',
              example: '文件对象'
            })
            return
          }
          
          if (prop.refSchema) {
            // 递归处理嵌套对象
            rows.push({
              name: fullKey,
              type: `object (${prop.refSchema.title})`,
              required: prop.required,
              description: prop.description,
              example: prop.example
            })
            rows.push(...flattenSchema(prop.refSchema, fullKey, prop.required))
          } else {
            // 处理基本类型
            let type = prop.type
            if (prop.format) {
              type += `(${prop.format})`
            }
            if (prop.enum) {
              type += ` enum(${prop.enum.join('|')})`
            }
            
            rows.push({
              name: fullKey,
              type,
              required: prop.required,
              description: prop.description,
              example: prop.example
            })
          }
        })
      }
      
      return rows
    }

    // 修改formatSchema函数
    const formatSchema = (schema) => {
      try {
        if (!schema) return null
        
        // 转换成表格数据
        const rows = flattenSchema(schema)
        return rows
      } catch (error) {
        console.error('格式化Schema失败:', error, schema)
        return []
      }
    }

    onMounted(() => {
      fetchTokens()
      fetchApiDocs()
    })

    return {
      tokens,
      loading,
      dialogVisible,
      formRef,
      form,
      rules,
      uploadParams,
      uploadResponse,
      handleCreateToken,
      handleStatusChange,
      handleDeleteToken,
      submitForm,
      copyToken,
      copyAuthHeader,
      fetchApiDocs,
      apiDocs,
      getMethodType,
      formatSchema
    }
  }
}
</script>

<style scoped>
.api-container {
  padding: 20px;
}

.token-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.token-value {
  display: flex;
  align-items: center;
  gap: 8px;
}

.token-actions {
  display: flex;
  gap: 4px;
}

.doc-content {
  padding: 20px 0;
}

.doc-content h3 {
  margin-top: 0;
  margin-bottom: 16px;
}

.doc-content h4 {
  margin: 24px 0 16px;
}

.code-block {
  background-color: #f5f7fa;
  margin: 16px 0;
}

.code-block pre {
  margin: 0;
  padding: 16px;
  font-family: monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.api-list {
  padding: 20px 0;
}

.api-item {
  margin-bottom: 32px;
}

.operation-item {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  margin-bottom: 16px;
}

.operation-header {
  padding: 12px;
  background-color: var(--el-fill-color-light);
  border-bottom: 1px solid var(--el-border-color);
  display: flex;
  align-items: center;
  gap: 12px;
}

.path {
  font-family: monospace;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.summary {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.operation-content {
  padding: 16px;
}

.description {
  color: var(--el-text-color-secondary);
  margin: 0 0 16px;
}

.code-block {
  margin: 16px 0;
  background-color: var(--el-fill-color-light);
}

.code-block pre {
  margin: 0;
  padding: 16px;
  font-family: monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
}

h4 {
  margin: 24px 0 16px;
  font-size: 16px;
  font-weight: 500;
}

.auth-section {
  padding: 0 0 20px;
}

.auth-section h3 {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 500;
}

.auth-section p {
  margin: 0 0 12px;
  color: var(--el-text-color-secondary);
}

.auth-section .code-block {
  width: 100%;
}

.auth-section .auth-code {
  margin: 0;
}

.auth-section .auth-code pre {
  color: var(--el-text-color-primary);
}

.api-list h3 {
  margin: 0 0 24px;
  font-size: 18px;
  font-weight: 500;
}

.el-divider {
  margin: 0 0 24px;
}

.code-block.auth-code {
  background-color: #e6f7ff;
  border-color: #91d5ff;
}

.code-block.auth-code pre {
  color: #0050b3;
}
</style> 