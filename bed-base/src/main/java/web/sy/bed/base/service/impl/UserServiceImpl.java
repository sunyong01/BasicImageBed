package web.sy.bed.base.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import web.sy.bed.base.mapper.UserMapper;
import web.sy.bed.base.mapper.UserProfileMapper;
import web.sy.bed.base.pojo.entity.User;
import web.sy.bed.base.pojo.entity.UserProfile;

import web.sy.bed.base.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = findByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        // 先通过用户名查找
        User user = userMapper.findByUsername(usernameOrEmail);
        
        if (user == null) {
            // 如果用户名未找到，则通过邮箱查找
            UserProfile profile = userProfileMapper.findByEmail(usernameOrEmail);
            if (profile != null) {
                user = userMapper.findById(profile.getUserId());
                if (user != null) {
                    // 加载用户的角色和权限
                    user = userMapper.findByUsername(user.getUsername());
                }
            }
        }
        return user;
    }

    @Override
    public void updateLastLoginTime(String username) {
        userMapper.updateLastLoginTime(username);
    }

    @Override
    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

}
