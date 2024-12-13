package web.sy.base.pojo.entity;

import lombok.Data;
import web.sy.base.pojo.dto.SystemConfigVO;

@Data
public class SystemConfig {
    private String siteName = "我的图床";
    private String serverUrl = "http://localhost:8080/";
    private Integer maxSize = 10240;
    private String allowedTypes = "jpg,jpeg,png,gif,bmp,webp";
    private Boolean allowGuest = false;
    private Boolean allowRegister = false;
    private String jwtSecret;
    private Long jwtExpiration = 86400L;
    private Boolean debug = false;
    private Boolean allowEmptyReferer = true;
    private String[] allowedReferers = new String[0];
    private Boolean enableRefererControl = false;
    private Boolean enableDefaultSwaggerAccess = false;

    public SystemConfigVO toVO() {
        SystemConfigVO vo = new SystemConfigVO();
        vo.setSiteName(siteName);
        vo.setServerUrl(serverUrl);
        vo.setMaxSize(maxSize);
        vo.setAllowedTypes(allowedTypes);
        vo.setAllowGuest(allowGuest);
        vo.setAllowRegister(allowRegister);
        vo.setJwtSecret(jwtSecret);
        vo.setJwtExpiration(jwtExpiration);
        vo.setDebug(debug);
        vo.setAllowEmptyReferer(allowEmptyReferer);
        vo.setAllowedReferers(allowedReferers);
        vo.setEnableRefererControl(enableRefererControl);
        vo.setEnableDefaultSwaggerAccess(enableDefaultSwaggerAccess);
        return vo;
    }
} 
