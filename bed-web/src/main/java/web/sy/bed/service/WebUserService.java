package web.sy.bed.service;

import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.pojo.entity.User;

import web.sy.bed.base.pojo.entity.UserProfile;
import web.sy.bed.base.pojo.dto.UserSearchDTO;
import web.sy.bed.base.pojo.common.PaginationVO;

public interface WebUserService {

    void register(String username, String password, String email);

    void updatePassword(Long userId, String oldPassword, String newPassword);

    void ForceUpdatePassword(Long userId, String newPassword);

    void updateStatus(Long userId, boolean enabled);

    boolean existsByUsername(String username);

    void deleteUser(Long userId);

    void checkUsernameAndEmailAvailable(String username, String email);

    UserProfile getUserProfile(Long userId);

    ResponseInfo<PaginationVO<User>> searchUsers(UserSearchDTO condition, Integer page, Integer pageSize);

    void setUserRole(Long userId, String roleCode);

    User findById(Long id);

    void updateEmail(Long userId, String email);

    void removeUserRole(Long userId, String roleCode);
}