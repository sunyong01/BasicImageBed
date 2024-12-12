package web.sy.bed.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import web.sy.base.annotation.RateLimit;
import web.sy.base.annotation.RequireAuthentication;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.entity.UserToken;
import web.sy.base.service.UserService;
import web.sy.base.service.UserTokenService;
import web.sy.base.utils.ApiTokenSwaggerGenerator;
import web.sy.base.utils.ResponseBuilder;
import web.sy.bed.controller.BaseController;
import web.sy.bed.service.WebUserService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Tag(name = "API Token接口", description = "API Token管理相关接口")
@RestController
@RequestMapping("/api/v1/tokens")
@RateLimit(count = 10, time = 10, timeUnit = TimeUnit.SECONDS, prefix = "api_tokens", limitByUser = true)
public class ApiController extends BaseController {

    private final UserTokenService userTokenService;
    private final ApiTokenSwaggerGenerator swaggerGenerator;

    public ApiController(UserTokenService userTokenService, UserService userService, WebUserService webUserService, ApiTokenSwaggerGenerator swaggerGenerator) {
        super(userService, webUserService);
        this.userTokenService = userTokenService;
        this.swaggerGenerator = swaggerGenerator;
    }

    @Operation(summary = "创建新的 API Token", description = "创建新的 API Token")
    @PostMapping
    @RequireAuthentication
    public ResponseInfo<UserToken> createToken(@RequestParam @NotNull String tokenName) {
        Long currentUserId = getCurrentUserId();
        UserToken token = userTokenService.createToken(currentUserId, tokenName);
        return ResponseBuilder.success(token);
    }

    @Operation(summary = "获取当前用户的所有 Token", description = "获取当前用户的所有 Token")
    @GetMapping
    @RequireAuthentication
    public ResponseInfo<List<UserToken>> getTokens() {
        Long currentUserId = getCurrentUserId();
        List<UserToken> tokens = userTokenService.getUserTokens(currentUserId);
        return ResponseBuilder.success(tokens);
    }

    @Operation(summary = "启用/禁用 Token", description = "启用或禁用 Token")
    @PutMapping("/{tokenId}/status")
    @RequireAuthentication
    public ResponseInfo<String> updateTokenStatus(
            @PathVariable Long tokenId,
            @RequestParam boolean enabled) {

        Long currentUserId = getCurrentUserId();
        UserToken token = userTokenService.getById(tokenId);

        if (token == null || !token.getUserId().equals(currentUserId)) {
            return ResponseBuilder.error("Token不存在或无权操作");
        }

        userTokenService.updateTokenStatus(tokenId, enabled);
        return ResponseBuilder.success("Token状态已更新");
    }

    @Operation(summary = "删除 Token", description = "删除 Token")
    @DeleteMapping("/{tokenId}")
    @RequireAuthentication
    public ResponseInfo<String> deleteToken(@PathVariable Long tokenId) {
        Long currentUserId = getCurrentUserId();
        UserToken token = userTokenService.getById(tokenId);

        if (token == null || !token.getUserId().equals(currentUserId)) {
            return ResponseBuilder.error("Token不存在或无权操作");
        }

        userTokenService.deleteToken(tokenId);
        return ResponseBuilder.success("Token已删除");
    }

    @Operation(summary = "获取 API Token 文档", description = "获取 API Token 文档")
    @RateLimit(count = 6, time = 2, timeUnit = TimeUnit.SECONDS, prefix = "api_tokens_doc", limitByUser = true)
    @RequireAuthentication
    @GetMapping(value = "/docs")
    public ResponseInfo<OpenAPI> getApiTokenDocs() {
        return ResponseBuilder.success(swaggerGenerator.generateApiTokenSwaggerJson());
    }
}