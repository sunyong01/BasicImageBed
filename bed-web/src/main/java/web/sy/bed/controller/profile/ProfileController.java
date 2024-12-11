package web.sy.bed.controller.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.bind.annotation.*;
import web.sy.bed.base.annotation.ApiTokenSupport;
import web.sy.bed.base.annotation.RateLimit;
import web.sy.bed.controller.BaseController;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.service.WebUserService;
import web.sy.bed.vo.ProfileVO;
import web.sy.bed.service.UserProfileService;
import web.sy.bed.base.service.UserService;
import web.sy.bed.base.utils.ResponseBuilder;
import web.sy.bed.base.annotation.RequireAuthentication;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Tag(name = "用户档案接口", description = "用户个人信息管理相关接口")
@RestController
@RequestMapping("/api/v1/profile")
@RateLimit(count = 3, time = 1, timeUnit = TimeUnit.SECONDS, prefix = "profile",limitByUser = true)
public class ProfileController extends BaseController {
    private final UserProfileService userProfileService;

    private final WebUserService webUserService;

    public ProfileController(UserService userService, WebUserService webUserService, UserProfileService userProfileService) {
        super(userService,webUserService);
        this.webUserService = webUserService;
        this.userProfileService = userProfileService;
    }

    @Operation(summary = "获取当前用户档案", description = "获取当前登录用户的个人档案信息")
    @GetMapping
    @ApiTokenSupport
    @RequireAuthentication
    public ResponseInfo<ProfileVO> getCurrentProfile() {
        Long userId = getCurrentUserId();
        ProfileVO profile = userProfileService.getUserProfile(userId);
        if (profile == null) {
            return ResponseBuilder.error("用户信息不存在");
        }
        return ResponseBuilder.success(profile);
    }

    @Operation(summary = "更新用户档案", description = "更新当前登录用户的个人档案信息")
    @PutMapping
    @RequireAuthentication
    public ResponseInfo<String> updateProfile(@RequestBody @Valid ProfileVO profileVO) {
        Long userId = getCurrentUserId();
        userProfileService.updateProfile(userId, profileVO);
        return ResponseBuilder.success("个人信息更新成功");
    }

    @Operation(summary = "更新用户邮箱", description = "更新当前登录用户的邮箱地址")
    @PutMapping("/email")
    @RequireAuthentication
    public ResponseInfo<String> updateEmail(@RequestParam @Email String email) {
        Long userId = getCurrentUserId();
        userProfileService.updateEmail(userId, email);
        return ResponseBuilder.success("邮箱更新成功");
    }

    @Operation(summary = "更新用户头像", description = "更新当前登录用户的头像URL")
    @PutMapping("/avatar")
    @RequireAuthentication
    public ResponseInfo<String> updateAvatar(@RequestParam @URL String avatarUrl) {
        Long userId = getCurrentUserId();
        userProfileService.updateAvatar(userId, avatarUrl);
        return ResponseBuilder.success("头像更新成功");
    }

    @Operation(summary = "获取已用容量", description = "用户获取已用容量 KB")
    @GetMapping("/capacity")
    @RequireAuthentication
    public ResponseInfo<Double> getUsedCapacity() {
        Long userId = getCurrentUserId();
        double usedCapacity = userProfileService.getUsedCapacity(userId);
        return ResponseBuilder.success(usedCapacity);
    }

    @Operation(summary = "修改密码", description = "用户操作修改密码")
    @PutMapping("/password")
    public ResponseInfo<String> updatePassword(@RequestBody HashMap<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");
        Long currentUserId = getCurrentUserId();
        webUserService.updatePassword(currentUserId, oldPassword, newPassword);
        return ResponseBuilder.success("密码修改成功");
    }

} 