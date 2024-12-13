package web.sy.bed.vo.resp;

import lombok.Data;
import web.sy.base.config.GlobalConfig;

@Data
public class FrontendConfig {
    private Boolean systemInitialized;
    private Boolean allowRegister;
    private Boolean allowGuest;
    private String maxUploadSize;
    private String allowedSuffixes;
    private String siteName;
    private String serverUrl;

    public FrontendConfig fromSystemConfig(){
        this.systemInitialized = GlobalConfig.getInitialized();
        this.allowRegister = GlobalConfig.getConfig().getAllowRegister();
        this.allowGuest = GlobalConfig.getConfig().getAllowGuest();
        this.maxUploadSize = String.valueOf(GlobalConfig.getConfig().getMaxSize());
        this.allowedSuffixes = GlobalConfig.getConfig().getAllowedTypes();
        this.siteName = GlobalConfig.getConfig().getSiteName();
        this.serverUrl = GlobalConfig.getConfig().getServerUrl();
        return this;
    }
}
