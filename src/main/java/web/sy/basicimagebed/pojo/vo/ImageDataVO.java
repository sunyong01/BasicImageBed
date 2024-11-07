package web.sy.basicimagebed.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;

@Data
@Schema(description = "图片数据视图对象")
public class ImageDataVO {
    @Schema(description = "图片唯一密钥", example = "abc123")
    private String key;

    @Schema(description = "图片名称", example = "example.jpg")
    private String name;

    @Schema(description = "图片原始名称", example = "example_original.jpg")
    private String origin_name;

    @Schema(description = "图片路径名", example = "/images/example.jpg")
    private String pathname;

    @Schema(description = "图片大小，单位 KB", example = "1024.5")
    private Float size;

    @Schema(description = "图片宽度", example = "800")
    private Integer width;

    @Schema(description = "图片高度", example = "600")
    private Integer height;

    @Schema(description = "图片 md5 值", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String md5;

    @Schema(description = "图片 sha1 值", example = "da39a3ee5e6b4b0d3255bfef95601890afd80709")
    private String sha1;

    @Schema(description = "上传时间(友好格式)", example = "2 hours ago")
    private String human_date;

    @Schema(description = "上传日期(yyyy-MM-dd HH:mm:ss)", example = "2023-10-01 12:34:56")
    private Date date;

    @Schema(description = "链接，与上传接口返回参数中的 links 相同")
    private HashMap<String,String> links;

    @Schema(description = "相册 ID", example = "1")
    private Integer albumId;


}
