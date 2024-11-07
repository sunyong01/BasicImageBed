package web.sy.basicimagebed.service;


import web.sy.basicimagebed.pojo.common.ResponseInfo;

public interface TokenAuthService {

     String tokenAuth(String token);

     ResponseInfo<String> createToken(String email, String password);

     ResponseInfo<String> clearToken(String email);

     ResponseInfo<String> deleteToken(String token);
}
