package web.sy.bed.controller.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import web.sy.bed.base.annotation.ApiTokenSupport;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.pojo.entity.StrategyConfig;
import web.sy.bed.service.StorageStrategyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/strategies")
@Tag(name = "存储策略接口", description = "存储策略接口")
public class StrategyController {

    @Resource
    private StorageStrategyService manageService;

    @ApiTokenSupport
    @GetMapping
    @Operation(summary = "获取所有存储策略", description = "获取所有存储策略")
    public ResponseInfo<List<StrategyConfig>> getStrategy() {
        return manageService.getAllStrategy();
    }

    @PostMapping
    @Operation(summary = "创建存储策略", description = "创建存储策略")
    public ResponseInfo<String> createStrategy(@RequestBody @Valid StrategyConfig strategyConfig) {
        return manageService.createStrategy(strategyConfig);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除存储策略", description = "删除存储策略")
    public ResponseInfo<String> deleteStrategy(@PathVariable String id) {
        return manageService.deleteStrategy(Integer.parseInt(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新存储策略", description = "更新存储策略")
    public ResponseInfo<String> updateStrategy(@RequestBody @Valid StrategyConfig strategyConfig) {
        return manageService.updateStrategy(strategyConfig);
    }

    @GetMapping("/{id}/test")
    @Operation(summary = "测试存储策略", description = "测试存储策略")
    public ResponseInfo<String> testStrategy(@PathVariable String id) {
        return manageService.testStrategy(Integer.parseInt(id));
    }
} 