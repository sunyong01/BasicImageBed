package web.sy.base.pojo.dto;

import lombok.Data;

@Data
public class SystemConfigVO {
    private String siteName;
    private String serverUrl;
    private Integer maxSize;
    private String allowedTypes;
    private Boolean allowGuest;
    private Boolean allowRegister;
    private String jwtSecret;
    private Long jwtExpiration;
    private Boolean debug;
    private Boolean allowEmptyReferer;
    private String[] allowedReferers;
    private Boolean enableRefererControl;
    private Boolean enableDefaultSwaggerAccess;
} 