package web.sy.bed.controller.stats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.sy.base.annotation.RateLimit;
import web.sy.base.annotation.RequireAuthentication;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.service.UserService;
import web.sy.base.utils.ResponseBuilder;
import web.sy.bed.controller.BaseController;
import web.sy.bed.service.ImageAccessLogService;
import web.sy.bed.service.WebUserService;
import web.sy.bed.vo.stats.AdminStatsVO;
import web.sy.bed.vo.stats.StatsVO;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Tag(name = "统计数据接口", description = "获取系统统计数据")
@RestController
@RequestMapping("/api/v1/stats")
@RateLimit(count = 3, time = 10, timeUnit = TimeUnit.SECONDS, prefix = "stats",limitByUser = true)
public class StatsController extends BaseController {

    private final ImageAccessLogService imageAccessLogService;

    public StatsController(UserService userService, WebUserService webUserService,
                          ImageAccessLogService imageAccessLogService) {
        super(userService,webUserService);
        this.imageAccessLogService = imageAccessLogService;
    }

    @GetMapping
    @RequireAuthentication
    @Operation(summary = "获取统计数据", description = "获取系统访问统计数据")
    public ResponseInfo<StatsVO> getStats(
            @RequestParam(required = false,defaultValue = "7") @Min(1) @Max(30) Integer days
    ) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        Long userId = getCurrentUserId();
        StatsVO stats = imageAccessLogService.getStats(userId, startTime, endTime);
        return ResponseBuilder.success(stats);
    }

    @GetMapping("/admin")
    @RequireAuthentication(requireAdmin = true)
    @Operation(summary = "获取管理员统计数据", description = "获取系统管理员专用统计数据")
    public ResponseInfo<AdminStatsVO> getAdminStats() {
        return ResponseBuilder.success(imageAccessLogService.getAdminStats());
    }
} 