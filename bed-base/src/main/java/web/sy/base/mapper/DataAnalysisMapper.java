package web.sy.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import web.sy.base.pojo.entity.ImageInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据分析 Mapper
 * 用于查询统计相关数据
 */
@Mapper
public interface DataAnalysisMapper {
    /**
     * 获取指定图片在时间段内的下载次数
     */
    int getImageDownloadCount(
        @Param("pathname") String pathname,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取用户所有图片在时间段内的下载次数
     */
    int getUserImagesDownloadCount(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取时间段内下载量前10的图片
     */
    List<ImageInfo> getTopDownloadedImages(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取指定图片在时间段内的流量消耗
     */
    Long getImageTrafficUsage(
        @Param("pathname") String pathname,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取所有图片在时间段内的总流量消耗
     */
    Long getTotalTrafficUsage(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
       /**
     * 获取时间段内的总下载次数
     */
    int getTotalDownloadCount(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取时间段内每天的下载统计
     */
    List<Map<String, Object>> getDailyDownloadStats(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取时间段内指定用户每天的下载统计
     */
    List<Map<String, Object>> getUserDailyDownloadStats(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
        /**
     * 获取用户今天上传的图片数量
     */
    int getTodayUploadCount(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取用户最新上传的图片
     */
    List<ImageInfo> getLatestUploadedImages(
        @Param("userId") Long userId,
        @Param("limit") int limit
    );
} 