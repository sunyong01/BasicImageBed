package web.sy.bed.base.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色信息")
public class Role {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "角色描述")
    private String description;
} 