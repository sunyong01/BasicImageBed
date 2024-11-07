package web.sy.basicimagebed.pojo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ResponseInfo<T> implements Serializable {
    @Schema(description = "状态码", example = "true")
    private boolean status;
    @Schema(description = "消息", example = "success")
    private String  message;
    @Schema(description = "数据")
    private T 	    data;
}
