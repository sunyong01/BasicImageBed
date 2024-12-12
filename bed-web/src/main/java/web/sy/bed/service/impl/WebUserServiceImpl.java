package web.sy.bed.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sy.base.exception.UserException;
import web.sy.base.mapper.UserMapper;
import web.sy.base.mapper.UserProfileMapper;
import web.sy.base.pojo.common.PaginationVO;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.dto.UserSearchDTO;
import web.sy.base.pojo.entity.User;
import web.sy.base.pojo.entity.UserProfile;
import web.sy.base.utils.PaginationUtil;
import web.sy.base.utils.ResponseBuilder;
import web.sy.bed.service.WebUserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebUserServiceImpl implements WebUserService {

    private static final double DEFAULT_CAPACITY = 1024.0 * 1024.0; // 默认1GB空间
    
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void register(String username, String password, String email) {
        checkUsernameAndEmailAvailable(username, email);

        User user = createUser(username, password);
        userMapper.insert(user);

        UserProfile profile = createUserProfile(user.getId(), email);
        userProfileMapper.insert(profile);

        // 设置用户为普通用户角色
        userMapper.insertUserRole(user.getId(), 3L);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserException("旧密码错误");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    public void ForceUpdatePassword(Long userId, String newPassword) {
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    public void updateEmail(Long userId, String email) {
        // 检查新邮箱是否已被其他用户使用
        UserProfile existingProfile = userProfileMapper.findByEmail(email);
        if (existingProfile != null && !existingProfile.getUserId().equals(userId)) {
            throw new UserException("该邮箱已被其他用户使用");
        }

        UserProfile currentProfile = getUserProfileById(userId);

        // 如果新邮箱与当前邮箱相同，直接返回
        if (email.equals(currentProfile.getEmail())) {
            return;
        }

        userProfileMapper.updateEmail(userId, email);
    }

    @Override
    public ResponseInfo<PaginationVO<User>> searchUsers(UserSearchDTO condition, Integer page, Integer pageSize) {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        
        List<User> users = userMapper.searchUsers(condition, offset, pageSize);
        int total = userMapper.countSearchUsers(condition);

        return ResponseBuilder.success(PaginationUtil.buildPaginationVO(users, page, pageSize, total));
    }

    @Override
    public void setUserRole(Long userId, String roleCode) {
        Long roleId = getRoleIdByCode(roleCode);
        userMapper.insertUserRole(userId, roleId);
    }

    @Override
    public void removeUserRole(Long userId, String roleCode) {
        Long adminRoleId = getRoleIdByCode(roleCode);
        Long userRoleId = getRoleIdByCode("ROLE_USER");

        // 删除管理员角色
        userMapper.deleteUserRole(userId, adminRoleId);

        // 添加普通用户角色（如果不存在）
        if (!hasUserRole(userId, userRoleId)) {
            userMapper.insertUserRole(userId, userRoleId);
        }
    }

    @Override
    public void updateStatus(Long userId, boolean enabled) {
        getUserById(userId);  // 检查用户是否存在
        userMapper.updateStatus(userId, enabled);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public void deleteUser(Long userId) {
        getUserById(userId);  // 检查用户是否存在
        userMapper.updateStatus(userId, false);
    }

    @Override
    public UserProfile getUserProfile(Long userId) {
        return getUserProfileById(userId);  // 使用已有的辅助方法
    }

    @Override
    public User findById(Long id) {
        return getUserById(id);  // 使用已有的辅助方法
    }

    // 辅助方法
    private User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCreateTime(LocalDateTime.now());
        return user;
    }

    private UserProfile createUserProfile(Long userId, String email) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setEmail(email);
        profile.setCapacity(DEFAULT_CAPACITY);
        profile.setCapacityUsed(0.0);
        return profile;
    }

    private User getUserById(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new UserException("用户不存在");
        }
        return user;
    }

    private UserProfile getUserProfileById(Long userId) {
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            throw new UserException("用户信息不存在");
        }
        return profile;
    }

    private Long getRoleIdByCode(String roleCode) {
        Long roleId = userMapper.getRoleIdByCode(roleCode);
        if (roleId == null) {
            throw new UserException("角色不存在: " + roleCode);
        }
        return roleId;
    }

    private boolean hasUserRole(Long userId, Long roleId) {
        return userMapper.checkUserRole(userId, roleId) > 0;
    }

    @Override
    public void checkUsernameAndEmailAvailable(String username, String email) {
        if (existsByUsername(username)) {
            throw new UserException("用户名已存在");
        }
        if (userProfileMapper.existsByEmail(email)) {
            throw new UserException("邮箱已被使用");
        }
    }
} 