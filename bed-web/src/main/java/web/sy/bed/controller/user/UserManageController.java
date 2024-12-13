package web.sy.bed.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;
import web.sy.base.annotation.RequireAuthentication;
import web.sy.base.pojo.common.PaginationVO;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.dto.UserSearchDTO;
import web.sy.base.pojo.entity.User;
import web.sy.base.service.UserService;
import web.sy.base.utils.ResponseBuilder;
import web.sy.bed.controller.BaseController;
import web.sy.bed.service.ImageService;
import web.sy.bed.service.UserProfileService;
import web.sy.bed.service.WebUserService;
import web.sy.bed.vo.ProfileVO;
import web.sy.bed.vo.req.TokenAuthReqVO;

@RestController
@Tag(name = "用户管理接口", description = "用户管理接口")
@RequestMapping("/api/v1/users")
public class UserManageController extends BaseController {
    private final UserProfileService userProfileService;
    protected final UserService userService;
    protected final WebUserService webUserService;
    private final ImageService imageService;

    public UserManageController(WebUserService webUserService, UserProfileService userProfileService, UserService userService, ImageService imageService) {
        super(userService, webUserService);
        this.webUserService = webUserService;
        this.userProfileService = userProfileService;
        this.userService = userService;
        this.imageService = imageService;
    }

    @PostMapping
    @Operation(summary = "创建新用户", description = "管理员创建新用户")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<String> createUser(@RequestBody @Valid TokenAuthReqVO user) {
        if (user.getEmail() == null || user.getPassword() == null || user.getUsername() == null) {
            return ResponseBuilder.error("Email, password and username are required!");
        }

        webUserService.register(
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
        return ResponseBuilder.success("用户创建成功");
    }

    @GetMapping
    @Operation(summary = "获取用户列表", description = "管理员获取所有用户列表，支持分页和条件搜索")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<PaginationVO<ProfileVO>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false, defaultValue = "1")  @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(0) @Max(50) Integer page_size,
            @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        UserSearchDTO searchDTO = new UserSearchDTO();
        searchDTO.setUsername(username);
        searchDTO.setEmail(email);
        searchDTO.setEnabled(enabled);

        return userProfileService.searchUserProfiles(searchDTO, page, page_size);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取指定用户信息", description = "管理员获取指定用户的详细信息")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<ProfileVO> getUserById(@PathVariable Long userId) {
        ProfileVO profile = userProfileService.getUserProfile(userId);
        if (profile == null) {
            return ResponseBuilder.error("用户不存在");
        }
        return ResponseBuilder.success(profile);
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<String> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean enabled) {
        User targetUser = webUserService.findById(userId);
        if (targetUser == null) {
            return ResponseBuilder.error("用户不存在");
        }

        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(role -> role.getRoleCode().equals("ROLE_ADMIN") ||
                        role.getRoleCode().equals("ROLE_SUPER_ADMIN"));

        if (isTargetAdmin) {
            return ResponseBuilder.error("不能禁用管理员用户");
        }

        webUserService.updateStatus(userId, enabled);
        return ResponseBuilder.success("用户状态更新成功");
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "重置用户密码", description = "管理员重置用户密码")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<String> resetPassword(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "newPassword") @Size(min = 6, max = 30) String newPassword) {
        webUserService.ForceUpdatePassword(userId,  newPassword);
        return ResponseBuilder.success("密码重置成功");
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "管理员删除用户")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<String> deleteUser(@PathVariable Long userId) {
        webUserService.deleteUser(userId);
        return ResponseBuilder.success("用户删除成功");
    }

    @PutMapping("/{userId}/capacity")
    @Operation(summary = "更新用户容量", description = "管理员更新用户容量")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<String> updateUserCapacity(
            @PathVariable Long userId,
            @RequestParam @Min(0) Double capacity) {
        userProfileService.updateCapacity(userId, capacity);
        return ResponseBuilder.success("用户容量更新成功");
    }

    @PutMapping("/{userId}/role/admin")
    @Operation(summary = "切换管理员权限", description = "将用户设置为管理员或取消管理员权限（仅超级管理员可操作）")
    @RequireAuthentication(requireSuperAdmin = true)
    public ResponseInfo<String> setUserAsAdmin(@PathVariable Long userId) {
        User targetUser = webUserService.findById(userId);
        if (targetUser == null) {
            return ResponseBuilder.error("用户不存在");
        }

        boolean isAlreadyAdmin = targetUser.getRoles().stream()
                .anyMatch(role -> role.getRoleCode().equals("ROLE_ADMIN"));

        if (isAlreadyAdmin) {
            webUserService.removeUserRole(userId, "ROLE_ADMIN");
            return ResponseBuilder.success("已取消该用户的管理员权限");
        } else {
            webUserService.setUserRole(userId, "ROLE_ADMIN");
            return ResponseBuilder.success("已将用户设置为管理员");
        }
    }

    @DeleteMapping("/users/{userId}/images")
    @Operation(summary = "删除用户的所有图片和相册", description = "管理员删除用户的所有图片和相册")
    @RequireAuthentication(requireSuperAdmin = true)
    public ResponseInfo<String> deleteUserImages(@PathVariable Long userId) {
        imageService.deleteAllUserImages(userId);
        return ResponseBuilder.success("删除了用户的所有照片和相册");
    }
}