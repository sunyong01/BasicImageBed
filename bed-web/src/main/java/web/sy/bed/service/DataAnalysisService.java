package web.sy.bed.service;

import web.sy.base.pojo.entity.ImageInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据分析服务
 * 用于分析图片访问、用户行为等数据
 */
public interface DataAnalysisService {
    /**
     * 统计指定图片在时间段内的下载次数
     * @param pathname 图片路径
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 下载次数
     */
    int getImageDownloadCount(String pathname, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计用户所有图片在时间段内的总下载次数
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 下载次数
     */
    int getUserImagesDownloadCount(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取当天下载量前10的图片
     * @return 图片信息列表
     */
    List<ImageInfo> getTopDownloadedImagesForToday();

    /**
     * 获取指定时间段内下载量前10的图片
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 图片信息列表
     */
    List<ImageInfo> getTopDownloadedImages(LocalDateTime startTime, LocalDateTime endTime);
        /**
     * 统计指定图片在时间段内的流量消耗（字节）
     * @param pathname 图片路径
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 流量消耗（字节）
     */
    Long getImageTrafficUsage(String pathname, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计所有图片在时间段内的总流量消耗（字节）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总流量消耗（字节）
     */
    Long getTotalTrafficUsage(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计所有图片在时间段内的总下载次数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总下载次数
     */
    int getTotalDownloadCount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取最近30天全站图片每天的下载次数
     * @return 每天的下载次数列表，key为日期，value为下载次数
     */
    List<Map<String, Object>> getDailyDownloadStatsLast30Days();

    /**
     * 获取最近30天指定用户的图片每天的下载次数
     * @param userId 用户ID
     * @return 每天的下载次数列表，key为日期，value为下载次数
     */
    List<Map<String, Object>> getUserDailyDownloadStatsLast30Days(Long userId);

    /**
     * 统计用户今天上传的图片数量
     * @param userId 用户ID
     * @return 上传数量
     */
    int getTodayUploadCount(Long userId);

    /**
     * 获取用户最新上传的N张图片
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 图片列表
     */
    List<ImageInfo> getLatestUploadedImages(Long userId, int limit);
} 