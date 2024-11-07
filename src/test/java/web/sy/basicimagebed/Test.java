package web.sy.basicimagebed;

import lombok.val;
import org.springframework.http.MediaType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
    }
}
