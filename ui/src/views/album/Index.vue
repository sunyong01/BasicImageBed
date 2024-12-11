<template>
  <div class="album-container">
    <!-- 顶部操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleCreate">
        创建相册
      </el-button>
      
      <!-- 添加搜索和筛选按钮 -->
      <div class="search-wrapper">
        <el-input
          v-model="searchQuery"
          placeholder="搜索相册"
          class="search-input"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <el-button @click="filterDrawer = true">
        <el-icon><Filter /></el-icon>筛选
      </el-button>
    </div>

    <!-- 相册列表 -->
    <el-row :gutter="20" class="album-list">
      <el-col :span="6" v-for="album in albums" :key="album.id">
        <el-card class="album-card" :body-style="{ padding: '0px' }" @click="navigateToImages(album)">
          <div class="album-cover">
            <el-image 
              :src="album.cover_url || defaultCover" 
              class="image"
              fit="cover"
              loading="lazy"
            >
              <template #placeholder>
                <div class="image-placeholder">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <span>加载中...</span>
                </div>
              </template>
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
            <!-- 添加遮罩层 -->
            <div class="cover-overlay">
              <div class="overlay-content">
                <p class="album-desc">{{ album.description || '暂无描述' }}</p>
                <p class="album-time">创建时间：{{ formatDate(album.createTime) }}</p>
                <p class="album-time">更新时间：{{ formatDate(album.updateTime) }}</p>
              </div>
            </div>
          </div>
          <div class="album-info">
            <h3>{{ album.name }}</h3>
            <p>{{ album.description }}</p>
            <div class="album-footer">
              <div class="album-stats">
                <span>{{ album.image_num }}张照片</span>
                <el-tag 
                  size="small" 
                  :type="album.isPublic ? 'success' : 'info'"
                  class="visibility-tag"
                >
                  {{ album.isPublic ? '公开' : '私有' }}
                </el-tag>
              </div>
              <div class="operations">
                <el-button link @click.stop="handleEdit(album)">编辑</el-button>
                <el-button link type="danger" @click.stop="handleDelete(album)">删除</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 创建/编辑相册对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="albumFormRef"
        :model="albumForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="相册名称" prop="name">
          <el-input v-model="albumForm.name" placeholder="请输入相册名称" />
        </el-form-item>

        <el-form-item label="相册描述" prop="description">
          <el-input
            v-model="albumForm.description"
            type="textarea"
            placeholder="请输入相册描述"
          />
        </el-form-item>

        <el-form-item label="是否公开" prop="isPublic">
          <el-switch v-model="albumForm.isPublic" />
        </el-form-item>

        <el-form-item label="封面图片" prop="coverUrl">
          <el-input v-model="albumForm.coverUrl" placeholder="请输入封面图片URL">
            <template #append>
              <el-tooltip content="可以先上传图片，然后复制图片URL到此处" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加筛选抽屉 -->
    <el-drawer
      v-model="filterDrawer"
      title="相册筛选"
      direction="rtl"
      size="380px"
    >
      <el-form
        ref="filterForm"
        :model="filterCondition"
        label-width="80px"
        class="filter-form"
      >
        <el-form-item label="相册名称">
          <el-input v-model="filterCondition.name" placeholder="请输入相册名称" clearable />
        </el-form-item>

        <el-form-item label="用户名" v-if="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')">
          <el-input 
            v-model="filterCondition.username" 
            placeholder="请输入用户名" 
            clearable 
          />
        </el-form-item>

        <el-form-item label="访问权限">
          <el-select v-model="filterCondition.is_public" placeholder="请选择" clearable>
            <el-option label="公开" :value="true" />
            <el-option label="私有" :value="false" />
          </el-select>
        </el-form-item>

        <el-form-item label="排序">
          <el-select v-model="sortOrder" placeholder="请选择" clearable>
            <el-option label="创建时间降序" value="create_time_desc" />
            <el-option label="创建时间升序" value="create_time_asc" />
            <el-option label="更新时间降序" value="update_time_desc" />
            <el-option label="更新时间升序" value="update_time_asc" />
            <el-option label="图片数量降序" value="image_num_desc" />
            <el-option label="图片数量升序" value="image_num_asc" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="filter-footer">
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" @click="applyFilter">应用筛选</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Filter, 
  Loading, 
  Picture,
  InfoFilled
} from '@element-plus/icons-vue'
import { album, hasAnyRole } from '@/api'
import { useRouter } from 'vue-router'

