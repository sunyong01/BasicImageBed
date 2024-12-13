package web.sy.bed.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import web.sy.base.pojo.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "用户个人信息")
public class ProfileVO {
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;

    @Email
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "昵称")
    private String nickname;

    @URL
    @Schema(description = "个人主页URL")
    private String url;

    @URL
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "总容量(KB)")
    private Double capacity;
    
    @Schema(description = "已使用容量(KB)")
    private Double capacityUsed;
    
    @Schema(description = "账号是否可用")
    private boolean enabled;
    
    @Schema(description = "账号是否未过期")
    private boolean accountNonExpired;
    
    @Schema(description = "密码是否未过期")
    private boolean credentialsNonExpired;
    
    @Schema(description = "账号是否未锁定")
    private boolean accountNonLocked;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "用户角色")
    private Set<Role> roles;
}