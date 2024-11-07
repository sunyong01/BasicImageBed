package web.sy.basicimagebed.service.impl;

import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import web.sy.basicimagebed.mapper.AlbumsMapper;
import web.sy.basicimagebed.mapper.TokenMapper;
import web.sy.basicimagebed.mapper.UserMapper;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.dao.UserDAO;
import web.sy.basicimagebed.pojo.vo.ProfileVO;
import web.sy.basicimagebed.service.ImageService;
import web.sy.basicimagebed.service.UserService;
import web.sy.basicimagebed.utils.ResponseBuilder;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    TokenMapper tokenMapper;

    @Resource
    AlbumsMapper albumMapper;

    @Resource
    ImageService imageService;

    @Override
    public ResponseInfo<String> createUser(UserDAO userDAO) {
        // 每个用户默认给1GB的容量
        userDAO.setCapacity(1024 * 1024);
        userDAO.setCapacityUsed(0);
        boolean created = userMapper.createUser(userDAO);
        if (created) {
            return ResponseBuilder.success("账户创建成功");
        } else {
            return ResponseBuilder.error("账户创建失败");
        }
    }

    @Override
    public ResponseInfo<String> deleteUser(String email) {
        //删除用户之前先清空他的token
        tokenMapper.deleteToken(email);
        boolean deleted = userMapper.deleteUser(email);
        //删除用户之前先清空他的相册
        albumMapper.deleteAlbumsByUserEmail(email);
        //删除用户的所有图片
        imageService.deleteImagesByUserEmail(email);
        if (deleted) {
            return ResponseBuilder.success("账户删除成功");
        } else {
            return ResponseBuilder.error("账户删除失败");
        }
    }

    @Override
    public ResponseInfo<String> updateUser(UserDAO userDAO) {
        int updated = userMapper.updateUser(userDAO);
        if (updated > 0) {
            return ResponseBuilder.success("账户更新成功");
        } else {
            return ResponseBuilder.error("账户更新失败");
        }

    }

    @Override
    public ResponseInfo<ProfileVO> getUserProfile(String email) {
        UserDAO userDAO = userMapper.getUserByEmail(email);
        if (userDAO == null) {
            return ResponseBuilder.error("用户不存在");
        }
        ProfileVO profileVO = userDAO.toProfileVO();
        // 容量
        return ResponseBuilder.success(profileVO);
    }

    @Override
    public ResponseInfo<String> changePassword(String email, String oldPassword, String newPassword) {

        return null;
    }
}
