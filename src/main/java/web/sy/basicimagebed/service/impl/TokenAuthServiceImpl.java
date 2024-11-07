package web.sy.basicimagebed.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import web.sy.basicimagebed.exception.TokenAuthException;
import web.sy.basicimagebed.mapper.TokenMapper;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.dao.UserDAO;
import web.sy.basicimagebed.service.TokenAuthService;
import web.sy.basicimagebed.utils.ResponseBuilder;
import web.sy.basicimagebed.utils.VerifyUtil;


@Service
public class TokenAuthServiceImpl implements TokenAuthService {

    @Resource
    private TokenMapper tokenMapper;

    @Override
    public String  tokenAuth(String token) {
        String userEmailByToken = tokenMapper.getUserEmailByToken(token);
        if (userEmailByToken == null) {
            throw new TokenAuthException("Token不存在");
        }
        return userEmailByToken;
    }

    @Override
    public ResponseInfo<String> createToken(String email, String password) {
        UserDAO user = tokenMapper.getUserByEmailAndPassword(email, password);
        if (user == null) {
            throw new TokenAuthException("用户名或密码错误");
        }
        int count = tokenMapper.countUserToken(email);
        String token = VerifyUtil.makeToken(count);
        tokenMapper.insertToken(token, email);
        return ResponseBuilder.success(token);
    }

    @Override
    public ResponseInfo<String> clearToken(String email) {
        tokenMapper.clearToken(email);
        return ResponseBuilder.success("清除成功");
    }

    @Override
    public ResponseInfo<String> deleteToken(String token) {
        tokenMapper.deleteToken(token);
        return ResponseBuilder.success("删除成功");
    }


}
