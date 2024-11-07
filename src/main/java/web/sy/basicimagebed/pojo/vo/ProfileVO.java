package web.sy.basicimagebed.pojo.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息响应对象")
public class ProfileVO {

    @Schema(description = "用户名", example = "John Doe")
    private String name;

    @Schema(description = "头像地址", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "邮箱地址", example = "john.doe@example.com")
    private String email;

    @Schema(description = "总容量", example = "1024.0")
    private Float capacity;

    @Schema(description = "已使用容量", example = "512.0")
    private Float usedCapacity;

    @Schema(description = "个人主页地址", example = "https://example.com/john-doe")
    private String url;

    @Schema(description = "图片数量", example = "100")
    private Integer imageNum;

    @Schema(description = "相册数量", example = "10")
    private Integer albumNum;

    @Schema(description = "注册 IP", example = "192.168.1.1")
    private String registeredIp;
}