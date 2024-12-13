package web.sy.base.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "相册数据传输对象")
public class AlbumDTO {
    @Size(min = 1, max = 50)
    @Schema(description = "相册名称")
    private String name;

    @Size(min = 1, max = 100)
    @Schema(description = "相册描述")
    private String description;

    @Schema(description = "是否公开", example = "false")
    private Boolean isPublic;

    @Schema(hidden = true)
    private String username;

    @Schema(description = "相册封面图片URL")
    private String coverUrl;
} 