<template>
  <div class="storage-container">
    <!-- 顶部操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>添加策略
      </el-button>
    </div>

    <!-- 策略列表 -->
    <el-table :data="storageList" style="width: 100%" v-loading="loading">
      <el-table-column prop="strategyName" label="策略名称" />
      <el-table-column prop="strategyType" label="存储类型">
        <template #default="{ row }">
          <el-tag>{{ row.strategyType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="row.available ? 'success' : 'danger'">
            {{ row.available ? '可用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="容量">
        <template #default="{ row }">
          <div>
            <div>已用：{{ formatSize(row.usedCapacity) }}</div>
            <div>总量：{{ formatSize(row.maxCapacity) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button-group>
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              link 
              type="primary" 
              @click="handleTest(row)"
              :loading="row.testing"
            >
              测试
            </el-button>
            <el-button 
              link 
              type="danger" 
              @click="handleDelete(row)"
              :disabled="row.isDefault"
            >
              删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑策略对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑策略' : '添加策略'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="策略名称" prop="strategyName">
          <el-input v-model="form.strategyName" placeholder="请输入策略名称" />
        </el-form-item>
        
        <el-form-item label="存储类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择存储类型" style="width: 100%">
            <el-option label="本地存储" value="LOCAL" />
            <el-option label="FTP" value="FTP" />
            <el-option label="SFTP" value="SFTP" />
            <el-option label="WebDAV" value="WEBDAV" />
            <el-option label="Amazon S3" value="S3" />
            <el-option label="MinIO" value="MINIO" />
            <el-option label="阿里云 OSS" value="ALIYUN_OSS" />
            <el-option label="华为云 OBS" value="HUAWEI_OBS" />
            <el-option label="腾讯云 COS" value="TENCENT_COS" />
            <el-option label="百度云 BOS" value="BAIDU_BOS" />
            <el-option label="又拍云 USS" value="USS" />
            <el-option label="七牛云 Kodo" value="KODO" />
            <el-option label="Google Cloud Storage" value="GOOGLE_CLOUD_STORAGE" />
            <el-option label="FastDFS" value="FASTDFS" />
            <el-option label="Azure Blob Storage" value="AZURE_BLOB_STORAGE" />
            <el-option label="其他兼容S3的云存储" value="OTHER_S3_COMPATIBLE" />
          </el-select>
        </el-form-item>

        <!-- 基础路径（所有类型都需要） -->
        <el-form-item label="基础路径" prop="basePath">
          <el-input v-model="form.basePath" placeholder="请输入基础路径" />
        </el-form-item>

        <!-- 本地存储配置 -->
        <template v-if="form.type === 'LOCAL'">
          <el-form-item label="存储路径" prop="storagePath">
            <el-input v-model="form.storagePath" placeholder="请输入存储路径" />
          </el-form-item>
        </template>

        <!-- 对象存储通用配置 -->
        <template v-else-if="[
          'ALIYUN_OSS', 
          'HUAWEI_OBS', 
          'KODO', 
          'TENCENT_COS', 
          'BAIDU_BOS', 
          'MINIO', 
          'S3',
          'GOOGLE_CLOUD_STORAGE',
          'AZURE_BLOB_STORAGE',
          'OTHER_S3_COMPATIBLE'
        ].includes(form.type)">
          <el-form-item label="AccessKey" prop="accessKeyId">
            <el-input v-model="form.accessKeyId" placeholder="请输入AccessKey" />
          </el-form-item>
          <el-form-item label="SecretKey" prop="accessKeySecret">
            <el-input v-model="form.accessKeySecret" type="password" placeholder="请输入SecretKey" show-password />
          </el-form-item>
          <el-form-item label="Bucket" prop="bucketName">
            <el-input v-model="form.bucketName" placeholder="请输入Bucket名称" />
          </el-form-item>
          <el-form-item label="Endpoint" prop="endpoint" 
            v-if="['ALIYUN_OSS', 'HUAWEI_OBS', 'BAIDU_BOS', 'MINIO'].includes(form.type)">
            <el-input v-model="form.endpoint" placeholder="请输入Endpoint" />
          </el-form-item>
          <el-form-item label="Region" prop="region" 
            v-if="['TENCENT_COS', 'AMAZON_S3', 'GOOGLE_CLOUD_STORAGE'].includes(form.type)">
            <el-input v-model="form.region" placeholder="请输入区域" />
          </el-form-item>
          <!-- Google Cloud Storage 特有配置 -->
          <template v-if="form.type === 'GOOGLE_CLOUD_STORAGE'">
            <el-form-item label="项目ID" prop="projectId">
              <el-input v-model="form.projectId" placeholder="请输入项目ID" />
            </el-form-item>
            <el-form-item label="凭证文件路径" prop="credentialsPath">
              <el-input v-model="form.credentialsPath" placeholder="请输入凭证文件路径" />
            </el-form-item>
          </template>
          <!-- Azure Blob Storage 特有配置 -->
          <template v-if="form.type === 'AZURE_BLOB_STORAGE'">
            <el-form-item label="连接字符串" prop="connectionString">
              <el-input v-model="form.connectionString" type="password" placeholder="请输入连接字符串" show-password />
            </el-form-item>
            <el-form-item label="容器名称" prop="containerName">
              <el-input v-model="form.containerName" placeholder="请输入容器名称" />
            </el-form-item>
          </template>
        </template>

        <!-- FTP/SFTP配置 -->
        <template v-else-if="['FTP', 'SFTP'].includes(form.type)">
          <el-form-item label="主机地址" prop="host">
            <el-input v-model="form.host" placeholder="请输入主机地址" />
          </el-form-item>
          <el-form-item label="端口" prop="port">
            <el-input-number v-model="form.port" :min="1" :max="65535" />
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="私钥路径" prop="privateKeyPath" v-if="form.type === 'SFTP'">
            <el-input v-model="form.privateKeyPath" placeholder="请输入私钥路径（可��" />
          </el-form-item>
        </template>

        <!-- WebDAV配置 -->
        <template v-else-if="form.type === 'WEBDAV'">
          <el-form-item label="服务器地址" prop="server">
            <el-input v-model="form.server" placeholder="请输入WebDAV服务器地址" />
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
        </template>

        <!-- 又拍云配置 -->
        <template v-else-if="form.type === 'USS'">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="Bucket" prop="bucketName">
            <el-input v-model="form.bucketName" placeholder="请输入Bucket名称" />
          </el-form-item>
        </template>

        <!-- FastDFS配置 -->
        <template v-else-if="form.type === 'FASTDFS'">
          <el-form-item label="Tracker服务器" prop="trackerServerHost">
            <el-input v-model="form.trackerServerHost" placeholder="请输入Tracker服务器地址" />
          </el-form-item>
          <el-form-item label="HTTP端口" prop="trackerServerHttpPort">
            <el-input-number v-model="form.trackerServerHttpPort" :min="1" :max="65535" />
          </el-form-item>
        </template>

        <el-form-item label="最大容量" prop="maxCapacity">
          <div class="capacity-input">
            <el-input-number 
              v-model="capacityValue" 
              :min="0" 
              :precision="2"
              placeholder="请输入最大容量"
              @change="handleCapacityChange"
            />
            <el-select v-model="capacityUnit" @change="handleCapacityChange" style="width: 80px">
              <el-option label="KB" value="KB" />
              <el-option label="MB" value="MB" />
              <el-option label="GB" value="GB" />
            </el-select>
          </div>
        </el-form-item>

        <el-form-item label="排序顺序" prop="sortOrder">
          <el-input-number 
            v-model="form.sortOrder" 
            :min="0" 
            placeholder="数字越小越靠前"
          />
        </el-form-item>

        <el-form-item label="是否可用" prop="available">
          <el-switch v-model="form.available" />
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
import { Plus, FolderOpened } from '@element-plus/icons-vue'
import { strategy } from '@/api/index'

export default {
  name: 'StorageManagement',
  components: {
    Plus,
    FolderOpened
  },
  setup() {
    const loading = ref(false)
    const storageList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref(null)

    const form = reactive({
      id: null,
      strategyName: '',
      type: '',
      maxCapacity: 0,
      usedCapacity: 0,
      available: true,
      sortOrder: 0,
      storagePath: '',
      basePath: '',
      accessKeyId: '',
      accessKeySecret: '',
      bucketName: '',
      endpoint: '',
      region: '',
      host: '',
      port: 21,
      username: '',
      password: '',
      privateKeyPath: '',
      server: ''
    })

    const rules = {
      strategyName: [
        { required: true, message: '请输入策略名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择存储类型', trigger: 'change' }
      ],
      basePath: [
        { required: true, message: '请输入基础路径', trigger: 'blur' },
        { 
          validator: (rule, value, callback) => {
            if (!value) {
              callback()
              return
            }
            if (!value.endsWith('/')) {
              callback(new Error('基础路径必须以/结尾'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }
      ],
      storagePath: [
        { required: true, message: '请输入存储路径', trigger: 'blur' },
        { 
          validator: (rule, value, callback) => {
            if (!value) {
              callback()
              return
            }
            if (!value.endsWith('/')) {
              callback(new Error('存储路径必须以/结尾'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }
      ],
      maxCapacity: [
        { required: true, message: '请输入最大容量', trigger: 'blur' }
      ],
      sortOrder: [
        { required: true, message: '请输入排序顺序', trigger: 'blur' }
      ]
    }

    // 获取策略列表
    const fetchStorageList = async () => {
      loading.value = true
      try {
        const { data } = await strategy.getList()
        if (data.success) {
          storageList.value = data.data
        } else {
          ElMessage.error(data.message || '获取存储策略列表失败')
        }
      } catch (error) {
        console.error('获取存储策略列表失败:', error)
        ElMessage.error('获取存储策略列表失败')
      } finally {
        loading.value = false
      }
    }

    // 添加策略
    const handleCreate = () => {
      isEdit.value = false
      Object.assign(form, {
        id: null,
        strategyName: '',
        type: '',
        maxCapacity: 0,
        usedCapacity: 0,
        available: true,
        sortOrder: 0,
        storagePath: '',
        basePath: '',
        accessKeyId: '',
        accessKeySecret: '',
        bucketName: '',
        endpoint: '',
        region: '',
        host: '',
        port: 21,
        username: '',
        password: '',
        privateKeyPath: '',
        server: ''
      })
      capacityValue.value = 0
      capacityUnit.value = 'MB'
      dialogVisible.value = true
    }

    // 编辑策略
    const handleEdit = (row) => {
      isEdit.value = true
      form.id = row.id
      form.strategyName = row.strategyName
      form.type = row.strategyType
      form.maxCapacity = Number(row.maxCapacity)
      form.usedCapacity = Number(row.usedCapacity)
      form.available = row.available
      form.sortOrder = Number(row.sortOrder)

      // 解析 configJson
      const config = row.configJson
      switch (row.strategyType) {
        case 'LOCAL':
          form.storagePath = config['storage-path']
          form.basePath = config['base-path']
          break
        case 'FTP':
          form.host = config['host']
          form.port = parseInt(config['port'])
          form.username = config['username']
          form.password = config['password']
          form.basePath = config['base-path']
          break
        case 'SFTP':
          form.host = config['host']
          form.port = parseInt(config['port'])
          form.username = config['username']
          form.password = config['password']
          form.privateKeyPath = config['private-key-path']
          form.basePath = config['base-path']
          break
        case 'WEBDAV':
          form.server = config['server']
          form.username = config['username']
          form.password = config['password']
          form.basePath = config['base-path']
          break
        case 'TENCENT_COS':
          form.accessKeyId = config['secret-id']
          form.accessKeySecret = config['secret-key']
          form.region = config['region']
          form.bucketName = config['bucket-name']
          form.basePath = config['base-path']
          break
        case 'ALIYUN_OSS':
        case 'MINIO':
        case 'HUAWEI_OBS':
        case 'BAIDU_BOS':
          form.accessKeyId = config['access-key-id']
          form.accessKeySecret = config['access-key-secret']
          form.endpoint = config['endpoint']
          form.bucketName = config['bucket-name']
          form.basePath = config['base-path']
          break
        case 'S3':
        case 'OTHER_S3_COMPATIBLE':
          form.accessKeyId = config['access-key-id']
          form.accessKeySecret = config['access-key-secret']
          form.region = config['region']
          form.bucketName = config['bucket-name']
          form.basePath = config['base-path']
          break
        case 'USS':
          form.username = config['username']
          form.password = config['password']
          form.bucketName = config['bucket-name']
          form.basePath = config['base-path']
          break
        case 'KODO':
          form.accessKeyId = config['access-key-id']
          form.accessKeySecret = config['access-key-secret']
          form.bucketName = config['bucket-name']
          form.basePath = config['base-path']
          break
        case 'GOOGLE_CLOUD_STORAGE':
          form.projectId = config['project-id']
          form.credentialsPath = config['credential-file-path']
          form.bucketName = config['bucket-name']
          form.basePath = config['base-path']
          break
        case 'FASTDFS':
          form.trackerServerHost = config['tracker-server-host']
          form.trackerServerHttpPort = parseInt(config['tracker-server-http-port'])
          form.basePath = config['base-path']
          break
        case 'AZURE_BLOB_STORAGE':
          form.connectionString = config['connection-string']
          form.containerName = config['container-name']
          form.basePath = config['base-path']
          break
      }
      
      // 处理容量显示
      if (form.maxCapacity >= 1024 * 1024) {
        capacityValue.value = Number((form.maxCapacity / (1024 * 1024)).toFixed(2))
        capacityUnit.value = 'GB'
      } else if (form.maxCapacity >= 1024) {
        capacityValue.value = Number((form.maxCapacity / 1024).toFixed(2))
        capacityUnit.value = 'MB'
      } else {
        capacityValue.value = Number(form.maxCapacity)
        capacityUnit.value = 'KB'
      }

      dialogVisible.value = true
    }

    // 测试策略
    const handleTest = async (row) => {
      try {
        // 添加加载状态
        row.testing = true
        await strategy.test(row.id)
        ElMessage.success('测试成功，存储策略可用')
        // 测试成功后立即更新列表
        await fetchStorageList()
      } catch (error) {
        ElMessage.error('测试失败：' + (error.response?.data?.message || '存储策略不可用'))
      } finally {
        // 清除加载状态
        row.testing = false
      }
    }

    // 删除策略
    const handleDelete = (row) => {
      ElMessageBox.confirm(
        '确定要删除该存储策略？',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        try {
          // 调用删除接口
          await strategy.delete(row.id)
          ElMessage.success('删除成功')
          fetchStorageList()
        } catch (error) {
          ElMessage.error(error.response?.data?.message || '删除失败')
        }
      })
    }

    // 设置默认策略
    const handleDefaultChange = async (row, value) => {
      try {
        // TODO: 调用设置默认接口
        ElMessage.success('设置成功')
        fetchStorageList()
      } catch (error) {
        row.isDefault = !value
        ElMessage.error('设置失败')
      }
    }

    // 转换表单数据为API所需格式
    const convertFormToApiData = (formData) => {
      // 根据存储类型构建不同的配置
      let configJson = {}
      
      switch (formData.type) {
        case 'LOCAL':
          configJson = {
            'platform-name': formData.strategyName,
            'storage-path': formData.storagePath,
            'base-path': formData.basePath
          }
          break

        case 'ALIYUN_OSS':
          configJson = {
            'platform-name': formData.strategyName,
            'access-key-id': formData.accessKeyId,
            'access-key-secret': formData.accessKeySecret,
            'endpoint': formData.endpoint,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'TENCENT_COS':
          configJson = {
            'platform-name': formData.strategyName,
            'secret-id': formData.accessKeyId,
            'secret-key': formData.accessKeySecret,
            'region': formData.region,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'KODO':
          configJson = {
            'platform-name': formData.strategyName,
            'access-key-id': formData.accessKeyId,
            'access-key-secret': formData.accessKeySecret,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'MINIO':
          configJson = {
            'platform-name': formData.strategyName,
            'access-key-id': formData.accessKeyId,
            'access-key-secret': formData.accessKeySecret,
            'endpoint': formData.endpoint,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'FTP':
          configJson = {
            'platform-name': formData.strategyName,
            'host': formData.host,
            'port': formData.port,
            'username': formData.username,
            'password': formData.password,
            'base-path': formData.basePath
          }
          break

        case 'SFTP':
          configJson = {
            'platform-name': formData.strategyName,
            'host': formData.host,
            'port': formData.port,
            'username': formData.username,
            'password': formData.password,
            'private-key-path': formData.privateKeyPath,
            'base-path': formData.basePath
          }
          break

        case 'WEBDAV':
          configJson = {
            'platform-name': formData.strategyName,
            'server': formData.server,
            'username': formData.username,
            'password': formData.password,
            'base-path': formData.basePath
          }
          break

        case 'HUAWEI_OBS':
          configJson = {
            'platform-name': formData.strategyName,
            'access-key-id': formData.accessKeyId,
            'access-key-secret': formData.accessKeySecret,
            'endpoint': formData.endpoint,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'BAIDU_BOS':
          configJson = {
            'platform-name': formData.strategyName,
            'access-key-id': formData.accessKeyId,
            'access-key-secret': formData.accessKeySecret,
            'endpoint': formData.endpoint,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'USS':
          configJson = {
            'platform-name': formData.strategyName,
            'username': formData.username,
            'password': formData.password,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'S3':
        case 'OTHER_S3_COMPATIBLE':
          configJson = {
            'platform-name': formData.strategyName,
            'access-key-id': formData.accessKeyId,
            'access-key-secret': formData.accessKeySecret,
            'region': formData.region,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'GOOGLE_CLOUD_STORAGE':
          configJson = {
            'platform-name': formData.strategyName,
            'project-id': formData.projectId,
            'credential-file-path': formData.credentialsPath,
            'bucket-name': formData.bucketName,
            'base-path': formData.basePath
          }
          break

        case 'FASTDFS':
          configJson = {
            'platform-name': formData.strategyName,
            'tracker-server-host': formData.trackerServerHost,
            'tracker-server-http-port': formData.trackerServerHttpPort,
            'base-path': formData.basePath
          }
          break

        case 'AZURE_BLOB_STORAGE':
          configJson = {
            'platform-name': formData.strategyName,
            'connection-string': formData.connectionString,
            'container-name': formData.containerName,
            'base-path': formData.basePath
          }
          break
      }

      return {
        id: formData.id || 0,
        strategyType: formData.type,
        strategyName: formData.strategyName,
        configJson: configJson,
        maxCapacity: formData.maxCapacity,
        usedCapacity: formData.usedCapacity,
        available: formData.available,
        sortOrder: formData.sortOrder
      }
    }

    // 提交表单
    const submitForm = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        const apiData = convertFormToApiData(form)
        
        if (isEdit.value) {
          await strategy.update(form.id, apiData)
          ElMessage.success('更新成功')
        } else {
          await strategy.create(apiData)
          ElMessage.success('创建成功')
        }
        
        dialogVisible.value = false
        fetchStorageList()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }

    // 格式化容量显示
    const formatSize = (kb) => {
      if (!kb) return '0 KB'
      if (kb < 1024) return kb.toFixed(2) + ' KB'
      if (kb < 1024 * 1024) return (kb / 1024).toFixed(2) + ' MB'
      return (kb / (1024 * 1024)).toFixed(2) + ' GB'
    }

    // 添加容量相关的响应式变量
    const capacityValue = ref(0)
    const capacityUnit = ref('MB')

    // 处理容量变化
    const handleCapacityChange = () => {
      // 转换为KB，确保都是数字类型
      switch (capacityUnit.value) {
        case 'GB':
          form.maxCapacity = Number(capacityValue.value) * 1024 * 1024
          break
        case 'MB':
          form.maxCapacity = Number(capacityValue.value) * 1024
          break
        case 'KB':
        default:
          form.maxCapacity = Number(capacityValue.value)
      }
    }

    onMounted(() => {
      fetchStorageList()
    })

    return {
      loading,
      storageList,
      dialogVisible,
      isEdit,
      formRef,
      form,
      rules,
      handleCreate,
      handleEdit,
      handleTest,
      handleDelete,
      handleDefaultChange,
      submitForm,
      convertFormToApiData,
      formatSize,
      capacityValue,
      capacityUnit,
      handleCapacityChange
    }
  }
}
</script>

<style scoped>
.storage-container {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.capacity-input {
  display: flex;
  gap: 8px;
  align-items: center;
}

</style>