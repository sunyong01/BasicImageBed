package web.sy.base.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "相册实体")
public class Album {
    @Schema(description = "相册ID")
    private Long id;

    @Schema(description = "相册名称")
    private String name;

    @JsonProperty("intro")
    @Schema(description = "相册描述")
    private String description;

    @JsonProperty("is_public")
    @Schema(description = "是否公开")

    private Boolean isPublic = false;

    @JsonProperty("create_time")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonProperty("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;

    @JsonProperty("create_user")
    @Schema(description = "创建者用户名")
    private String username;

    @JsonProperty("image_num")
    @Schema(description = "图片数量")
    private Integer imageCount;

    @JsonProperty("cover_url")
    @Schema(description = "相册封面图片URL")
    private String coverUrl;
} 