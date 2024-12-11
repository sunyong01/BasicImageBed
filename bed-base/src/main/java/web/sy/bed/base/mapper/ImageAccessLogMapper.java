package web.sy.bed.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import web.sy.bed.base.pojo.entity.ImageAccessLog;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ImageAccessLogMapper {
    /**
     * 插入访问日志
     */
    void insert(ImageAccessLog log);
    
    /**
     * 获取用户的访问日志
     */
    List<ImageAccessLog> getLogsByUserId(
        @Param("userId") Long userId,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );
    
    /**
     * 统计用户的访问日志数量
     */
    int countLogsByUserId(@Param("userId") Long userId);
    
    Map<String, Object> getUploadStats(@Param("userId") Long userId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
    
    Map<String, Object> getDownloadStats(@Param("userId") Long userId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    
    long countTotalAccess(@Param("userId") Long userId);
    
    /**
     * 获取指定日期的统计数据（管理员用）
     */
    Map<String, Object> getAdminDailyStats(@Param("date") LocalDate date);
    
    /**
     * 获取用户的每日统计数据
     */
    List<Map<String, Object>> getDailyStats(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取下载次数最多的图片
     */
    List<Map<String, Object>> getTopDownloadedImages(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 获取最近下载的图片
     */
    List<Map<String, Object>> getRecentDownloadedImages(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 获取时间范围内的每日统计数据
     */
    List<Map<String, Object>> getRangeStats(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 获取总流量最大的图片
     */
    List<Map<String, Object>> getTopTrafficImages(@Param("limit") int limit);
    
    /**
     * 获取图片数量排行
     */
    List<Map<String, Object>> getImageCountRanking(@Param("limit") int limit);
    
    /**
     * 获取流量排行
     */
    List<Map<String, Object>> getTrafficRanking(@Param("limit") int limit);
    
    /**
     * 获取存储空间排行
     */
    List<Map<String, Object>> getStorageRanking(@Param("limit") int limit);
    
    /**
     * 获取下载次数排行
     */
    List<Map<String, Object>> getDownloadCountRanking(@Param("limit") int limit);
} 