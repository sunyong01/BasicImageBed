package web.sy.base.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import web.sy.base.pojo.enums.Strategy;

import java.util.HashMap;


@Data
@Schema(description = "存储策略配置")
public class StrategyConfig {
    @Schema(description = "配置ID")
    private Long id;
    
    @Schema(description = "策略类型")
    private Strategy strategyType;
    
    @Schema(description = "配置JSON")
    private HashMap<String,String> configJson;

    @Min(0)
    @Schema(description = "最大容量(KB)")
    private Double maxCapacity;
    
    @Schema(description = "已用容量(KB)")
    private Double usedCapacity;

    @Size(max = 60)
    @Schema(description = "策略名称")
    private String strategyName;
    
    @Schema(description = "是否可用")
    private Boolean available;

    @Min(0)
    @Max(1000)
    @Schema(description = "优先级(数值越小优先级越高)")
    private Integer sortOrder;
} 