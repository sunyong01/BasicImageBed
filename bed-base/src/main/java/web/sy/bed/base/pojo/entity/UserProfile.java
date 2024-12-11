package web.sy.bed.base.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息")
public class UserProfile {
    @Schema(description = "用户信息ID")
    private Long id;
    @Schema(description = "关联的用户ID")
    private Long userId;
    @Schema(description = "用户邮箱")
    private String email;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "个人主页URL")
    private String url;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "注册IP")
    private String registerIp;
    @Schema(description = "用户总容量(KB)")
    private Double capacity;
    @Schema(description = "已使用容量(KB)")
    private Double capacityUsed;
} 