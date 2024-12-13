package web.sy.base.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import web.sy.base.exception.RateLimitException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitManager {
    
    private final Cache<String, RateLimitInfo> requestCountCache;

    public RateLimitManager() {
        this.requestCountCache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build();
    }
    @Data
    @AllArgsConstructor
    private static class RateLimitInfo {
        private AtomicInteger count;
        private long startTime;
    }

    public void checkRateLimit(String key, int limit, int time, TimeUnit timeUnit) {
        RateLimitInfo info = requestCountCache.get(key, k -> new RateLimitInfo(new AtomicInteger(0), System.currentTimeMillis()));
        
        long now = System.currentTimeMillis();
        long timeWindowMillis = timeUnit.toMillis(time);
        log.debug("Checking rate limit for key: {}, now: {}, startTime: {}", key, now, info.getStartTime());
        // 检查是否需要重置计数器
        if (now - info.getStartTime() > timeWindowMillis) {
            info.setCount(new AtomicInteger(0));
            info.setStartTime(now);
        }

        int count = info.getCount().incrementAndGet();
        if (count > limit) {
            log.warn("Rate limit exceeded for key: {}, count: {}, limit: {}", key, count, limit);
            throw new RateLimitException(String.format("请求过于频繁，请在%.1f秒后重试", 
                (timeWindowMillis - (now - info.getStartTime())) / 1000.0));
        }
        
        log.debug("Rate limit check passed for key: {}, count: {}, limit: {}", key, count, limit);
    }
} 