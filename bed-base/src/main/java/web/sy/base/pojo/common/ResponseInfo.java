package web.sy.base.pojo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "通用响应对象")
public class ResponseInfo<T> implements Serializable {
    @Schema(description = "状态", example = "true")
    private boolean status;
    @Schema(description = "消息", example = "操作成功")
    private String  message;
    @Schema(description = "数据")
    private T 	    data;

    public boolean isSuccess() {
        return status;
    }
}
