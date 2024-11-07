package web.sy.basicimagebed.service;

import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.dao.UserDAO;
import web.sy.basicimagebed.pojo.vo.ProfileVO;

public interface UserService {
    ResponseInfo<String> createUser(UserDAO  userDAO);
    ResponseInfo<String> deleteUser(String email);
    ResponseInfo<String> updateUser(UserDAO userDAO);
    ResponseInfo<String> changePassword(String email, String oldPassword, String newPassword);
    ResponseInfo<ProfileVO> getUserProfile(String email);
}
