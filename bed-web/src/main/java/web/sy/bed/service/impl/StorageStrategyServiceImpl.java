package web.sy.bed.service.impl;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import web.sy.bed.base.mapper.StrategyMapper;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.pojo.entity.StrategyConfig;
import web.sy.bed.base.utils.ResponseBuilder;
import web.sy.bed.service.StorageStrategyService;
import web.sy.bed.service.StorageStrategySelector;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StorageStrategyServiceImpl implements StorageStrategyService {
    private final StrategyMapper strategyMapper;
    @Lazy
    @Resource
    StorageStrategySelector storageStrategySelector;

    @Override
    public ResponseInfo<List<StrategyConfig>> getAllStrategy() {
        return ResponseBuilder.success(strategyMapper.getAll());
    }

    @Override
    public ResponseInfo<String> createStrategy(StrategyConfig strategyConfig) {
        // 默认设置为不可用
        strategyConfig.setAvailable(false);
        strategyMapper.insert(strategyConfig);
        return ResponseBuilder.success("策略创建成功");
    }

    @Override
    public ResponseInfo<String> updateStrategy(StrategyConfig strategyConfig) {
        // 默认设置为不可用
        strategyConfig.setAvailable(false);
        strategyMapper.update(strategyConfig);
        return ResponseBuilder.success("策略更新成功");
    }

    @Override
    public ResponseInfo<String> deleteStrategy(Integer id) {
        strategyMapper.delete(id.longValue());
        return ResponseBuilder.success("策略删除成功");
    }

    @Override
    public ResponseInfo<String> testStrategy(Integer id) {
        return storageStrategySelector.testStrategy(id) ? ResponseBuilder.success("测试成功,该策略可用") : ResponseBuilder.error("测试失败");
    }
}
