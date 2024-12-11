<template>
  <div class="stats-container">
    <!-- 今日数据概览 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日上传</span>
              <el-tag size="small" type="success">{{ stats.todayStats?.uploadCount || 0 }}</el-tag>
            </div>
          </template>
          <div class="card-value">{{ formatSize(stats.todayStats?.uploadTraffic || 0) }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日下载</span>
              <el-tag size="small" type="info">{{ stats.todayStats?.downloadCount || 0 }}</el-tag>
            </div>
          </template>
          <div class="card-value">{{ formatSize(stats.todayStats?.downloadTraffic || 0) }}</div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户统计</span>
            </div>
          </template>
          <div class="overview-stats">
            <div class="overview-item">
              <span>总用户数</span>
              <el-tag>{{ stats.systemOverview?.totalUsers || 0 }}</el-tag>
            </div>
            <div class="overview-item">
              <span>活跃用户</span>
              <el-tag type="success">{{ stats.systemOverview?.activeUsers || 0 }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>内容统计</span>
            </div>
          </template>
          <div class="overview-stats">
            <div class="overview-item">
              <span>总图片数</span>
              <el-tag type="info">{{ stats.systemOverview?.totalImages || 0 }}</el-tag>
            </div>
            <div class="overview-item">
              <span>总相册数</span>
              <el-tag type="warning">{{ stats.systemOverview?.totalAlbums || 0 }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>流量统计</span>
            </div>
          </template>
          <div class="traffic-stats">
            <div class="traffic-item">
              <span>上传流量</span>
              <el-tag type="success">{{ formatSize(getTotalUploadTraffic()) }}</el-tag>
            </div>
            <div class="traffic-item">
              <span>下载流量</span>
              <el-tag type="info">{{ formatSize(getTotalDownloadTraffic()) }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 访问趋势图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>访问趋势</span>
              <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
                <el-radio-button :value="'month'">本月</el-radio-button>
                <el-radio-button :value="'all'">全部</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <div ref="chartRef" style="width: 100%; height: 100%;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门图片统计 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>下载排行</span>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(image, index) in stats.popularImages?.topDownloaded" 
                 :key="index" 
                 class="rank-item">
              <span class="rank-name">{{ image.originName }}</span>
              <div class="rank-stats">
                <el-tag size="small">{{ image.downloadCount }}次</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>流量排行</span>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(image, index) in stats.popularImages?.topTraffic" 
                 :key="index" 
                 class="rank-item">
              <span class="rank-name">{{ image.originName }}</span>
              <div class="rank-stats">
                <el-tag size="small">{{ formatSize(image.totalTraffic) }}</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户排行榜 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>图片数量排行</span>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(user, index) in stats.userRankings?.imageCountRanking" 
                 :key="index" 
                 class="rank-item">
              <div class="rank-user">
                <span class="rank-number">{{ index + 1 }}</span>
                <span class="user-name">{{ user.nickname || user.username }}</span>
              </div>
              <el-tag size="small">{{ user.value }} 张</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>流量消耗排行</span>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(user, index) in stats.userRankings?.trafficRanking" 
                 :key="index" 
                 class="rank-item">
              <div class="rank-user">
                <span class="rank-number">{{ index + 1 }}</span>
                <span class="user-name">{{ user.nickname || user.username }}</span>
              </div>
              <el-tag size="small">{{ formatSize(user.value) }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>存储空间排行</span>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(user, index) in stats.userRankings?.storageRanking" 
                 :key="index" 
                 class="rank-item">
              <div class="rank-user">
                <span class="rank-number">{{ index + 1 }}</span>
                <span class="user-name">{{ user.nickname || user.username }}</span>
              </div>
              <el-tag size="small">{{ formatSize(user.value) }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>下载次数排行</span>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(user, index) in stats.userRankings?.downloadCountRanking" 
                 :key="index" 
                 class="rank-item">
              <div class="rank-user">
                <span class="rank-number">{{ index + 1 }}</span>
                <span class="user-name">{{ user.nickname || user.username }}</span>
              </div>
              <el-tag size="small">{{ user.value }} 次</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { stats as statsApi } from '@/api'
import * as echarts from 'echarts'  // 导入 ECharts

export default {
  name: 'StatsIndex',
  setup() {
    const loading = ref(false)
    const statsData = ref({})
    const timeRange = ref('month')
    const chartRef = ref(null)
    let chart = null

    // 获取当前趋势数据
    const currentTrendData = computed(() => {
      if (timeRange.value === 'month') {
        return statsData.value?.monthlyStats || []
      }
      return statsData.value?.allTimeStats || []
    })

    // 计算总上传流量
    const getTotalUploadTraffic = () => {
      return statsData.value?.allTimeStats?.reduce((sum, item) => sum + (item.uploadTraffic || 0), 0) || 0
    }

    // 计算总下载流量
    const getTotalDownloadTraffic = () => {
      return statsData.value?.allTimeStats?.reduce((sum, item) => sum + (item.downloadTraffic || 0), 0) || 0
    }

    // 格式化文件大小
    const formatSize = (kb) => {
      if (!kb) return '0 KB'
      if (kb < 1024) return kb.toFixed(2) + ' KB'
      if (kb < 1024 * 1024) return (kb / 1024).toFixed(2) + ' MB'
      return (kb / (1024 * 1024)).toFixed(2) + ' GB'
    }

    // 格式化日期
    const formatDate = (date) => {
      return new Date(date).toLocaleDateString()
    }

    // 获取统计数据
    const fetchStats = async () => {
      loading.value = true
      try {
        const { data } = await statsApi.getAdminStats()
        if (data.success) {
          statsData.value = data.data
        }
      } catch (error) {
        ElMessage.error('获取统计数据失败')
      } finally {
        loading.value = false
      }
    }

    // 处理时间范围变化
    const handleTimeRangeChange = () => {
      // 数据已经在 computed 属性中处理
    }

    // 初始化图表
    const initChart = () => {
      if (chart) {
        chart.dispose()
      }
      chart = echarts.init(chartRef.value)
    }

    // 更新图表数据
    const updateChart = () => {
      if (!chart) return

      // 获取数据源
      const data = timeRange.value === 'month' ? 
        statsData.value?.monthlyStats || [] : 
        statsData.value?.allTimeStats || []

      // 生成日期范围
      const today = new Date()
      const days = timeRange.value === 'month' ? 30 : 90
      const dates = Array.from({ length: days }, (_, i) => {
        const date = new Date(today)
        date.setDate(date.getDate() - (days - 1 - i))
        return date.toISOString().split('T')[0]
      })

      // 处理数据，确保每天都有数据点
      const uploadData = new Map()
      const downloadData = new Map()
      
      // 初始化所有日期的数据为0
      dates.forEach(date => {
        uploadData.set(date, 0)
        downloadData.set(date, 0)
      })

      // 填充实际数据
      data.forEach(item => {
        if (uploadData.has(item.date)) {
          uploadData.set(item.date, item.uploadCount)
          downloadData.set(item.date, item.downloadCount)
        }
      })

      // 设置图表配置
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['上传数量', '下载数量']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '上传数量',
            type: 'line',
            smooth: true,
            data: Array.from(uploadData.values()),
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '下载数量',
            type: 'line',
            smooth: true,
            data: Array.from(downloadData.values()),
            itemStyle: {
              color: '#909399'
            }
          }
        ]
      }

      chart.setOption(option)
    }

    // 监听数据变化
    watch([() => statsData.value, timeRange], () => {
      updateChart()
    })

    // 监听窗口大小变化
    const handleResize = () => {
      chart?.resize()
    }

    onMounted(() => {
      fetchStats()
      initChart()
      window.addEventListener('resize', handleResize)
    })

    // 组件卸载时清理
    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      chart?.dispose()
    })

    return {
      loading,
      stats: statsData,
      timeRange,
      currentTrendData,
      formatSize,
      formatDate,
      handleTimeRangeChange,
      getTotalUploadTraffic,
      getTotalDownloadTraffic,
      chartRef
    }
  }
}
</script>

<style scoped>
.stats-container {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 400px;
  padding: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-card__header) {
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
}

.overview-stats, .traffic-stats {
  padding: 10px 0;
}

.overview-item, .traffic-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.trend-details {
  display: flex;
  gap: 20px;
}

.trend-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rank-list {
  padding: 0 20px;
}

.rank-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.rank-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
}

.rank-stats {
  display: flex;
  gap: 8px;
}

.rank-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank-number {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 50%;
  font-size: 14px;
  color: #606266;
}

.rank-number:nth-child(1) {
  background-color: #f56c6c;
  color: white;
}

.rank-number:nth-child(2) {
  background-color: #e6a23c;
  color: white;
}

.rank-number:nth-child(3) {
  background-color: #67c23a;
  color: white;
}

.user-name {
  font-size: 14px;
  color: #303133;
}

.rank-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.rank-item:last-child {
  border-bottom: none;
}

.rank-item:hover {
  background-color: #f5f7fa;
}
</style> 