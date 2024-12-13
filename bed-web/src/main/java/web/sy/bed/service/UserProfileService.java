package web.sy.bed.service;

import web.sy.base.pojo.common.PaginationVO;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.dto.UserSearchDTO;
import web.sy.bed.vo.ProfileVO;

import java.util.List;

public interface UserProfileService {
    // 用户信息相关方法
    ProfileVO getUserProfile(Long userId);
    void updateProfile(Long userId, ProfileVO profileVO);
    void updateEmail(Long userId, String email);
    void updateAvatar(Long userId, String avatarUrl);
    void updateCapacityUsed(Long userId, double capacityUsed);
    boolean existsByEmail(String email);
    double getUsedCapacity(Long userId);
    double getTotalCapacity(Long userId);

    /**
     * 获取所有用户的信息
     */
    List<ProfileVO> getAllUserProfiles();

    /**
     * 更新用户总容量
     */
    void updateCapacity(Long userId, Double capacity);

    /**
     * 条件搜索用户配置信息
     * @param condition 搜索条件
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页用户配置信息列表
     */
    ResponseInfo<PaginationVO<ProfileVO>> searchUserProfiles(
        UserSearchDTO condition,
        Integer page,
        Integer pageSize
    );
} 