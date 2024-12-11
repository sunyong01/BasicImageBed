package web.sy.bed.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import web.sy.bed.base.pojo.entity.UserProfile;

import java.util.List;

@Mapper
public interface UserProfileMapper {
    UserProfile findByUserId(Long userId);

    void insert(UserProfile userProfile);

    void update(UserProfile userProfile);

    void updateEmail(@Param("userId") Long userId, @Param("email") String email);

    void updateAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);

    void updateCapacityUsed(@Param("userId") Long userId, @Param("capacityUsed") double capacityUsed);

    boolean existsByEmail(String email);

    /**
     * 获取所有用户信息
     */
    List<UserProfile> findAll();

    /**
     * 更新用户总容量
     */
    void updateCapacity(@Param("userId") Long userId, @Param("capacity") Double capacity);

    UserProfile findByEmail(String email);
} 