package web.sy.bed.service;

import web.sy.base.pojo.enums.OperationType;
import web.sy.bed.vo.stats.AdminStatsVO;
import web.sy.bed.vo.stats.StatsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ImageAccessLogService {
    /**
     * 记录访问日志
     * @param pathname 文件路径
     * @param userId 用户ID
     * @param fileSize 文件大小(KB)
     * @param operationType 操作类型
     */
    void logAccess(String pathname, Long userId, Double fileSize, OperationType operationType);

    /**
     * 获取上传统计
     */
    StatsVO.UploadStats getUploadStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取下载统计
     */
    StatsVO.DownloadStats getDownloadStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取总体统计
     */
    StatsVO.TotalStats getTotalStats(Long userId);

    /**
     * 获取存储空间统计
     */
    StatsVO.StorageStats getStorageStats(Long userId);

    /**
     * 获取图片类型分布
     */
    Map<String, Long> getImageTypeDistribution(Long userId);

    /**
     * 获取访问趋势
     */
    List<StatsVO.AccessTrendItem> getAccessTrend(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取所有统计数据
     */
    StatsVO getStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取管理员统计数据
     */
    AdminStatsVO getAdminStats();
} 