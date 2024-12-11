<template>
    <div class="gallery-container">
      <!-- 顶部操作栏 -->
      <div class="operation-bar">
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
                :src="image.links.thumbnailUrl" 
                :preview-src-list="[image.links.url]"
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
              <div class="cover-overlay">
                <div class="overlay-content">
                  <h4 class="image-name">{{ image.origin_name }}</h4>
                  <div class="image-info">
                    <p>大小：{{ formatFileSize(image.size) }}</p>
                    <p>上传者：{{ image.upload_user }}</p>
                    <p>上传时间：{{ formatDate(image.date) }}</p>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 分页 -->
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
  
      <!-- 筛选抽屉 -->
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
  
          <el-form-item label="上传者">
            <el-input v-model="filterCondition.upload_user" placeholder="请输入上传者用户名" clearable />
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
  import { ref, reactive, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { Search, Filter, Picture, Loading } from '@element-plus/icons-vue'
  import { image } from '@/api'
  
  export default {
    name: 'Gallery',
    components: {
      Search,
      Filter,
      Picture,
      Loading
    },
    setup() {
      const imageList = ref([])
      const currentPage = ref(1)
      const pageSize = ref(12)
      const total = ref(0)
      const searchQuery = ref('')
      const filterDrawer = ref(false)
      const filterCondition = reactive({
        origin_name: '',
        upload_user: '',
        is_public: true  // 只显示公开图片
      })
      const sortOrder = ref('date_desc')
      const { getPublicImages } = image
  
      // 图片列表
      const fetchImages = async () => {
        try {
          const condition = {
            ...filterCondition,
            origin_name: searchQuery.value || filterCondition.origin_name
          }
  
          const response = await getPublicImages(
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
        if (!size) return '0 KB'
        if (size < 1024) return size.toFixed(2) + ' KB'
        if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' MB'
        return (size / (1024 * 1024)).toFixed(2) + ' GB'
      }
  
      // 格式化日期
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
  
      // 处理搜索
      const handleSearch = () => {
        currentPage.value = 1
        fetchImages()
      }
  
      // 处理分页
      const handleSizeChange = (val) => {
        pageSize.value = val
        fetchImages()
      }
  
      const handleCurrentChange = (val) => {
        currentPage.value = val
        fetchImages()
      }
  
      // 重置筛选
      const resetFilter = () => {
        searchQuery.value = ''
        Object.keys(filterCondition).forEach(key => {
          if (key !== 'is_public') {
            filterCondition[key] = ''
          }
        })
        sortOrder.value = 'date_desc'
        currentPage.value = 1
        fetchImages()
      }
  
      // 应用筛选
      const applyFilter = () => {
        currentPage.value = 1
        fetchImages()
        filterDrawer.value = false
      }
  
      onMounted(() => {
        fetchImages()
      })
  
      return {
        imageList,
        currentPage,
        pageSize,
        total,
        searchQuery,
        filterDrawer,
        filterCondition,
        sortOrder,
        handleSearch,
        handleSizeChange,
        handleCurrentChange,
        resetFilter,
        applyFilter,
        formatFileSize,
        formatDate
      }
    }
  }
  </script>
  
  <style scoped>
  .gallery-container {
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
    pointer-events: none;
  }
  
  .image-name {
    font-size: 14px;
    margin: 0 0 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .image-info {
    font-size: 13px;
  }
  
  .image-info p {
    margin: 4px 0;
    color: #e6e6e6;
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
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