package web.sy.base.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;

@Data
@Schema(description = "图片信息实体")
public class ImageInfo {
    @JsonIgnore
    @Schema(description = "图片ID")
    private Long id;

    @Schema(description = "图片唯一密钥")
    private String key;

    @Schema(description = "图片名称")
    private String name;

    @JsonProperty("origin_name")
    @Schema(description = "原始文件名")
    private String originName;

    @Schema(description = "图片路径名")
    private String pathname;

    @Schema(description = "图片大小(KB)")
    private Float size;

    @Schema(description = "图片宽度(px)")
    private Integer width;

    @Schema(description = "图片高度(px)")
    private Integer height;

    @Schema(description = "图片MD5值")
    private String md5;

    @Schema(description = "图片SHA1值")
    private String sha1;

    @Schema(description = "上传日期")
    private Date date;

    @JsonIgnore
    @Schema(description = "存储策略枚举值")
    private Integer strategy;

    @JsonProperty("is_public")
    @Schema(description = "是否公开")
    private Boolean isPublic;

    @JsonProperty("upload_user")
    @Schema(description = "上传用户")
    private String uploadUser;

    @JsonProperty("album_id")
    @Schema(description = "所属相册ID")
    private Integer albumId;

    @JsonProperty("human_date")
    @Schema(description = "人性化时间显示")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String humanDate;

    @Schema(description = "图片链接信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<String, String> links;
} 