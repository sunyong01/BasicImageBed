package web.sy.bed.service;

import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.vo.req.TokenAuthReqVO;
import web.sy.bed.vo.resp.AuthResponse;

public interface AuthService {
    ResponseInfo<AuthResponse> login(TokenAuthReqVO loginReq);
    ResponseInfo<String> logout();
    ResponseInfo<String> refreshToken(String refreshToken);
} 