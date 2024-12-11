<template>
  <div class="dashboard-container">
    <!-- 今日数据 -->
    <el-row :gutter="20" class="data-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总访问量</span>
              <el-tag size="small" type="success" v-if="stats.totalStats?.totalAccess > 0">
                {{ stats.totalStats?.totalAccess }}
              </el-tag>
            </div>
          </template>
          <div class="card-content">
            <div class="download-stats">
              <div class="download-item">
                <span class="label">原图下载：</span>
                <span class="value">{{ stats.downloadStats?.originalTotalCount || 0 }}</span>
              </div>
              <div class="download-item">
                <span class="label">缩略图下载：</span>
                <span class="value">{{ stats.downloadStats?.thumbnailTotalCount || 0 }}</span>
              </div>
            </div>
            <div class="card-total">
              总流量：{{ formatSize(stats.downloadStats?.totalTraffic || 0) }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总流量</span>
            </div>
          </template>
          <div class="card-content">
            <div class="card-value">{{ formatSize(stats.downloadStats?.totalTraffic || 0) }}</div>
            <div class="card-total">平均：{{ formatSize(stats.downloadStats?.averageSize || 0) }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>上传统计</span>
            </div>
          </template>
          <div class="card-content">
            <div class="download-stats">
              <div class="download-item">
                <span class="label">原图上传：</span>
                <span class="value">{{ stats.uploadStats?.originalTotalCount || 0 }}</span>
              </div>
              <div class="download-item">
                <span class="label">缩略图上传：</span>
                <span class="value">{{ stats.uploadStats?.thumbnailTotalCount || 0 }}</span>
              </div>
            </div>
            <div class="card-total">
              总大小：{{ formatSize(stats.uploadStats?.totalSize || 0) }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>存储空间</span>
            </div>
          </template>
          <div class="card-content">
            <div class="card-value">{{ formatSize(stats.storageStats?.usedCapacity || 0) }}</div>
            <div class="card-total">
              总容量：{{ formatSize(stats.storageStats?.totalCapacity || 0) }}
              <el-progress 
                :percentage="calculateUsagePercentage()" 
                :status="getStorageStatus()"
              />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图片类型分布 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>图片类型分布</span>
            </div>
          </template>
          <div class="type-distribution">
            <div v-for="(count, type) in stats.imageTypeDistribution" :key="type" class="type-item">
              <span class="type-label">{{ type.toUpperCase() }}</span>
              <el-tag>{{ count }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>访问趋势</span>
            </div>
          </template>
          <div class="trend-list">
            <div v-for="item in stats.accessTrend" :key="item.date" class="trend-item">
              <span class="trend-date">{{ formatDate(item.date) }}</span>
              <div class="trend-counts">
                <el-tag type="success">
                  上传：{{ (item.originalUploadCount || 0) + (item.thumbnailUploadCount || 0) }}
                </el-tag>
                <el-tag type="info">
                  下载：{{ (item.originalDownloadCount || 0) + (item.thumbnailDownloadCount || 0) }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门图片和最近下载 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>热门下载</span>
            </div>
          </template>
          <div class="image-list">
            <div v-for="(image, index) in stats.topDownloadedImages" :key="index" class="image-item">
              <span class="image-name">{{ image.originName }}</span>
              <div class="download-counts">
                <el-tag type="success">原图：{{ image.originalDownloadCount || 0 }}</el-tag>
                <el-tag type="info">缩略图：{{ image.thumbnailDownloadCount || 0 }}</el-tag>
                <el-tag type="warning">
                  总计：{{ (image.originalDownloadCount || 0) + (image.thumbnailDownloadCount || 0) }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近下载</span>
            </div>
          </template>
          <div class="image-list">
            <div v-for="(image, index) in stats.recentDownloadedImages" :key="index" class="image-item">
              <span class="image-name">{{ image.originName }}</span>
              <span class="download-time">{{ formatDateTime(image.downloadTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { stats as statsApi } from '@/api'

export default {
  name: 'Dashboard',
  setup() {
    const statsData = ref({})

    // 格式化文件大小
    const formatSize = (kb) => {
      if (!kb) return '0 KB'
      if (kb < 1024) return kb.toFixed(2) + ' KB'
      if (kb < 1024 * 1024) return (kb / 1024).toFixed(2) + ' MB'
      return (kb / 1024 / 1024).toFixed(2) + ' GB'
    }

    // 格式化日期
    const formatDate = (date) => {
      return new Date(date).toLocaleDateString()
    }

    // 计算存储使用百分比
    const calculateUsagePercentage = () => {
      if (!statsData.value.storageStats) return 0
      return Number((statsData.value.storageStats.usageRate * 100).toFixed(2))
    }

    // 获取存储状态
    const getStorageStatus = () => {
      const usage = statsData.value.storageStats?.usageRate || 0
      if (usage > 0.9) return 'exception'
      if (usage > 0.7) return 'warning'
      return 'success'
    }

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const { data } = await statsApi.getStats(7)
        if (data.success) {
          statsData.value = data.data
          console.log('Stats data:', data.data)
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        ElMessage.error('获取统计数据失败')
      }
    }

    // 添加日期时间格式化方法
    const formatDateTime = (date) => {
      if (!date) return ''
      return new Date(date).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }

    onMounted(() => {
      fetchStats()
    })

    return {
      stats: statsData,
      formatSize,
      formatDate,
      calculateUsagePercentage,
      getStorageStatus,
      formatDateTime,
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.data-cards {
  margin-bottom: 20px;
}

.data-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
  padding: 10px 0;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.card-total {
  font-size: 13px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
}

.chart-container {
  height: 300px;
}

:deep(.el-card__header) {
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
}

.type-distribution {
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
}

.type-item {
  text-align: center;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.type-label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #606266;
}

.trend-list {
  padding: 20px;
}

.trend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.trend-date {
  color: #606266;
}

.trend-counts {
  display: flex;
  gap: 12px;
}

.image-list {
  padding: 0 20px;
}

.image-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.image-item:last-child {
  border-bottom: none;
}

.image-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 16px;
}

.download-time {
  color: #909399;
  font-size: 13px;
}

.download-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.download-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.download-item .label {
  color: #606266;
  font-size: 14px;
}

.download-item .value {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.card-total {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #ebeef5;
  font-size: 13px;
  color: #909399;
}

.download-counts {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
