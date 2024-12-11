package web.sy.bed.controller.init;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import web.sy.bed.base.annotation.Anonymous;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.base.pojo.dto.InitRequest;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.pojo.entity.SystemConfig;
import web.sy.bed.base.utils.SQLiteUtils;
import web.sy.bed.vo.resp.FrontendConfig;
import web.sy.bed.base.service.InitService;
import web.sy.bed.base.utils.ResponseBuilder;

@Anonymous
@RestController
@RequestMapping("/api/v1/init")
@Tag(name = "初始化接口", description = "系统初始化相关接口")
public class InitController {

    @Resource
    private InitService initService;
    @Operation(summary = "前端配置信息")
    @GetMapping("/front-config")
    public ResponseInfo<FrontendConfig> getFrontConfig() {
        FrontendConfig frontendConfig = new FrontendConfig();
        return ResponseBuilder.success(frontendConfig.fromSystemConfig());
    }
    @GetMapping("/check")
    @Operation(summary = "检查系统是否已初始化")
    public ResponseInfo<Boolean> checkInit() {
        boolean isInitialized = GlobalConfig.getInitialized();
        return ResponseBuilder.success(isInitialized);
    }

    @PostMapping
    @Operation(summary = "初始化系统")
    public ResponseInfo<String> initSystem(@RequestBody InitRequest request) {
        boolean isInitialized = GlobalConfig.getInitialized();
        if (isInitialized) {
            return ResponseBuilder.error("系统已初始化,请不要重复操作");
        }
        initService.initSystem(request);
        return ResponseBuilder.success("初始化成功");
    }
} 