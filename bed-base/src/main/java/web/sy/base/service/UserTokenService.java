package web.sy.base.service;

import web.sy.base.pojo.entity.UserToken;

import java.util.List;

public interface UserTokenService {
    
    /**
     * 创建新的token
     */
    UserToken createToken(Long userId, String tokenName);
    
    /**
     * 获取用户的所有token
     */
    List<UserToken> getUserTokens(Long userId);
    
    /**
     * 验证API Token
     * @param tokenId Token ID
     * @param token Token值
     * @return 如果验证成功返回UserToken对象,否则返回null
     */
    UserToken validateToken(Long tokenId, String token);
    
    /**
     * 启用/禁用token
     */
    void updateTokenStatus(Long tokenId, boolean enabled);
    
    /**
     * 删除token
     */
    void deleteToken(Long tokenId);
    
    /**
     * 删除用户的所有token
     */
    void deleteUserTokens(Long userId);
    
    /**
     * 根据ID获取token
     */
    UserToken getById(Long tokenId);
}