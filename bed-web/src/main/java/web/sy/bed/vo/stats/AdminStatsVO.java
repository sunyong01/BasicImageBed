package web.sy.bed.vo.stats;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理员统计数据")
public class AdminStatsVO {
    
    @Schema(description = "今日统计")
    private DailyStats todayStats;
    
    @Schema(description = "最近一个月统计")
    private List<DailyStats> monthlyStats;
    
    @Schema(description = "所有时间统计")
    private List<DailyStats> allTimeStats;
    
    @Schema(description = "系统概览")
    private SystemOverview systemOverview;
    
    @Schema(description = "热门图片")
    private PopularImages popularImages;
    
    @Schema(description = "用户排行榜")
    private UserRankings userRankings;
    
    @Data
    @Schema(description = "每日统计数据")
    public static class DailyStats {
        @Schema(description = "日期")
        private String date;
        
        @Schema(description = "上传次数")
        private long uploadCount;
        
        @Schema(description = "上传流量(KB)")
        private double uploadTraffic;
        
        @Schema(description = "下载次数")
        private long downloadCount;
        
        @Schema(description = "下载流量(KB)")
        private double downloadTraffic;
    }
    
    @Data
    @Schema(description = "系统概览")
    public static class SystemOverview {
        @Schema(description = "总用户数")
        private long totalUsers;
        
        @Schema(description = "启用的用户数")
        private long activeUsers;
        
        @Schema(description = "总图片数")
        private long totalImages;
        
        @Schema(description = "总相册数")
        private long totalAlbums;
    }
    
    @Data
    @Schema(description = "热门图片")
    public static class PopularImages {
        @Schema(description = "下载次数最多的图片")
        private List<ImageStats> topDownloaded;
        
        @Schema(description = "总流量最大的图片")
        private List<ImageStats> topTraffic;
    }
    
    @Data
    @Schema(description = "图片统计")
    public static class ImageStats {
        @Schema(description = "图片原始名称")
        private String originName;
        
        @Schema(description = "下载次数")
        private long downloadCount;
        
        @Schema(description = "总流量(KB)")
        private double totalTraffic;
    }
    
    @Data
    @Schema(description = "用户排行榜")
    public static class UserRankings {
        @Schema(description = "图片数量排行")
        private List<UserRankItem> imageCountRanking;
        
        @Schema(description = "流量排行")
        private List<UserRankItem> trafficRanking;
        
        @Schema(description = "存储空间排行")
        private List<UserRankItem> storageRanking;
        
        @Schema(description = "下载次数排行")
        private List<UserRankItem> downloadCountRanking;
    }
    
    @Data
    @Schema(description = "用户排行项")
    public static class UserRankItem {
        @Schema(description = "用户名")
        private String username;
        
        @Schema(description = "用户昵称")
        private String nickname;
        
        @Schema(description = "统计值")
        private double value;
    }
} 