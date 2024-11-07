package web.sy.basicimagebed.service;

public interface VerifyService {
    String doVerify(Integer routeId, String RawData, String Signature);

    String getKey();
}
