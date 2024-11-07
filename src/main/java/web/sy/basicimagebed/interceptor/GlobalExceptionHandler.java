package web.sy.basicimagebed.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import web.sy.basicimagebed.exception.TokenAuthException;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.utils.ResponseBuilder;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    // 打印log
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseInfo<String> handleHttpMessageNotReadableException(
            MissingServletRequestParameterException ex) {
        logger.error("缺少请求参数，{}", ex.getMessage());
        return ResponseBuilder.error("缺少请求参数");
    }

    @ExceptionHandler(TokenAuthException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseInfo<String> handleTokenAuthException(TokenAuthException ex) {
        logger.error("Token认证失败，{}", ex.getMessage());
        return ResponseBuilder.error("Token认证失败");
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseInfo<String> handleRuntimeException(RuntimeException ex) {
        return ResponseBuilder.error(ex.getMessage());
    }

}
