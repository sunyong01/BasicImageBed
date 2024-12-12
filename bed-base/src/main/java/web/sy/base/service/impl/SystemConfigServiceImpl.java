package web.sy.base.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import web.sy.base.event.ConfigChangedEvent;
import web.sy.base.pojo.dto.SystemConfigVO;
import web.sy.base.pojo.entity.SystemConfig;
import web.sy.base.service.SystemConfigService;
import web.sy.base.utils.SQLiteUtils;


@Slf4j
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private final ApplicationEventPublisher eventPublisher;

    private static final String SYSTEM_INITIALIZED = "system.initialized";

    public SystemConfigServiceImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    @Override
    public SystemConfigVO getSystemConfigVO() {
        return SQLiteUtils.getSystemConfig().toVO();
    }

    @Override
    public void updateSystemConfig(SystemConfig config) {
        SQLiteUtils.saveSystemConfig(config);
        eventPublisher.publishEvent(new ConfigChangedEvent(this, "system.config", null));
    }

    @Override
    public boolean isInitialized() {
        String value = SQLiteUtils.getValue("system.initialized");
        return Boolean.parseBoolean(value);
    }

    @Override
    public void setInitialized(boolean initialized) {
        SQLiteUtils.setValue(SYSTEM_INITIALIZED, String.valueOf(initialized));
    }

    @Override
    public String getConfigValue(String key) {
        return SQLiteUtils.getValue(key);
    }

    @Override
    public void setConfigValue(String key, String value) {
        SQLiteUtils.setValue(key, value);
        eventPublisher.publishEvent(new ConfigChangedEvent(this, key, value));
    }

    @Override
    public void updateSystemConfig(SystemConfigVO vo) {
        SystemConfig config = new SystemConfig();
        
        // 从VO复制属性到实体
        config.setSiteName(vo.getSiteName());
        config.setServerUrl(vo.getServerUrl());
        config.setMaxSize(vo.getMaxSize());
        config.setAllowedTypes(vo.getAllowedTypes());
        config.setAllowGuest(vo.getAllowGuest());
        config.setAllowRegister(vo.getAllowRegister());
        config.setJwtSecret(vo.getJwtSecret());
        config.setJwtExpiration(vo.getJwtExpiration());
        config.setDebug(vo.getDebug());
        config.setAllowEmptyReferer(vo.getAllowEmptyReferer());
        config.setAllowedReferers(vo.getAllowedReferers());
        config.setEnableRefererControl(vo.getEnableRefererControl());
        config.setEnableDefaultSwaggerAccess(vo.getEnableDefaultSwaggerAccess());
        // 保存配置并触发事件
        SQLiteUtils.setValue("system.debug", String.valueOf(vo.getDebug()));
        updateSystemConfig(config);
    }
}