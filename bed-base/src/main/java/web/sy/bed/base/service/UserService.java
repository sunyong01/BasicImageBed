package web.sy.bed.base.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import web.sy.bed.base.pojo.entity.User;

public interface UserService extends UserDetailsService {

    User findByUsername(String username);

    User findByUsernameOrEmail(String usernameOrEmail);

    void updateLastLoginTime(String username);

    User findById(Long userId);
}
