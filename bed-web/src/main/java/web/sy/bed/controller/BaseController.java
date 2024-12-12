package web.sy.bed.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import web.sy.base.exception.AuthenticationException;
import web.sy.base.pojo.entity.User;
import web.sy.base.pojo.entity.UserProfile;
import web.sy.base.service.UserService;
import web.sy.bed.service.WebUserService;

public abstract class BaseController {
    
    private final UserService userService;
    private final WebUserService webUserService;

    public BaseController(UserService userService, WebUserService webUserService) {
        this.userService = userService;
        this.webUserService = webUserService;
    }

    /**
     * 从安全上下文中获取当前认证用户的ID
     *
     * @return 用户ID
     * @throws AuthenticationException 当用户未认证或不存在时
     */
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("用户未认证");
        }

        String username = authentication.getName();
        try {
            User user = userService.findByUsername(username);
            return user.getId();
        } catch (Exception e) {
            throw new AuthenticationException("用户不存在");
        }
    }

    /**
     * 从安全上下文中获取当前认证用户的用户名
     *
     * @return 用户名
     * @throws AuthenticationException 当用户未认证时
     */
    protected String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("用户未认证");
        }
        return authentication.getName();
    }

    /**
     * 从安全上下文中获取当前认证用户的邮箱
     *
     * @return 用户邮箱
     * @throws AuthenticationException 当用户未认证或不存在时
     */
    protected String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("用户未认证");
        }

        String username = authentication.getName();
        try {
            User user = userService.findByUsername(username);
            UserProfile profile = webUserService.getUserProfile(user.getId());
            if (profile == null || profile.getEmail() == null) {
                throw new AuthenticationException("用户邮箱不存在");
            }
            return profile.getEmail();
        } catch (Exception e) {
            throw new AuthenticationException("获取用户邮箱失败");
        }
    }
    /**
     * 从安全上下文中获取当前认证用户的角色是否是管理或超级管理
     *
     * @return boolean true or false
     * @throws AuthenticationException 当用户未认证时
     */
    protected boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("用户未认证");
        }
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }


} 