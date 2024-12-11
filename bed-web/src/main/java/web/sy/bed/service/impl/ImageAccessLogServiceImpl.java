package web.sy.bed.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import web.sy.bed.base.pojo.enums.OperationType;
import web.sy.bed.base.mapper.ImageAccessLogMapper;
import web.sy.bed.base.mapper.ImageInfoMapper;
import web.sy.bed.base.mapper.UserMapper;
import web.sy.bed.base.mapper.UserProfileMapper;
import web.sy.bed.base.mapper.AlbumMapper;
import web.sy.bed.base.pojo.entity.ImageAccessLog;
import web.sy.bed.base.pojo.entity.UserProfile;
import web.sy.bed.vo.stats.AdminStatsVO;
import web.sy.bed.vo.stats.StatsVO;
import web.sy.bed.service.ImageAccessLogService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageAccessLogServiceImpl implements ImageAccessLogService {

    private final ImageAccessLogMapper accessLogMapper;
    private final ImageInfoMapper imageInfoMapper;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final AlbumMapper albumMapper;

    @Override
    public void logAccess(String pathname, Long userId, Double fileSize, OperationType operationType) {
        ImageAccessLog log = ImageAccessLog.builder()
            .pathname(pathname)
            .userId(userId)
            .fileSize(fileSize)
            .operationType(operationType)
            .createTime(LocalDateTime.now())
            .build();
        
        accessLogMapper.insert(log);
    }

    @Override
    public StatsVO.UploadStats getUploadStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> stats = accessLogMapper.getUploadStats(userId, startTime, endTime);
        StatsVO.UploadStats uploadStats = new StatsVO.UploadStats();
        uploadStats.setOriginalTotalCount(((Number) stats.get("originalTotalCount")).longValue());
        uploadStats.setThumbnailTotalCount(((Number) stats.get("thumbnailTotalCount")).longValue());
        uploadStats.setTotalSize(((Number) stats.get("totalSize")).doubleValue());
        uploadStats.setAverageSize(((Number) stats.get("averageSize")).doubleValue());
        return uploadStats;
    }

    @Override
    public StatsVO.DownloadStats getDownloadStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> stats = accessLogMapper.getDownloadStats(userId, startTime, endTime);
        StatsVO.DownloadStats downloadStats = new StatsVO.DownloadStats();
        downloadStats.setOriginalTotalCount(((Number) stats.get("originalTotalCount")).longValue());
        downloadStats.setThumbnailTotalCount(((Number) stats.get("thumbnailTotalCount")).longValue());
        downloadStats.setTotalTraffic(((Number) stats.get("totalTraffic")).doubleValue());
        downloadStats.setAverageSize(((Number) stats.get("averageSize")).doubleValue());
        return downloadStats;
    }

    @Override
    public StatsVO.TotalStats getTotalStats(Long userId) {
        StatsVO.TotalStats stats = new StatsVO.TotalStats();
        
        // 获取基本统计数据
        stats.setTotalImages(imageInfoMapper.countImagesByUserId(userId));
        stats.setTotalAccess(accessLogMapper.countTotalAccess(userId));
        
        return stats;
    }

    @Override
    public StatsVO getStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        StatsVO statsVO = new StatsVO();
        
        // 获取上传统计
        statsVO.setUploadStats(getUploadStats(userId, startTime, endTime));
        
        // 获取下载统计
        statsVO.setDownloadStats(getDownloadStats(userId, startTime, endTime));
        
        // 获取总体统计
        statsVO.setTotalStats(getTotalStats(userId));
        
        // 获取存储空间统计
        statsVO.setStorageStats(getStorageStats(userId));
        
        // 获取图片类型分布
        statsVO.setImageTypeDistribution(getImageTypeDistribution(userId));
        
        // 获取访问趋势
        statsVO.setAccessTrend(getAccessTrend(userId, startTime, endTime));
        
        // 获取下载次数最多的图片
        List<Map<String, Object>> results = accessLogMapper.getTopDownloadedImages(userId, 10);
        List<StatsVO.TopDownloadedImage> topDownloaded = new ArrayList<>();
        
        for (Map<String, Object> result : results) {
            StatsVO.TopDownloadedImage image = new StatsVO.TopDownloadedImage();
            image.setOriginName((String) result.get("originName"));
            
            // 安全地处理原图下载次数
            Object originalCount = result.get("originalDownloadCount");
            if (originalCount != null) {
                image.setOriginalDownloadCount(((Number) originalCount).longValue());
            } else {
                image.setOriginalDownloadCount(0L);
            }
            
            // 安全地处理缩略图下载次数
            Object thumbnailCount = result.get("thumbnailDownloadCount");
            if (thumbnailCount != null) {
                image.setThumbnailDownloadCount(((Number) thumbnailCount).longValue());
            } else {
                image.setThumbnailDownloadCount(0L);
            }
            
            topDownloaded.add(image);
        }
        
        statsVO.setTopDownloadedImages(topDownloaded);
        
        // 获取最近下载的图片
        List<Map<String, Object>> recentResults = accessLogMapper.getRecentDownloadedImages(userId, 10);
        List<StatsVO.RecentDownloadedImage> recentDownloaded = new ArrayList<>();
        
        for (Map<String, Object> result : recentResults) {
            StatsVO.RecentDownloadedImage image = new StatsVO.RecentDownloadedImage();
            image.setOriginName((String) result.get("originName"));
            image.setDownloadTime((LocalDateTime) result.get("downloadTime"));
            
            // 安全地处理是否为缩略图
            Object isThumbnail = result.get("isThumbnail");
            image.setThumbnail(isThumbnail != null && ((Number) isThumbnail).intValue() == 1);
            
            recentDownloaded.add(image);
        }
        
        statsVO.setRecentDownloadedImages(recentDownloaded);
        
        return statsVO;
    }

    // 添加检查是否是管理员的方法
    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || 
                                    auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    @Override
    public StatsVO.StorageStats getStorageStats(Long userId) {
        StatsVO.StorageStats stats = new StatsVO.StorageStats();
        
        // 获取用户存储空间信息
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile != null) {
            stats.setTotalCapacity(profile.getCapacity());
            stats.setUsedCapacity(profile.getCapacityUsed());
            stats.setUsageRate(profile.getCapacityUsed() / profile.getCapacity());
        }
        
        return stats;
    }

    @Override
    public Map<String, Long> getImageTypeDistribution(Long userId) {
        List<Map<String, Object>> results = imageInfoMapper.getImageTypeDistribution(userId);
        Map<String, Long> distribution = new HashMap<>();
        
        for (Map<String, Object> result : results) {
            String key = (String) result.get("key");
            Long value = ((Number) result.get("value")).longValue();
            distribution.put(key, value);
        }
        
        return distribution;
    }

    @Override
    public List<StatsVO.AccessTrendItem> getAccessTrend(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        List<Map<String, Object>> dailyStats = accessLogMapper.getDailyStats(userId, startTime, endTime);
        
        return dailyStats.stream().map(stat -> {
            StatsVO.AccessTrendItem item = new StatsVO.AccessTrendItem();
            item.setDate((String) stat.get("date"));
            
            Object originalUploadCountObj = stat.get("originalUploadCount");
            if (originalUploadCountObj != null) {
                item.setOriginalUploadCount(((Number) originalUploadCountObj).longValue());
            }
            
            Object thumbnailUploadCountObj = stat.get("thumbnailUploadCount");
            if (thumbnailUploadCountObj != null) {
                item.setThumbnailUploadCount(((Number) thumbnailUploadCountObj).longValue());
            }
            
            Object originalDownloadCountObj = stat.get("originalDownloadCount");
            if (originalDownloadCountObj != null) {
                item.setOriginalDownloadCount(((Number) originalDownloadCountObj).longValue());
            }
            
            Object thumbnailDownloadCountObj = stat.get("thumbnailDownloadCount");
            if (thumbnailDownloadCountObj != null) {
                item.setThumbnailDownloadCount(((Number) thumbnailDownloadCountObj).longValue());
            }
            
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public AdminStatsVO getAdminStats() {
        AdminStatsVO adminStats = new AdminStatsVO();
        
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        
        // 1. 获取今日统计
        Map<String, Object> todayStatsMap = accessLogMapper.getAdminDailyStats(today);
        AdminStatsVO.DailyStats todayStats = convertToDailyStats(todayStatsMap);
        todayStats.setDate(today.toString());
        adminStats.setTodayStats(todayStats);
        
        // 2. 获取最近一个月统计
        LocalDate monthAgo = today.minusMonths(1);
        List<Map<String, Object>> monthlyStatsMap = accessLogMapper.getRangeStats(monthAgo, today);
        // 如果列表为空，创建空列表
        if (monthlyStatsMap == null) {
            monthlyStatsMap = new ArrayList<>();
        }
        adminStats.setMonthlyStats(monthlyStatsMap.stream()
                .map(this::convertToDailyStats)
                .collect(Collectors.toList()));
        
        // 3. 获取所有时间的统计
        List<Map<String, Object>> allTimeStatsMap = accessLogMapper.getRangeStats(
            LocalDate.of(2000, 1, 1),  // 使用一个足够早的日期
            today
        );
        adminStats.setAllTimeStats(allTimeStatsMap.stream()
                .map(this::convertToDailyStats)
                .collect(Collectors.toList()));
        
        // 4. 获取系统概览
        AdminStatsVO.SystemOverview systemOverview = new AdminStatsVO.SystemOverview();
        systemOverview.setTotalUsers(userMapper.countUsers());
        systemOverview.setActiveUsers(userMapper.countActiveUsers());
        systemOverview.setTotalImages(imageInfoMapper.countAllImages());
        systemOverview.setTotalAlbums(albumMapper.countAllAlbums());
        adminStats.setSystemOverview(systemOverview);
        
        // 5. 获取热门图片统计
        AdminStatsVO.PopularImages popularImages = new AdminStatsVO.PopularImages();
        
        // 获取下载次数最多的图片
        List<Map<String, Object>> topDownloaded = accessLogMapper.getTopDownloadedImages(null,5);
        popularImages.setTopDownloaded(topDownloaded.stream()
                .map(this::convertToImageStats)
                .collect(Collectors.toList()));
        
        // 获取总流量最大的图片
        List<Map<String, Object>> topTraffic = accessLogMapper.getTopTrafficImages(5);
        popularImages.setTopTraffic(topTraffic.stream()
                .map(this::convertToImageStats)
                .collect(Collectors.toList()));
        
        adminStats.setPopularImages(popularImages);
        
        // 添加用户排行榜
        AdminStatsVO.UserRankings userRankings = new AdminStatsVO.UserRankings();
        
        // 获取图片数量排行
        userRankings.setImageCountRanking(
            accessLogMapper.getImageCountRanking(10).stream()
                .map(this::convertToUserRankItem)
                .collect(Collectors.toList())
        );
        
        // 获取流量排行
        userRankings.setTrafficRanking(
            accessLogMapper.getTrafficRanking(10).stream()
                .map(this::convertToUserRankItem)
                .collect(Collectors.toList())
        );
        
        // 获取存储空间排行
        userRankings.setStorageRanking(
            accessLogMapper.getStorageRanking(10).stream()
                .map(this::convertToUserRankItem)
                .collect(Collectors.toList())
        );
        
        // 获取下载次数排行
        userRankings.setDownloadCountRanking(
            accessLogMapper.getDownloadCountRanking(10).stream()
                .map(this::convertToUserRankItem)
                .collect(Collectors.toList())
        );
        
        adminStats.setUserRankings(userRankings);
        
        return adminStats;
    }

    private AdminStatsVO.DailyStats convertToDailyStats(Map<String, Object> statsMap) {
        AdminStatsVO.DailyStats stats = new AdminStatsVO.DailyStats();
        
        // 如果传入的 Map 为空，返回空对象
        if (statsMap == null) {
            stats.setUploadCount(0L);
            stats.setUploadTraffic(0.0);
            stats.setDownloadCount(0L);
            stats.setDownloadTraffic(0.0);
            return stats;
        }
        
        if (statsMap.get("date") != null) {
            stats.setDate(statsMap.get("date").toString());
        }
        
        // 使用 getOrDefault 避免空指针
        stats.setUploadCount(((Number) statsMap.getOrDefault("upload_count", 0L)).longValue());
        stats.setUploadTraffic(((Number) statsMap.getOrDefault("upload_traffic", 0.0)).doubleValue());
        stats.setDownloadCount(((Number) statsMap.getOrDefault("download_count", 0L)).longValue());
        stats.setDownloadTraffic(((Number) statsMap.getOrDefault("download_traffic", 0.0)).doubleValue());
        
        return stats;
    }

    private AdminStatsVO.ImageStats convertToImageStats(Map<String, Object> statsMap) {
        AdminStatsVO.ImageStats stats = new AdminStatsVO.ImageStats();
        stats.setOriginName((String) statsMap.get("originName"));
        // 计算总下载次数（原图 + 缩略图）
        long originalDownloads = ((Number) statsMap.getOrDefault("originalDownloadCount", 0)).longValue();
        long thumbnailDownloads = ((Number) statsMap.getOrDefault("thumbnailDownloadCount", 0)).longValue();
        stats.setDownloadCount(originalDownloads + thumbnailDownloads);

        // 如果需要计算总流量，但目前 Map 中没有这个数据
        // 可能需要在 SQL 查询中添加流量统计
        stats.setTotalTraffic(((Number) statsMap.getOrDefault("totalTraffic", 0)).doubleValue());
        return stats;
    }

    private AdminStatsVO.UserRankItem convertToUserRankItem(Map<String, Object> map) {
        AdminStatsVO.UserRankItem item = new AdminStatsVO.UserRankItem();
        item.setUsername((String) map.get("username"));
        item.setNickname((String) map.get("nickname"));
        item.setValue(((Number) map.get("value")).doubleValue());
        return item;
    }


    public List<StatsVO.TopDownloadedImage> getTopDownloadedImages(Long userId, int limit) {
        List<Map<String, Object>> results = accessLogMapper.getTopDownloadedImages(userId, limit);
        return results.stream().map(result -> {
            StatsVO.TopDownloadedImage image = new StatsVO.TopDownloadedImage();
            image.setOriginName((String) result.get("originName"));
            Object originalCount = result.get("originalDownloadCount");
            if (originalCount != null) {
                image.setOriginalDownloadCount(((Number) originalCount).longValue());
            }
            Object thumbnailCount = result.get("thumbnailDownloadCount");
            if (thumbnailCount != null) {
                image.setThumbnailDownloadCount(((Number) thumbnailCount).longValue());
            }
            return image;
        }).collect(Collectors.toList());
    }
} 