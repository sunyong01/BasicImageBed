package web.sy.basicimagebed.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;

@Data
@Schema(description = "上传图片响应视图对象")
public class UploadImageRespVO {

    @Schema(description = "图片唯一密钥", example = "abc123")
    private String key;

    @Schema(description = "图片名称", example = "example.jpg")
    private String name;

    @Schema(description = "图片路径名", example = "/images/example.jpg")
    private String pathname;

    @Schema(description = "图片原始名", example = "example_original.jpg")
    private String originName;

    @Schema(description = "图片大小，单位 KB", example = "1024.5")
    private Float size;

    @Schema(description = "图片类型", example = "image/jpeg")
    private String mimetype;

    @Schema(description = "图片拓展名", example = "jpg")
    private String extension;

    @Schema(description = "图片 md5 值", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String md5;

    @Schema(description = "图片 sha1 值", example = "da39a3ee5e6b4b0d3255bfef95601890afd80709")
    private String sha1;

    @Schema(description = "链接")
    private HashMap<String,String> links;
}