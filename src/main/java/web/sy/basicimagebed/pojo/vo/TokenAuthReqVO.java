package web.sy.basicimagebed.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Token 鉴权请求视图对象")
public class TokenAuthReqVO {
    @Schema(description = "邮箱", example = "abc@demo.com")
    private String email;
    @Schema(description = "密码", example = "123456")
    private String password;
}
