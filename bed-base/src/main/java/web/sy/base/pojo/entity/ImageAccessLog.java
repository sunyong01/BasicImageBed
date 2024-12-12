package web.sy.base.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.sy.base.pojo.enums.OperationType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAccessLog {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 图片路径
     */
    private String pathname;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 文件大小 (KB)
     */
    private Double fileSize;
    
    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private OperationType operationType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 