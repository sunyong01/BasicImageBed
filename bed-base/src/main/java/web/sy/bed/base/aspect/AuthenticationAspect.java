package web.sy.bed.base.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import web.sy.bed.base.annotation.RequireAuthentication;
import web.sy.bed.base.exception.AuthenticationException;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationAspect {

    @Before("@annotation(requireAuth)")
    public void checkAuthentication(RequireAuthentication requireAuth) {
        log.debug("Checking authentication for method: {}", requireAuth);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("用户未认证");
        }

        if (requireAuth.requireSuperAdmin() && !hasRole("ROLE_SUPER_ADMIN")) {
            throw new AuthenticationException("需要超级管理员权限");
        }

        if (requireAuth.requireAdmin() && !hasAnyRole("ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            throw new AuthenticationException("需要管理员权限");
        }
    }

    private boolean hasRole(String roleCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals(roleCode));
    }

    private boolean hasAnyRole(String... roleCodes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.getAuthorities().stream()
                   .anyMatch(auth -> {
                       String authority = auth.getAuthority();
                       for (String roleCode : roleCodes) {
                           if (authority.equals(roleCode)) {
                               return true;
                           }
                       }
                       return false;
                   });
    }
} 