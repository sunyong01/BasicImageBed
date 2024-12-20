package web.sy.base.service;


import web.sy.base.pojo.dto.SystemConfigVO;
import web.sy.base.pojo.entity.SystemConfig;

public interface SystemConfigService {

    /**
     * 更新系统配置
     */
    void updateSystemConfig(SystemConfigVO config);
    /**
     * 获取系统配置
     */
    SystemConfigVO getSystemConfigVO();

    void updateSystemConfig(SystemConfig config);

    boolean isInitialized();

    void setInitialized(boolean initialized);


    String getConfigValue(String key);

    void setConfigValue(String key, String value);
}