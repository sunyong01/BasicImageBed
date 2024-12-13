package web.sy.bed.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import web.sy.base.annotation.Anonymous;
import web.sy.base.annotation.AnonymousRateLimit;
import web.sy.base.config.GlobalConfig;
import web.sy.base.exception.RegisterPolicyException;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.bed.service.AuthService;
import web.sy.bed.service.WebUserService;
import web.sy.bed.vo.req.TokenAuthReqVO;
import web.sy.bed.vo.resp.AuthResponse;

import java.util.concurrent.TimeUnit;


@Anonymous
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证接口", description = "处理用户登录、登出等认证操作")
@AnonymousRateLimit(count = 100, time = 1, timeUnit = TimeUnit.MINUTES, prefix = "auth")
public class AuthController {

    @Resource
    private WebUserService webUserService;

    @Resource
    private AuthService authService;
    
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "401", description = "登录失败")
    })
    @PostMapping("/login")
    @AnonymousRateLimit(count = 5, time = 1, timeUnit = TimeUnit.MINUTES, prefix = "login")
    public ResponseInfo<AuthResponse> login(@RequestBody @Valid TokenAuthReqVO loginReq) {
        return authService.login(loginReq);
    }
    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping("/register")
    @AnonymousRateLimit(count = 3, time = 1, timeUnit = TimeUnit.MINUTES, prefix = "register")
    public ResponseInfo<AuthResponse> register(@RequestBody @Valid TokenAuthReqVO registerReq) {
        Boolean allowRegister = GlobalConfig.getConfig().getAllowRegister();
        if (!allowRegister){
            throw new RegisterPolicyException("当前系统不允许注册新用户");
        }
        webUserService.register(registerReq.getUsername(), registerReq.getPassword(), registerReq.getEmail());
        return authService.login(registerReq);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public ResponseInfo<String> logout() {
        return authService.logout();
    }

    @PostMapping("/refresh")
    @AnonymousRateLimit(count = 10, time = 1, timeUnit = TimeUnit.MINUTES, prefix = "refresh")
    public ResponseInfo<String> refreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

} 