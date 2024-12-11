package web.sy.storage.strategy.config;

import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.HashMap;

public interface FileStorageConfigFactory<T extends FileStorageProperties.BaseConfig> {
    T getConfig(HashMap<String, String> config);
}
