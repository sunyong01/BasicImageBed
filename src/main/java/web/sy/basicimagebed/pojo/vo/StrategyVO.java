package web.sy.basicimagebed.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "策略信息视图对象")
public class StrategyVO {

    @Schema(description = "策略 ID", example = "1")
    private Integer id;

    @Schema(description = "策略名称", example = "本地存储")
    private String name;
}
