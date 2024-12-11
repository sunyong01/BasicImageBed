package web.sy.bed.vo.stats;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "统计数据")
public class StatsVO {
    
    @Schema(description = "上传统计")
    private UploadStats uploadStats;
    
    @Schema(description = "下载统计")
    private DownloadStats downloadStats;
    
    @Schema(description = "总体统计")
    private TotalStats totalStats;
    
    @Schema(description = "存储空间统计")
    private StorageStats storageStats;
    
    @Schema(description = "图片类型分布")
    private Map<String, Long> imageTypeDistribution;
    
    @Schema(description = "访问趋势")
    private List<AccessTrendItem> accessTrend;
    
    @Schema(description = "下载次数最多的图片")
    private List<TopDownloadedImage> topDownloadedImages;
    
    @Schema(description = "最近下载的图片")
    private List<RecentDownloadedImage> recentDownloadedImages;
    
    @Data
    public static class UploadStats {
        @Schema(description = "原图上传总数")
        private long originalTotalCount;
        @Schema(description = "缩略图上传总数")
        private long thumbnailTotalCount;
        @Schema(description = "上传总大小(KB)")
        private double totalSize;
        @Schema(description = "平均大小(KB)")
        private double averageSize;
    }
    
    @Data
    public static class DownloadStats {
        @Schema(description = "原图下载总数")
        private long originalTotalCount;
        @Schema(description = "缩略图下载总数")
        private long thumbnailTotalCount;
        @Schema(description = "下载总流量(KB)")
        private double totalTraffic;
        @Schema(description = "平均每次下载大小(KB)")
        private double averageSize;
    }
    
    @Data
    public static class TotalStats {
        @Schema(description = "总图片数")
        private long totalImages;
        
        @Schema(description = "总访问次数")
        private long totalAccess;
    }
    
    @Data
    public static class StorageStats {
        @Schema(description = "总容量(KB)")
        private double totalCapacity;
        @Schema(description = "已用容量(KB)")
        private double usedCapacity;
        @Schema(description = "用率")
        private double usageRate;
    }
    
    @Data
    public static class AccessTrendItem {
        @Schema(description = "日期")
        private String date;
        @Schema(description = "原图上传次数")
        private long originalUploadCount;
        @Schema(description = "缩略图上传次数")
        private long thumbnailUploadCount;
        @Schema(description = "原图下载次数")
        private long originalDownloadCount;
        @Schema(description = "缩略图下载次数")
        private long thumbnailDownloadCount;
    }
    
    @Data
    public static class TopDownloadedImage {
        @Schema(description = "图片原始名称")
        private String originName;
        @Schema(description = "原图下载次数")
        private long originalDownloadCount;
        @Schema(description = "缩略图下载次数")
        private long thumbnailDownloadCount;
    }
    
    @Data
    public static class RecentDownloadedImage {
        @Schema(description = "图片原始名称")
        private String originName;
        @Schema(description = "下载时间")
        private LocalDateTime downloadTime;
        @Schema(description = "是否为缩略图")
        private boolean isThumbnail;
    }
} 