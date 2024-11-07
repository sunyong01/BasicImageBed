package web.sy.basicimagebed.controller.use;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.vo.ProfileVO;
import web.sy.basicimagebed.pojo.vo.TokenAuthReqVO;
import web.sy.basicimagebed.service.TokenAuthService;
import web.sy.basicimagebed.service.UserService;
import web.sy.basicimagebed.utils.ResponseBuilder;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "User Auth API", description = "Token相关接口")

public class AuthAPIv1 {

        @Resource
        private TokenAuthService tokenAuthService;

        @Resource
        private UserService userService;

        @PostMapping("/tokens")
        public ResponseInfo<String> login(@RequestBody TokenAuthReqVO tokenAuthReqVO) {
            return tokenAuthService.createToken(tokenAuthReqVO.getEmail(), tokenAuthReqVO.getPassword());
        }

        @DeleteMapping("/tokens")
        public ResponseInfo<String> delete(HttpServletRequest request) {
            String email = (String) request.getAttribute("userEmail");
            if (email == null) {
                return ResponseBuilder.error("Token is required!");
            }
            return tokenAuthService.clearToken(email);
        }

        @GetMapping("/profile")
        public ResponseInfo<ProfileVO> profile(HttpServletRequest request) {
            String email = (String) request.getAttribute("userEmail");
            return userService.getUserProfile(email);
        }


}
