package web.sy.basicimagebed.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "相册信息视图对象")
public class AlbumsVO {

    @Schema(description = "相册自增 ID", example = "1")
    private Integer id;

    @Schema(description = "相册名称", example = "旅行相册")
    private String name;

    @Schema(description = "相册简介", example = "记录旅行的美好瞬间")
    private String intro;

    @Schema(description = "相册图片数量", example = "50")
    private Integer imageNum;
}