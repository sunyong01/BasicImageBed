package web.sy.bed.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.sy.base.mapper.UserMapper;
import web.sy.base.mapper.UserProfileMapper;
import web.sy.base.pojo.common.PaginationVO;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.dto.UserSearchDTO;
import web.sy.base.pojo.entity.User;
import web.sy.base.pojo.entity.UserProfile;
import web.sy.base.utils.PaginationUtil;
import web.sy.base.utils.ResponseBuilder;
import web.sy.bed.service.UserProfileService;
import web.sy.bed.vo.ProfileVO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileMapper userProfileMapper;
    private final UserMapper userMapper;

    @Override
    public ProfileVO getUserProfile(Long userId) {
        UserProfile profile = userProfileMapper.findByUserId(userId);
        return convertToProfileVO(profile);
    }

    @Override
    public void updateProfile(Long userId, ProfileVO profileVO) {
        // 获取当前用户信息
        UserProfile currentProfile = userProfileMapper.findByUserId(userId);
        if (currentProfile == null) {
            throw new RuntimeException("用户信息不存在");
        }

        // 如果要修改邮箱，检查新邮箱是否已被其他用户使用
        if (profileVO.getEmail() != null && !profileVO.getEmail().equals(currentProfile.getEmail())) {
            UserProfile existingProfile = userProfileMapper.findByEmail(profileVO.getEmail());
            if (existingProfile != null && !existingProfile.getUserId().equals(userId)) {
                throw new IllegalArgumentException("该邮箱已被其他用户使用");
            }
        }

        // 创建更新对象，只包含允许修改的字段
        UserProfile updateProfile = new UserProfile();
        updateProfile.setUserId(userId);
        updateProfile.setEmail(profileVO.getEmail());      // 邮箱
        updateProfile.setNickname(profileVO.getNickname());// 昵称
        updateProfile.setUrl(profileVO.getUrl());          // 个人网站
        updateProfile.setAvatar(profileVO.getAvatar());    // 头像

        // 更新用户信息
        userProfileMapper.update(updateProfile);
    }

    @Override
    public void updateEmail(Long userId, String email) {
        // 检查新邮箱是否已被使用
        if (userProfileMapper.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱已被使用");
        }

        // 获取当前用户信息
        UserProfile currentProfile = userProfileMapper.findByUserId(userId);
        if (currentProfile == null) {
            throw new RuntimeException("用户信息不存在");
        }

        // 如果新邮箱与当前邮箱相同，直接返回
        if (email.equals(currentProfile.getEmail())) {
            return;
        }

        userProfileMapper.updateEmail(userId, email);
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        userProfileMapper.updateAvatar(userId, avatarUrl);
    }

    @Override
    public void updateCapacityUsed(Long userId, double capacityUsed) {
        userProfileMapper.updateCapacityUsed(userId, capacityUsed);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userProfileMapper.existsByEmail(email);
    }

    @Override
    public double getUsedCapacity(Long userId) {
        UserProfile profile = userProfileMapper.findByUserId(userId);
        return profile != null ? profile.getCapacityUsed() : 0.0;
    }

    @Override
    public double getTotalCapacity(Long userId) {
        UserProfile profile = userProfileMapper.findByUserId(userId);
        return profile != null ? profile.getCapacity() : 0.0;
    }

    @Override
    public List<ProfileVO> getAllUserProfiles() {
        List<UserProfile> profiles = userProfileMapper.findAll();
        return profiles.stream()
                .map(this::convertToProfileVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCapacity(Long userId, Double capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("容量不能为负数");
        }
        
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("用户信息不存在");
        }
        
        // 如果新容量小于已使用容量，则不允许更新
        if (capacity < profile.getCapacityUsed()) {
            throw new IllegalArgumentException("新容量不能小于已使用容量");
        }
        
        userProfileMapper.updateCapacity(userId, capacity);
    }

    @Override
    public ResponseInfo<PaginationVO<ProfileVO>> searchUserProfiles(
        UserSearchDTO condition,
        Integer page,
        Integer pageSize
    ) {
        // 移除参数校验，使用PaginationUtil计算偏移量
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        
        // 查询用户数据
        List<User> users = userMapper.searchUsers(condition, offset, pageSize);
        int total = userMapper.countSearchUsers(condition);
        
        // 转换为 ProfileVO
        List<ProfileVO> profiles = users.stream()
            .map(user -> {
                ProfileVO vo = new ProfileVO();
                vo.setUserId(user.getId());
                // 设置用户基本信息
                vo.setUsername(user.getUsername());
                vo.setEnabled(user.isEnabled());
                vo.setAccountNonExpired(user.isAccountNonExpired());
                vo.setCredentialsNonExpired(user.isCredentialsNonExpired());
                vo.setAccountNonLocked(user.isAccountNonLocked());
                vo.setCreateTime(user.getCreateTime());
                vo.setLastLoginTime(user.getLastLoginTime());
                // 设置用户角色信息
                vo.setRoles(user.getRoles());
                
                // 获取并设置用户配置信息
                UserProfile profile = userProfileMapper.findByUserId(user.getId());
                if (profile != null) {
                    vo.setEmail(profile.getEmail());
                    vo.setNickname(profile.getNickname());
                    vo.setUrl(profile.getUrl());
                    vo.setAvatar(profile.getAvatar());
                    vo.setCapacity(profile.getCapacity());
                    vo.setCapacityUsed(profile.getCapacityUsed());
                }
                
                return vo;
            })
            .collect(Collectors.toList());
        
        // 使用PaginationUtil构建分页结果
        return ResponseBuilder.success(PaginationUtil.buildPaginationVO(profiles, page, pageSize, total));
    }

    private ProfileVO convertToProfileVO(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        
        ProfileVO vo = new ProfileVO();
        vo.setUserId(profile.getUserId());
        vo.setEmail(profile.getEmail());
        vo.setNickname(profile.getNickname());
        vo.setUrl(profile.getUrl());
        vo.setAvatar(profile.getAvatar());
        vo.setCapacity(profile.getCapacity());
        vo.setCapacityUsed(profile.getCapacityUsed());
        
        // 获取用户认证信息
        User user = userMapper.findById(profile.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setEnabled(user.isEnabled());
            vo.setAccountNonExpired(user.isAccountNonExpired());
            vo.setCredentialsNonExpired(user.isCredentialsNonExpired());
            vo.setAccountNonLocked(user.isAccountNonLocked());
            vo.setCreateTime(user.getCreateTime());
            vo.setLastLoginTime(user.getLastLoginTime());
            vo.setRoles(user.getRoles());
        }
        
        return vo;
    }
} 