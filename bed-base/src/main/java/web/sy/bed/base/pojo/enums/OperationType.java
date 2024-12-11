package web.sy.bed.base.pojo.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "操作类型枚举")
public enum OperationType {
    @Schema(description = "上传")
    UPLOAD("UPLOAD"),
    
    @Schema(description = "下载")
    DOWNLOAD("DOWNLOAD"),

    @Schema(description = "缩略图上传")
    THUMBNAIL_UPLOAD("THUMBNAIL_UPLOAD"),
    
    @Schema(description = "缩略图下载")
    THUMBNAIL_DOWNLOAD("THUMBNAIL_DOWNLOAD");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

}