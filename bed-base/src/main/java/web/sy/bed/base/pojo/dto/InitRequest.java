package web.sy.bed.base.pojo.dto;

import lombok.Data;

@Data
public class InitRequest {
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String localPath;
    private String serverUrl;
    private Integer allowSizeKb;
    private String allowSuffix;
    private Boolean authAllowGuest;
    private Boolean authAllowRegister;
    private String jwtSecret;
    private Integer jwtExpiration;
    private Boolean debug;
    
    private String adminUsername;
    private String adminPassword;
    private String adminEmail;
} 