package web.sy.basicimagebed.controller.use;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
@RequestMapping("/verify")
@RestController
public class VerifyController {

    @GetMapping("/validate")
    public String doVerify(@RequestParam Integer routeId) {
        return "OK";
    }

    @GetMapping("/aes")
    public ResponseInfo<String> geAESKey() {
        return null;
    }
}
