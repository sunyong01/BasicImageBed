package web.sy.bed.service;

import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.entity.StrategyConfig;

import java.util.List;

public interface StorageStrategyService {
    ResponseInfo<List<StrategyConfig>> getAllStrategy();
    ResponseInfo<String> createStrategy(StrategyConfig strategyConfig);
    ResponseInfo<String> updateStrategy(StrategyConfig strategyConfig);
    ResponseInfo<String> deleteStrategy(Integer id);
    ResponseInfo<String> testStrategy(Integer id);
}
