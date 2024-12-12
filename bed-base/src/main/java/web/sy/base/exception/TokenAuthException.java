package web.sy.base.exception;

//JWT 过期或无效
public class TokenAuthException extends RuntimeException{
    public TokenAuthException(String message) {
        super(message);
    }
}
