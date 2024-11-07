package web.sy.basicimagebed.controller.use;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.dao.UserDAO;
import web.sy.basicimagebed.pojo.vo.ProfileVO;
import web.sy.basicimagebed.service.UserService;
import web.sy.basicimagebed.utils.LinksUtil;
import web.sy.basicimagebed.utils.ResponseBuilder;

import java.util.HashMap;

@RequestMapping("/user")
@RestController
public class UserController {
    @Resource
    UserService userService;


    @PostMapping("/register")
    ResponseInfo<String> register(HttpServletRequest request, @RequestBody HashMap<String, String> user) {
        String email = user.get("email");
        String password = user.get("password");
        if (email == null || password == null) {
            return ResponseBuilder.error("Email and password are required!");
        }
        UserDAO userDAO = new UserDAO();
        userDAO.setEmail(email);
        userDAO.setPassword(password);
        userDAO.setRegisterIp(LinksUtil.getClientIpAddress(request));
        return userService.createUser(userDAO);
    }

    @PostMapping("/update")
    ResponseInfo<String> update(HttpServletRequest request, @RequestBody ProfileVO profileVO) {
        //TODO 暂未做权限控制。role字段暂未使用
        String email = (String) request.getAttribute("userEmail");

        UserDAO userDAO = new UserDAO();
        // 用户只能更改这些信息
        userDAO.setEmail(email);
        userDAO.setUrl(profileVO.getUrl());
        userDAO.setAvatar(profileVO.getAvatar());
        userDAO.setNickname(profileVO.getName());
//        userDAO.setCapacity(profileVO.getCapacity());
        return userService.updateUser(userDAO);
    }

    @PostMapping("/changePassword")
    ResponseInfo<String> changePassword(@RequestBody HashMap<String, String> user) {
        String email = user.get("email");
        String oldPassword = user.get("oldPassword");
        String newPassword = user.get("newPassword");
        if (email == null || oldPassword == null || newPassword == null) {
            return ResponseBuilder.error("Email, old password and new password are required!");
        }
        return userService.changePassword(email, oldPassword, newPassword);
    }

    @DeleteMapping("/delete")
    ResponseInfo<String> delete(HttpServletRequest request,@RequestParam String email) {
        String emailFromToken = (String) request.getAttribute("userEmail");
        if (!email.equals(emailFromToken)) {
            return ResponseBuilder.error("Email is not match!");
        }
        return userService.deleteUser(email);
    }

}
