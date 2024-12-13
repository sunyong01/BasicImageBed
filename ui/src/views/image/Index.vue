<template>
  <div class="image-container">
    <!-- 顶部操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleUpload">
        <el-icon><Upload /></el-icon>上传图片
      </el-button>
      <el-button type="primary" @click="handleBatchUpload">
        <el-icon><Upload /></el-icon>批量上传
      </el-button>
      <el-button type="danger" :disabled="!selectedImages.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>批量删除
      </el-button>

      <!-- 搜索栏移到右侧 -->
      <div class="search-wrapper">
        <el-input
          v-model="searchQuery"
          placeholder="搜索图片"
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

    <!-- 图片列表 -->
    <el-row :gutter="20" class="image-list">
      <el-col :span="6" v-for="image in imageList" :key="image.key">
        <el-card class="image-card" :body-style="{ padding: '0px' }">
          <div class="image-cover">
            <el-image 
              :src="convertToProxyUrl(image.links.thumbnailUrl)" 
              :preview-src-list="convertUrlsToProxy([image.links.url])"
              :preview-teleported="true"
              fit="contain"
              loading="lazy"
              :preview-options="{
                zoom: true,
                closeOnPressEscape: true,
                zIndex: 3000
              }"
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
            <!-- 修改遮罩层，添加 pointer-events: none -->
            <div class="cover-overlay" @click.stop>
              <div class="overlay-content">
                <h4 class="image-name">{{ image.origin_name }}</h4>
                <div class="image-info">
                  <p>大小：{{ formatFileSize(image.size) }}</p>
                  <p>上传者：{{ image.upload_user }}</p>
                  <p>上传时间：{{ formatDate(image.date) }}</p>
                  <el-tag size="small" :type="image.is_public ? 'success' : 'info'">
                    {{ image.is_public ? '公开' : '私有' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
          <div class="image-footer">
            <div class="operations">
              <el-space :size="4">
                <el-dropdown trigger="click" @command="handleCopy">
                  <el-button link size="small">
                    复制链接
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <div class="dropdown-item-with-input">
                        <el-dropdown-item :command="{type: 'url', url: image.links.url}">
                          原图链接
                        </el-dropdown-item>
                        <el-input :model-value="image.links.url" size="small" readonly />
                      </div>
                      <div class="dropdown-item-with-input">
                        <el-dropdown-item :command="{type: 'thumbnailUrl', url: image.links.thumbnailUrl}">
                          缩略图链接
                        </el-dropdown-item>
                        <el-input :model-value="image.links.thumbnailUrl" size="small" readonly />
                      </div>
                      <div class="dropdown-item-with-input">
                        <el-dropdown-item :command="{type: 'html', url: image.links.html}">
                          HTML代码
                        </el-dropdown-item>
                        <el-input :model-value="image.links.html" size="small" readonly />
                      </div>
                      <div class="dropdown-item-with-input">
                        <el-dropdown-item :command="{type: 'markdown', url: image.links.markdown}">
                          Markdown
                        </el-dropdown-item>
                        <el-input :model-value="image.links.markdown" size="small" readonly />
                      </div>
                      <div class="dropdown-item-with-input">
                        <el-dropdown-item :command="{type: 'bbcode', url: image.links.bbcode}">
                          BBCode
                        </el-dropdown-item>
                        <el-input :model-value="image.links.bbcode" size="small" readonly />
                      </div>
                      <div class="dropdown-item-with-input">
                        <el-dropdown-item :command="{type: 'markdownWithLink', url: image.links.markdownWithLink}">
                          Markdown带链接
                        </el-dropdown-item>
                        <el-input :model-value="image.links.markdownWithLink" size="small" readonly />
                      </div>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>

                <el-dropdown trigger="click" @command="(id) => handleLinkAlbum(image, id)">
                  <el-button link size="small">
                    关联相册
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item 
                        v-for="album in albums" 
                        :key="album.id" 
                        :command="album.id"
                      >
                        {{ album.name }}
                      </el-dropdown-item>
                      <el-dropdown-item 
                        v-if="image.album_id" 
                        divided 
                        :command="null"
                      >
                        <span style="color: #f56c6c;">取消关联</span>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>

                <el-dropdown trigger="click">
                  <el-button link size="small">
                    更多操作
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="showImageDetails(image)">
                        <el-icon><InfoFilled /></el-icon>详情
                      </el-dropdown-item>
                      <el-dropdown-item @click="handlePublicStatusChange(image)">
                        <el-icon><Lock v-if="!image.is_public" /><Unlock v-else /></el-icon>
                        {{ image.is_public ? '设为私有' : '设为公开' }}
                      </el-dropdown-item>
                      <el-dropdown-item divided type="danger" @click="handleDelete(image.key)">
                        <el-icon><Delete /></el-icon>删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-space>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传图片"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-upload
        class="upload-area"
        drag
        action="/api/v1/images/upload"
        :headers="uploadHeaders"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        multiple
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          托拽文件到处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 jpg/png/gif 文件，且不超 5MB
          </div>
        </template>
      </el-upload>
    </el-dialog>

    <!-- 批量删除确框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="360px"
      :close-on-click-modal="false"
    >
      <div class="delete-confirm-content">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <p>确定要删除选中的 {{ selectedImages.length }} 张图片吗？</p>
        <p class="warning-text">此操作不可恢复，请谨慎操作！</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete" :loading="deleting">
            确定删除
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详信息对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="图片详细信息"
      width="600px"
    >
      <template v-if="currentImage">
        <div class="details-container">
          <div class="detail-preview">
            <el-image
              :src="convertToProxyUrl(currentImage?.links.thumbnailUrl)"
              :preview-src-list="convertUrlsToProxy([currentImage?.links.url])"
              fit="contain"
              class="preview-image"
            />
          </div>
          <div class="detail-list">
            <div class="detail-item">
              <span class="detail-label">文件名</span>
              <span class="detail-value">{{ currentImage.origin_name }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">存储路径</span>
              <span class="detail-value">{{ currentImage.pathname }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">文件大小</span>
              <span class="detail-value">{{ formatFileSize(currentImage.size) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">图片尺寸</span>
              <span class="detail-value">{{ currentImage.width }} × {{ currentImage.height }} px</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">上传时间</span>
              <span class="detail-value">{{ formatDate(currentImage.date) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">访问权限</span>
              <span class="detail-value">
                <el-tag size="small" :type="currentImage.is_public ? 'success' : 'info'">
                  {{ currentImage.is_public ? '公开' : '私有' }}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">所属相册</span>
              <span class="detail-value">
                <template v-if="albumInfo">
                  {{ albumInfo.create_user }}:{{ albumInfo.name }}
                </template>
                <template v-else>
                  {{ currentImage.album_id ? '加载中...' : '未分配' }}
                </template>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">MD5</span>
              <span class="detail-value">{{ currentImage.md5 }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">SHA1</span>
              <span class="detail-value">{{ currentImage.sha1 }}</span>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 添加筛选抽屉 -->
    <el-drawer
      v-model="filterDrawer"
      title="图片筛选"
      direction="rtl"
      size="380px"
    >
      <el-form
        ref="filterForm"
        :model="filterCondition"
        label-width="80px"
        class="filter-form"
      >
        <el-form-item label="文件名">
          <el-input v-model="filterCondition.origin_name" placeholder="请输入文件名" clearable />
        </el-form-item>

        <el-form-item label="用户名" v-if="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')">
          <el-input 
            v-model="filterCondition.upload_user" 
            placeholder="请输入上传者用名" 
            clearable 
          />
        </el-form-item>

        <el-form-item label="访问权限">
          <el-select v-model="filterCondition.is_public" placeholder="请选择" clearable>
            <el-option label="公开" :value="true" />
            <el-option label="私有" :value="false" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属相册">
          <el-select v-model="filterCondition.album_id" placeholder="请选择" clearable>
            <el-option label="未分配" :value="0" />
            <el-option
              v-for="album in albums"
              :key="album.id"
              :label="album.name"
              :value="album.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="排序">
          <el-select v-model="sortOrder" placeholder="请选择" clearable>
            <el-option label="上传时间降序" value="date_desc" />
            <el-option label="上传时间升序" value="date_asc" />
            <el-option label="文件大小降序" value="size_desc" />
            <el-option label="文件大小升序" value="size_asc" />
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Search, 
  Upload, 
  Delete, 
  UploadFilled, 
  Picture, 
  Loading,
  ArrowDown,
  InfoFilled,
  Warning,
  Filter,
  Lock,
  Unlock
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useClipboard } from '@vueuse/core'
import { image, album, hasAnyRole } from '@/api'
import { useRoute } from 'vue-router'
import { convertToProxyUrl, convertUrlsToProxy } from '@/utils/proxyUrl'

export default {
  name: 'ImageManagement',
  components: {
    Search,
    Upload,
    Delete,
    UploadFilled,
    Picture,
    Loading,
    ArrowDown,
    InfoFilled,
    Warning,
    Filter,
    Lock,
    Unlock
  },
  setup() {
    const imageList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(12)
    const total = ref(0)
    const searchQuery = ref('')
    const selectedImages = ref([])
    const uploadDialogVisible = ref(false)
    const deleteDialogVisible = ref(false)
    const deleting = ref(false)
    const { copy } = useClipboard()
    const detailsDialogVisible = ref(false)
    const currentImage = ref(null)
    const filterDrawer = ref(false)
    const filterCondition = reactive({
      origin_name: '',
      upload_user: '',
      is_public: null,
      album_id: null,
    })
    const sortOrder = ref('')
    const albums = ref([])
    const albumInfo = ref(null)
    const route = useRoute()
    const { search: searchImages, updatePublicStatus } = image

    // 获取相册列表
    const fetchAlbums = async () => {
      try {
        const { data } = await album.getList()
        if (data.success) {
          albums.value = data.data.data || []
        }
      } catch (error) {
        console.error('获取相册列表败:', error)
      }
    }

    // 获取图片列表
    const fetchImages = async () => {
      try {
        // 构建查询条件
        const condition = { ...filterCondition }
        
        // 处理相册ID
        if (condition.album_id === 0) {
          condition.album_id = null
        }

        const response = await searchImages(
          condition,
          currentPage.value,
          pageSize.value,
          sortOrder.value
        )
         
        if (response.data.success) {
          imageList.value = response.data.data.data
          total.value = response.data.data.total
        }
      } catch (error) {
        console.error('获取图片列表失败:', error)
        ElMessage.error('获取图片列表失败')
      }
    }

    // 格式化文件大小
    const formatFileSize = (size) => {
      if (size < 1024) return size.toFixed(2) + ' KB'
      return (size / 1024).toFixed(2) + ' MB'
    }

    // 格式化日期
    const formatDate = (date) => {
      if (!date) return ''
      const d = new Date(date)
      return d.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }

    // 处理复制链接
    const handleCopy = async (command) => {
      try {
        await copy(command.url)
        let typeText = {
          'url': '原图链接',
          'thumbnailUrl': '缩略图链接',
          'html': 'HTML代码',
          'markdown': 'Markdown',
          'bbcode': 'BBCode',
          'markdownWithLink': 'Markdown带链接'
        }[command.type]
        ElMessage.success(`${typeText} 已复制到剪贴板`)
      } catch (error) {
        console.error('Copy failed:', error)
        ElMessage.error('复制失败')
      }
    }

    // 处理删除
    const handleDelete = async (key) => {
      try {
        await request.delete(`/v1/images/${key}`)
        ElMessage.success('删除成功')
        fetchImages()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    }

    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchImages()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchImages()
    }

    // 搜索处理
    const handleSearch = () => {
      fetchImages()
    }

    // 上传相关
    const uploadHeaders = {
      Authorization: `Bearer ${localStorage.getItem('token')}`
    }

    const handleUpload = () => {
      uploadDialogVisible.value = true
    }

    const handleBatchUpload = () => {
      uploadDialogVisible.value = true
    }

    const beforeUpload = (file) => {
      const isImage = /^image\/(jpeg|png|gif)$/.test(file.type)
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt5M) {
        ElMessage.error('图片大小不能超过 5MB!')
        return false
      }
      return true
    }

    const handleUploadError = (error, file) => {
      // 检查是否有响应数据
      if (error.response && error.response.data) {
        ElMessage.error(error.response.data.message || '上传失败')
      } else {
        ElMessage.error('上传失败: ' + error.message)
      }
    }

    const handleUploadSuccess = (response) => {
      // 检查响应状态
      if (response.success) {
        ElMessage.success('上传成功')
        uploadDialogVisible.value = false
        fetchImages()
      } else {
        // 如果后端返回失败状态，显示错误信息
        ElMessage.error(response.message || '上传失败')
      }
    }

    // 删除相关
    const handleBatchDelete = () => {
      if (selectedImages.value.length === 0) {
        ElMessage.warning('请选择删除的图片')
        return
      }
      deleteDialogVisible.value = true
    }

    const confirmDelete = async () => {
      deleting.value = true
      try {
        await request.delete('/v1/images', {
          data: {
            ids: selectedImages.value
          }
        })
        ElMessage.success('删除成功')
        deleteDialogVisible.value = false
        selectedImages.value = []
        fetchImages()
      } catch (error) {
        console.error('删除失败:', error)
      } finally {
        deleting.value = false
      }
    }

    // 显示图片详细信息
    const showImageDetails = async (image) => {
      currentImage.value = image
      detailsDialogVisible.value = true
      
      // 如果有相册ID，获取相册信息
      if (image.album_id) {
        try {
          const { data } = await album.getById(image.album_id)
          if (data.success) {
            albumInfo.value = data.data
          }
        } catch (error) {
          console.error('获取相册信息失败:', error)
        }
      } else {
        albumInfo.value = null
      }
    }

    // 重置筛选条件
    const resetFilter = () => {
      Object.keys(filterCondition).forEach(key => {
        filterCondition[key] = key === 'album_id' ? null : null
      })
      sortOrder.value = ''
      currentPage.value = 1
      fetchImages()
    }

    // 应用筛选条件
    const applyFilter = () => {
      currentPage.value = 1
      fetchImages()
      filterDrawer.value = false
    }

    // 处理关联相册
    const handleLinkAlbum = async (image, albumId) => {
      try {
        await album.linkImage(albumId, image.key)
        ElMessage.success(albumId ? '关联成功' : '已取消关联')
        fetchImages() // 刷新图片列表
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    // 在对话框闭时清除相册信息
    watch(detailsDialogVisible, (val) => {
      if (!val) {
        albumInfo.value = null
      }
    })

    // 修改 onMounted
    onMounted(() => {
      // 检查路由参数中是否有相册信息
      if (route.query.album_id) {
        filterCondition.album_id = parseInt(route.query.album_id)
        // 如果需要，可以设置其他筛选条件的默认值
      }
      
      fetchAlbums()
      fetchImages()
    })

    const handlePublicStatusChange = async (image) => {
      try {
        await updatePublicStatus(image.key)
        ElMessage.success(`已${!image.is_public ? '公开' : '私有化'}该图片`)
        // 刷新图片列表
        fetchImages()
      } catch (error) {
        ElMessage.error('修改状态失败')
      }
    }

    return {
      imageList,
      currentPage,
      pageSize,
      total,
      searchQuery,
      selectedImages,
      uploadDialogVisible,
      deleteDialogVisible,
      deleting,
      formatFileSize,
      formatDate,
      handleCopy,
      handleDelete,
      handleSizeChange,
      handleCurrentChange,
      fetchImages,
      handleSearch,
      uploadHeaders,
      handleUpload,
      handleBatchUpload,
      beforeUpload,
      handleUploadSuccess,
      handleUploadError,
      handleBatchDelete,
      confirmDelete,
      detailsDialogVisible,
      currentImage,
      showImageDetails,
      filterDrawer,
      filterCondition,
      sortOrder,
      albums,
      resetFilter,
      applyFilter,
      handleLinkAlbum,
      albumInfo,
      hasAnyRole,
      handlePublicStatusChange,
      convertToProxyUrl,
      convertUrlsToProxy
    }
  }
}
</script>

<style scoped>
.image-container {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-wrapper {
  margin-left: auto;
}

.search-input {
  width: 300px;
}

.image-list {
  margin-bottom: 20px;
}

.image-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.image-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.image-cover {
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
  pointer-events: none;
}

.image-card:hover .cover-overlay {
  opacity: 1;
}

.overlay-content {
  color: white;
  text-align: center;
  width: 100%;
}

.image-name {
  font-size: 14px;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 13px;
}

.image-info p {
  margin: 0;
  color: #e6e6e6;
}

.image-footer {
  padding: 12px;
  border-top: 1px solid #ebeef5;
}

.operations {
  display: flex;
  justify-content: center;
}

/* 加载和错误状态的样式 */
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

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

:deep(.el-image) {
  width: 100%;
  height: 100%;
}

:deep(.el-image__inner) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

:deep(.el-image:hover .el-image__inner) {
  transform: scale(1.05);
}

/* 上传对话框样式 */
.upload-area {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
}

.el-upload__tip {
  color: #909399;
  font-size: 13px;
  margin-top: 12px;
  text-align: center;
}

/* 删除确认框样式 */
.delete-confirm-content {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 48px;
  color: #f56c6c;
  margin-bottom: 16px;
}

.warning-text {
  color: #f56c6c;
  font-size: 13px;
  margin-top: 8px;
}

/* 详细信息对话框样式 */
.details-container {
  display: flex;
  gap: 24px;
}

.detail-preview {
  width: 200px;
  height: 200px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.detail-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-label {
  width: 80px;
  color: #606266;
  font-weight: 500;
}

.detail-value {
  flex: 1;
  color: #303133;
  word-break: break-all;
}

/* 对话框通用样式 */
:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__body) {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

:deep(.el-dialog__footer) {
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 优化下拉菜单样式 */
:deep(.el-dropdown-menu) {
  min-width: 200px;
  padding: 8px 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 4px;
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid var(--el-border-color-lighter);
  margin: 4px 0;
}

/* 添加筛选相关样式 */
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

:deep(.el-date-editor.el-input__wrapper) {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}

.dropdown-item-with-input {
  padding: 8px 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.dropdown-item-with-input:last-child {
  border-bottom: none;
}

.dropdown-item-with-input :deep(.el-dropdown-menu__item) {
  padding: 0 0 8px 0;
  min-width: auto;
}

.dropdown-item-with-input :deep(.el-input) {
  width: 300px;
}

.dropdown-item-with-input :deep(.el-input__wrapper) {
  padding: 0 8px;
}

.dropdown-item-with-input :deep(.el-input__inner) {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-family: monospace;
}
</style> 