package web.sy.bed.service;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import web.sy.bed.base.config.event.MybatisAvailableEvent;
import web.sy.bed.base.exception.NoAvailableStrategyException;
import web.sy.bed.base.mapper.StrategyMapper;
import web.sy.bed.base.pojo.entity.StrategyConfig;
import web.sy.storage.strategy.StorageStrategyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@Lazy
@Component
public class StorageStrategySelector implements ApplicationListener<MybatisAvailableEvent> {

    @Getter
    @Resource
    private StorageStrategyManager storageStrategyManager;
    @Resource
    private StrategyMapper strategyConfigMapper;

    private List<StrategyConfig> strategyConfigs;

    private boolean initialized = false;

    @Override
    public void onApplicationEvent(MybatisAvailableEvent event) {
        log.info("StorageStrategySelector Loading!");
        loadInit();
    }

    public boolean testStrategy(Integer id) {
        StrategyConfig byId = strategyConfigMapper.getById(id.longValue());
        //测试之前，先初始化
        loadInit();
        String s = storageStrategyManager.testStorageStrategy(byId.getStrategyName());
        byId.setAvailable(s != null);
        strategyConfigMapper.update(byId);
        //测试之后，重新初始化
        loadInit();
        return s != null;
    }

    public boolean testStrategyByName(String name) {
        return null != storageStrategyManager.testStorageStrategy(name);
    }

    public void loadInit() {
        // 从数据库中获取所有的StrategyConfig
        strategyConfigs = strategyConfigMapper.getAll();

        if (strategyConfigs.isEmpty()) {
            throw new NoAvailableStrategyException("存储策略列表为空");
        }

        // 使用第一个StrategyConfig来初始化StorageStrategyManager
        StrategyConfig initialConfig = strategyConfigs.get(0);
        storageStrategyManager.init(initialConfig.getStrategyType().getValue(), initialConfig.getConfigJson());

        // 将其他的StrategyConfig添加到StorageStrategyManager
        for (int i = 1; i < strategyConfigs.size(); i++) {
            StrategyConfig config = strategyConfigs.get(i);
            storageStrategyManager.addStorageStrategy(config.getStrategyType().getValue(), config.getConfigJson());
        }
        initialized = true;
    }

    public HashMap<String, String> getUseStrategyIdAndNameByPriority() {
        if (!initialized) {
            loadInit();
        }

        //strategyConfigs去除没有Available的
        List<StrategyConfig> enabledStrategyConfigs = strategyConfigs.stream()
                .filter(StrategyConfig::getAvailable)
                .toList();
        //在启用的策略中抽取。概率为sortOrder/sum(sortOrder)
        if (enabledStrategyConfigs.isEmpty()) {
            throw new NoAvailableStrategyException("没有配置可用的存储策略");
        }

        int totalSortOrder = enabledStrategyConfigs.stream()
                .mapToInt(StrategyConfig::getSortOrder)
                .sum();

        if (totalSortOrder <= 0) {
            throw new NoAvailableStrategyException("策略优先级和小于等于0");
        }

        // 3. 随机选择
        Random random = new Random();
        int randomNumber = random.nextInt(totalSortOrder) + 1; // [1, totalSortOrder]
        int cumulativeSortOrder = 0;

        for (StrategyConfig config : enabledStrategyConfigs) {
            cumulativeSortOrder += config.getSortOrder();
            if (randomNumber <= cumulativeSortOrder) {
                return new HashMap<>() {{
                    put("StrategyId", config.getId().toString());
                    put("StrategyName", config.getStrategyName());
                }};
            }
        }
        // 如果因为某种原因没有返回，抛出异常或返回默认值
        throw new NoAvailableStrategyException("策略选择失败");
    }

    public FileStorageService getFileStorageService() {
        return storageStrategyManager.getFileStorageService();
    }

    public String getStrategyName(Long strategyId) {
        return strategyConfigMapper.getById(strategyId).getStrategyName();
    }

    /**
     * 更新策略已用容量
     * @param strategyId 策略ID
     * @param sizeChangeKb 容量变化（KB），正数表示增加，负数表示减少
     */
    public void updateStrategyUsedCapacity(Long strategyId, double sizeChangeKb) {
        strategyConfigMapper.updateUsedCapacity(strategyId, sizeChangeKb);
    }

}