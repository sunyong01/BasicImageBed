package web.sy.basicimagebed.utils;

import java.util.UUID;

public class VerifyUtil {
    // 这里使用UUID生成token
    // TODO 改成使用JWT
    public static String makeToken(int count) {
        String RandomPart = UUID.randomUUID().toString().replace("-", "");
        return "Bearer " + count +"|"+ RandomPart;
    }
}
