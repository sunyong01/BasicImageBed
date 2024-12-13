package web.sy.base.service.impl;

import org.springframework.stereotype.Service;
import web.sy.base.mapper.UserTokenMapper;
import web.sy.base.pojo.entity.UserToken;
import web.sy.base.service.UserTokenService;

import java.security.SecureRandom;
import java.util.List;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 40;
    private static final SecureRandom random = new SecureRandom();
    private final UserTokenMapper userTokenMapper;

    public UserTokenServiceImpl(UserTokenMapper userTokenMapper) {
        this.userTokenMapper = userTokenMapper;
    }

    public static String generateToken() {
        StringBuilder tokenBuilder = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            tokenBuilder.append(CHARACTERS.charAt(index));
        }
        return tokenBuilder.toString();
    }

    @Override
    public UserToken createToken(Long userId, String tokenName) {
        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setTokenName(tokenName);
        //生成长度为40位的随机字符串
        userToken.setToken(generateToken());
        userToken.setTokenId(userTokenMapper.getNextTokenId(userId));
        userToken.setEnabled(true);

        userTokenMapper.insert(userToken);
        return userToken;
    }

    @Override
    public List<UserToken> getUserTokens(Long userId) {
        return userTokenMapper.getByUserId(userId);
    }

    @Override
    public UserToken validateToken(Long tokenId, String token) {
        return userTokenMapper.findByIdAndToken(tokenId, token);
    }

    @Override
    public void updateTokenStatus(Long tokenId, boolean enabled) {
        userTokenMapper.updateEnabled(tokenId, enabled);
    }

    @Override
    public void deleteToken(Long tokenId) {
        userTokenMapper.deleteById(tokenId);
    }

    @Override
    public void deleteUserTokens(Long userId) {
        userTokenMapper.deleteByUserId(userId);
    }

    @Override
    public UserToken getById(Long tokenId) {
        return userTokenMapper.getById(tokenId);
    }


}