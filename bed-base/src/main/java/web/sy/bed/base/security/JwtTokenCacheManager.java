package web.sy.bed.base.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import web.sy.bed.base.config.GlobalConfig;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Lazy
@DependsOn("globalConfig")
public class JwtTokenCacheManager {

    private final Cache<String, String> tokenWhitelist;
    private static final long DEFAULT_EXPIRATION = 3600L; // 默认1小时

    public JwtTokenCacheManager() {
        long expiration = DEFAULT_EXPIRATION;
        try {
            Long configExpiration = GlobalConfig.getConfig().getJwtExpiration();
            if (configExpiration != null) {
                expiration = configExpiration;
                log.debug("Using configured JWT expiration: {} seconds", expiration);
            }
        } catch (Exception e) {
            log.warn("Failed to get JWT expiration from config, using default: {} seconds", expiration);
        }

        this.tokenWhitelist = Caffeine.newBuilder()
                .expireAfterWrite(expiration, TimeUnit.SECONDS)
                .maximumSize(10_000)
                .recordStats()
                .build();
        
        log.debug("Initialized JWT token cache with expiration: {} seconds", expiration);
    }

    public void addToken(String token, String username) {
        tokenWhitelist.put(token, username);
        log.debug("Token added to whitelist for user: {}", username);
    }

    public boolean isTokenValid(String token) {
        return tokenWhitelist.getIfPresent(token) != null;
    }

    public void removeToken(String token) {
        tokenWhitelist.invalidate(token);
        log.debug("Token removed from whitelist");
    }

    public void clearAllTokens() {
        tokenWhitelist.invalidateAll();
        log.debug("Token whitelist cleared");
    }
} 