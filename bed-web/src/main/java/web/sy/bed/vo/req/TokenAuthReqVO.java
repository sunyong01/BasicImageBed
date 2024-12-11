package web.sy.bed.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "认证请求对象")
public class TokenAuthReqVO {

    @Size(min = 4, max = 30)
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "邮箱", example = "admin@example.com")
    @Email
    private String email;

    @Size(min = 4, max = 30)
    @Schema(description = "密码", example = "password123")
    private String password;
}
