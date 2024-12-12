package web.sy.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import web.sy.base.pojo.entity.StrategyConfig;
import web.sy.base.pojo.enums.Strategy;

import java.util.List;

@Mapper
public interface StrategyMapper {
    // 插入存储策略配置
    int insert(StrategyConfig strategyConfig);
    
    // 更新存储策略配置
    int update(StrategyConfig strategyConfig);
    
    // 根据ID获取存储策略配置
    StrategyConfig getById(@Param("id") Long id);
    
    // 根据策略类型获取存储策略配置
    List<StrategyConfig> getByType(@Param("strategyType") Strategy strategyType);
    
    // 获取所有存储策略配置
    List<StrategyConfig> getAll();
    
    // 删除存储策略配置
    int delete(@Param("id") Long id);
    
    /**
     * 获取所有可用的策略配置，按优先级排序
     */
    List<StrategyConfig> getAvailable();
    
    /**
     * 更新策略的可用状态
     * @param id 策略ID
     * @param available 是否可用
     * @return 影响的行数
     */
    int updateAvailable(@Param("id") Long id, @Param("available") Boolean available);
    
    /**
     * 更新策略已用容量
     * @param id 策略ID
     * @param sizeChangeKb 容量变化（KB），正数表示增加，负数表示减少
     */
    void updateUsedCapacity(@Param("id") Long id, @Param("sizeChangeKb") double sizeChangeKb);
} 