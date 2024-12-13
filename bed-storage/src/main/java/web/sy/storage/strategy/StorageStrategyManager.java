package web.sy.storage.strategy;


import lombok.Getter;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.springframework.stereotype.Component;
import web.sy.storage.strategy.config.StrategyConfigBuilderEnum;
import web.sy.storage.strategy.config.StrategyInitPropertiesHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class StorageStrategyManager {

    FileStorageProperties properties;

    @Getter
    FileStorageService fileStorageService;

    /**
     * 初始化
     * @param type
     * @param configMap
     */
    public void init(Integer type, HashMap<String,String> configMap) {
        properties = StrategyInitPropertiesHelper.getInitProperties(type, configMap);
        fileStorageService = FileStorageServiceBuilder.create(properties).useDefault().build();
    }

    public void addStorageStrategy(Integer type, HashMap<String,String> configMap) {
        if (fileStorageService == null) {
            throw new RuntimeException("Base存储策略不存在,请先初始化!");
        }
        CopyOnWriteArrayList<FileStorage> fileStorageList = fileStorageService.getFileStorageList();
        fileStorageList.addAll(StrategyConfigBuilderEnum.getFileStorage(type, configMap));
    }

    public String testStorageStrategy(String platform) {
        if (fileStorageService == null) {
            throw new RuntimeException("Base存储策略不存在,请先初始化!");
        }
        FileInfo upload;
        URL resourceUrl = getClass().getClassLoader().getResource("test.png");
        System.out.println("Resource URL: " + resourceUrl);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.png")){
            if (inputStream == null) {
                throw new RuntimeException("test.jpg not found");
            }
            upload = fileStorageService.of(inputStream).setPlatform(platform).setSaveFilename("test.png").upload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return upload.getUrl();
    }

    public String[] getAllRegisteredPlatforms() {
        return fileStorageService.getFileStorageList().stream().map(FileStorage::getPlatform).toArray(String[]::new);
    }

    public void removeStorageStrategy(String platform) {
        if (fileStorageService == null) {
            throw new RuntimeException("Base存储策略不存在,请先初始化!");
        }
        fileStorageService.getFileStorageList().removeIf(fileStorage -> fileStorage.getPlatform().equals(platform));
    }
}
