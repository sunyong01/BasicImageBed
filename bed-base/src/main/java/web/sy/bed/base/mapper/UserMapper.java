package web.sy.bed.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import web.sy.bed.base.pojo.dto.UserSearchDTO;
import web.sy.bed.base.pojo.entity.User;


import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    User findByEmail(String email);
    void insert(User user);
    void update(User user);
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);
    void updateStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);
    void updateLastLoginTime(@Param("username") String username);
    void delete(Long userId);
    boolean existsByUsername(String username);
    User findById(Long id);
    
    /**
     * 条件搜索用户
     * @param condition 搜索条件
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 用户列表
     */
    List<User> searchUsers(
        @Param("condition") UserSearchDTO condition,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );
    
    /**
     * 统计符合条件的用户数量
     */
    int countSearchUsers(@Param("condition") UserSearchDTO condition);
    
    /**
     * 插入用户角色关系
     */
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 统计总用户数
     */
    long countUsers();
    
    /**
     * 统计活跃用户数量（enabled = true）
     */
    long countActiveUsers();
    
    /**
     * 根据角色代码获取角色ID
     */
    Long getRoleIdByCode(@Param("roleCode") String roleCode);
    
    /**
     * 删除用户角色关系
     */
    void deleteUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 检查用户是否有指定角色
     */
    int checkUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
} 