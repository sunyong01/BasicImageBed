package web.sy.bed.vo.resp;

import lombok.Data;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.base.pojo.dto.SystemConfigVO;

@Data
public class FrontendConfig {
    private Boolean systemInitialized;
    private Boolean allowRegister;
    private Boolean allowGuest;
    private String maxUploadSize;
    private String allowedSuffixes;
    private String siteName;

    public FrontendConfig fromSystemConfig(){
        this.systemInitialized = GlobalConfig.getInitialized();
        this.allowRegister = GlobalConfig.getConfig().getAllowRegister();
        this.allowGuest = GlobalConfig.getConfig().getAllowGuest();
        this.maxUploadSize = String.valueOf(GlobalConfig.getConfig().getMaxSize());
        this.allowedSuffixes = GlobalConfig.getConfig().getAllowedTypes();
        this.siteName = GlobalConfig.getConfig().getSiteName();
        return this;
    }
}
