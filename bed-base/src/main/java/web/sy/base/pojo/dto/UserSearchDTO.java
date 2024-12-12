package web.sy.base.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "用户搜索条件")
public class UserSearchDTO {
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "账号是否未过期")
    private Boolean accountNonExpired;
    
    @Schema(description = "密码是否未过期")
    private Boolean credentialsNonExpired;
    
    @Schema(description = "账号是否未锁定")
    private Boolean accountNonLocked;
    
    @Schema(description = "创建时间范围-开始")
    private Date createTimeStart;
    
    @Schema(description = "创建时间范围-结束")
    private Date createTimeEnd;
    
    @Schema(description = "最后登录时间范围-开始")
    private Date lastLoginTimeStart;
    
    @Schema(description = "最后登录时间范围-结束")
    private Date lastLoginTimeEnd;
    
    @Schema(description = "用户ID")
    private Long id;
} 