export default {
  name: 'AlbumIndex',
  components: {
    Search,
    Filter,
    Loading,
    Picture,
    InfoFilled
  },
  setup() {
    const albumFormRef = ref(null)
    const albums = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const isEdit = ref(false)
    const defaultCover = '/imgs/default.jpg'
    const router = useRouter()

    const albumForm = reactive({
      id: null,
      name: '',
      description: '',
      isPublic: false,
      coverUrl: ''
    })

    const rules = {
      name: [
        { required: true, message: '请输入相册名称', trigger: 'blur' },
        { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
      ],
      description: [
        { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
      ],
      coverUrl: [
        { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
      ]
    }

    // 添加筛选相关的变量
    const filterDrawer = ref(false)
    const searchQuery = ref('')
    const filterCondition = reactive({
      name: '',
      username: '',
      is_public: null,
    })
    const sortOrder = ref('')

    // 获取相册列表
    const fetchAlbums = async () => {
      try {
        const condition = {
          ...filterCondition,
          name: searchQuery.value || filterCondition.name
        }

        const { data } = await album.search(condition, 1, 20, sortOrder.value)
        if (data.success) {
          albums.value = (data.data.data || []).map(album => ({
            ...album,
            description: album.intro,
            createTime: album.create_time,
            updateTime: album.update_time,
            image_num: album.image_num || 0,
            coverUrl: album.cover_url || defaultCover,
            isPublic: album.is_public
          }))
        } else {
          ElMessage.error(data.message || '获取相册列表失败')
        }
      } catch (error) {
        ElMessage.error('获取相册列表失败')
      }
    }

    // 创建相册
    const handleCreate = () => {
      isEdit.value = false
      dialogTitle.value = '创建相册'
      albumForm.id = null
      albumForm.name = ''
      albumForm.description = ''
      albumForm.isPublic = false
      albumForm.coverUrl = ''
      dialogVisible.value = true
    }

    // 编辑相册
    const handleEdit = (album) => {
      isEdit.value = true
      dialogTitle.value = '编辑相册'
      albumForm.id = album.id
      albumForm.name = album.name
      albumForm.description = album.description
      albumForm.isPublic = album.isPublic
      albumForm.coverUrl = album.cover_url || ''
      dialogVisible.value = true
    }

    // 删除相册
    const handleDelete = (album) => {
      ElMessageBox.confirm(
        '确定要删除这个相册吗？',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        try {
          await album.delete(album.id)
          ElMessage.success('删除成功')
          fetchAlbums()
        } catch (error) {
          ElMessage.error('删除失败')
        }
      })
    }

    // 提交表单
    const submitForm = async () => {
      try {
        await albumFormRef.value.validate()
        
        const submitData = {
          name: albumForm.name,
          description: albumForm.description,
          isPublic: albumForm.isPublic,
          coverUrl: albumForm.coverUrl
        }

        if (isEdit.value) {
          await album.update(albumForm.id, submitData)
          ElMessage.success('更新成功')
        } else {
          await album.create(submitData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchAlbums()
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message)
        } else {
          ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
        }
      }
    }

    // 添加搜索处理方法
    const handleSearch = () => {
      fetchAlbums()
    }

    // 添加重置筛选方法
    const resetFilter = () => {
      searchQuery.value = ''
      Object.keys(filterCondition).forEach(key => {
        filterCondition[key] = null
      })
      sortOrder.value = ''
      fetchAlbums()
    }

    // 添加应用筛选方法
    const applyFilter = () => {
      fetchAlbums()
      filterDrawer.value = false
    }

    // 添加导航到图片管理的方法
    const navigateToImages = (album) => {
      router.push({
        path: '/images',
        query: {
          album_id: album.id,
          album_name: album.name
        }
      })
    }

    const formatDate = (date) => {
      if (!date) return ''
      return new Date(date).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    onMounted(() => {
      fetchAlbums()
    })

    return {
      albums,
      dialogVisible,
      dialogTitle,
      albumForm,
      albumFormRef,
      rules,
      defaultCover,
      handleCreate,
      handleEdit,
      handleDelete,
      submitForm,
      filterDrawer,
      searchQuery,
      filterCondition,
      sortOrder,
      handleSearch,
      resetFilter,
      applyFilter,
      navigateToImages,
      formatDate,
      hasAnyRole
    }
  }
}
</script>

<style scoped>
.album-container {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.album-list {
  margin-bottom: 20px;
}

.album-card {
  margin-bottom: 20px;
  transition: all 0.3s;
  cursor: pointer;
}

.album-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.album-cover {
  height: 200px;
  overflow: hidden;
  position: relative;
  background-color: #f5f7fa;
}

:deep(.el-image) {
  width: 100%;
  height: 100%;
}

:deep(.el-image__inner) {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 10px;
  box-sizing: border-box;
}

.album-info {
  padding: 14px;
}

.album-info h3 {
  margin: 0;
  font-size: 16px;
}

.album-info p {
  font-size: 13px;
  color: #999;
  margin: 8px 0;
}

.album-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #666;
}

.operations {
  display: flex;
  gap: 10px;
}

.search-wrapper {
  margin-left: auto;
}

.search-input {
  width: 300px;
}

.filter-form {
  padding: 20px;
}

.filter-footer {
  padding: 10px 20px;
  text-align: right;
}

:deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-select) {
  width: 100%;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.album-card:hover .cover-overlay {
  opacity: 1;
}

.overlay-content {
  color: white;
  text-align: center;
}

.album-desc {
  font-size: 14px;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.album-time {
  font-size: 12px;
  color: #e6e6e6;
}

.album-stats {
  display: flex;
  align-items: center;
  gap: 8px;
}

.visibility-tag {
  font-size: 12px;
  height: 20px;
  line-height: 18px;
  padding: 0 6px;
}

.image-placeholder, .image-error {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}

.image-placeholder .el-icon, .image-error .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}
</style> 