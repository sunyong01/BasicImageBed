package web.sy.base.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "数据分析实体")
public class DataAnalysis {
    @Schema(description = "ID")
    private Long id;
    
    @Schema(description = "分析类型")
    private String type;
    
    @Schema(description = "分析值")
    private String value;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 