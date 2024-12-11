package web.sy.bed.controller.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import web.sy.bed.base.annotation.RequireAuthentication;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.utils.ResponseBuilder;
import web.sy.bed.base.service.SystemConfigService;
import web.sy.bed.base.pojo.dto.SystemConfigVO;
import web.sy.bed.base.event.ConfigChangeEvent;



@Tag(name = "系统配置接口", description = "系统配置管理相关接口")
@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    private final SystemConfigService systemConfigService;
    private final ConfigurableApplicationContext applicationContext;
    private final ApplicationEventPublisher eventPublisher;

    public SystemController(SystemConfigService systemConfigService, 
                          ConfigurableApplicationContext applicationContext,
                          ApplicationEventPublisher eventPublisher) {
        this.systemConfigService = systemConfigService;
        this.applicationContext = applicationContext;
        this.eventPublisher = eventPublisher;
    }

    @Operation(summary = "获取系统配置", description = "获取当前系统的配置信息")
    @GetMapping("/config")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<SystemConfigVO> getSystemConfig() {
        return ResponseBuilder.success(systemConfigService.getSystemConfigVO());
    }

    @Operation(summary = "更新系统配置", description = "更新当前系统的配置信息")
    @PutMapping("/config")
    @RequireAuthentication(requireAdmin = true)
    public ResponseInfo<String> updateSystemConfig(@RequestBody SystemConfigVO config) {
        systemConfigService.updateSystemConfig(config);
        eventPublisher.publishEvent(new ConfigChangeEvent(this));
        return ResponseBuilder.success("系统配置更新成功");
    }

    @Operation(summary = "重启系统", description = "重启整个应用系统（需要管理员权限）")
    @PostMapping("/restart")
    @RequireAuthentication(requireSuperAdmin  = true)
    public ResponseInfo<String> restartSystem() {
        // 创建一个新线程来执行重启，以便能够正常返回响应
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 等待1秒以确保响应能够返回
                applicationContext.close(); // 关闭当前上下文
                System.exit(0); // 退出应用，依赖外部进程管理器（如systemd）来重启应用
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "RestartThread").start();
        
        return ResponseBuilder.success("系统正在重启，请稍后重新连接...");
    }
}