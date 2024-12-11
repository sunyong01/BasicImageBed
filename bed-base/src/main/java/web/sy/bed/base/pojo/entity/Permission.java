package web.sy.bed.base.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限信息")
public class Permission {
    
    @Schema(description = "权限ID")
    private Long id;
    
    @Schema(description = "权限名称")
    private String permissionName;
    
    @Schema(description = "权限编码")
    private String permissionCode;
    
    @Schema(description = "权限描述")
    private String description;
} 