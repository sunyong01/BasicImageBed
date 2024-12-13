package web.sy.base.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import web.sy.base.pojo.dto.InitRequest;
import web.sy.base.pojo.entity.SystemConfig;
import web.sy.base.service.InitService;
import web.sy.base.service.SystemConfigService;
import web.sy.base.utils.SQLiteUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class InitServiceImpl implements InitService {

    private final SystemConfigService systemConfigService;
    private final PasswordEncoder passwordEncoder;
    private final String dataDir = System.getProperty("user.dir").replaceAll("/bed-web", "")+"/data/";

    public InitServiceImpl(SystemConfigService systemConfigService,
                           PasswordEncoder passwordEncoder
    ) {
        this.systemConfigService = systemConfigService;
        this.passwordEncoder = passwordEncoder;

    }

    private String generateRandomJwtSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void initSystem(InitRequest request) {
        // 第一步：测试MySQL连接并执行初始化脚本
        initializeMySql(request);
        // 第二步：保存配置到SQLite
        saveConfigurations(request);
        // 第三步：重启应用程序
        restartApplication();
    }

    private void initializeMySql(InitRequest request) {
        // 创建临时数据源测试连接
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(request.getDbUrl());
        config.setUsername(request.getDbUsername());
        config.setPassword(request.getDbPassword());

        try (HikariDataSource dataSource = new HikariDataSource(config);
             Connection conn = dataSource.getConnection()) {

            // 读取init.sql文件内容
            ClassPathResource resource = new ClassPathResource("init.sql");
            StringBuilder sql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sql.append(line).append("\n");
                }
            }

            // 执行初始化SQL
            try (Statement stmt = conn.createStatement()) {
                for (String sqlStatement : sql.toString().split(";")) {
                    if (!sqlStatement.trim().isEmpty()) {
                        stmt.execute(sqlStatement.trim());
                    }
                }

                // 创建管理员用户
                String adminUsername = StringUtils.hasText(request.getAdminUsername())
                        ? request.getAdminUsername()
                        : "admin";
                String adminPassword = StringUtils.hasText(request.getAdminPassword())
                        ? request.getAdminPassword()
                        : "admin";
                String adminEmail = StringUtils.hasText(request.getAdminEmail())
                        ? request.getAdminEmail()
                        : "admin@local.host";

                // 插入管理员用户
                String insertUserSql = """
                        INSERT INTO tb_user (username, password, enabled,
                            account_non_expired, credentials_non_expired, account_non_locked)
                        VALUES (?, ?, true, true, true, true)
                        """;
                try (var pstmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, adminUsername);
                    pstmt.setString(2, passwordEncoder.encode(adminPassword));
                    pstmt.executeUpdate();

                    // 获取生成的用户ID
                    try (var rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            long userId = rs.getLong(1);

                            // 插入用户邮箱和昵称到tb_user_profile
                            String insertUserProfileSql = """
                                    INSERT INTO tb_user_profile (user_id, email, nickname, capacity, capacity_used)
                                    VALUES (?, ?, '管理员', ?, 0)
                                    """;
                            try (var profilePstmt = conn.prepareStatement(insertUserProfileSql)) {
                                profilePstmt.setLong(1, userId);
                                profilePstmt.setString(2, adminEmail);
                                profilePstmt.setDouble(3, 1024.0 * 1024.0); // 1GB = 1024MB
                                profilePstmt.executeUpdate();
                            }
                        }
                    }
                }

                // 为管理员分配超级管理员角色
                String insertRoleSql = """
                        INSERT INTO tb_user_role (user_id, role_id)
                        SELECT u.id, r.id
                        FROM tb_user u, tb_role r
                        WHERE u.username = ? AND r.role_code = 'ROLE_SUPER_ADMIN'
                        """;
                try (var pstmt = conn.prepareStatement(insertRoleSql)) {
                    pstmt.setString(1, adminUsername);
                    pstmt.executeUpdate();
                }
            }

            // 插入默认的本地存储策略
            String insertStrategySql = """
                    INSERT INTO tb_strategy (
                        strategy_type, 
                        config_json, 
                        max_capacity, 
                        used_capacity, 
                        strategy_name, 
                        available, 
                        sort_order
                    ) VALUES (
                        'LOCAL',
                        ?,
                        1024.0 * 1024.0,  -- 1TB
                        0.0,
                        '本地存储(默认)',
                        true,
                        100
                    )
                    """;
            try (var pstmt = conn.prepareStatement(insertStrategySql)) {

                // 构建配置JSON
                Map<String, String> configMap = new HashMap<>();
                String INIT_BASE_PATH = "/uploads/";
                configMap.put("local_path", INIT_BASE_PATH);
                configMap.put("base-path", "upload/");
                configMap.put("platform-name", "本地存储(默认)");
                configMap.put("storage-path", dataDir);
                ObjectMapper objectMapper = new ObjectMapper();
                String configJson = objectMapper.writeValueAsString(configMap);
                pstmt.setString(1, configJson);
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            throw new RuntimeException("数据库连接失败或初始化失败: " + e.getMessage(), e);
        }
    }

    private void saveConfigurations(InitRequest request) {
        SystemConfig config = new SystemConfig();

        // 设置服务器配置
        config.setServerUrl(request.getServerUrl());
        config.setSiteName("我的图床"); // 使用默认值

        // 设置上传限制
        config.setMaxSize(request.getAllowSizeKb());
        config.setAllowedTypes(request.getAllowSuffix());

        // 设置认证设置
        config.setAllowGuest(request.getAuthAllowGuest());
        config.setAllowRegister(request.getAuthAllowRegister());

        // 设置JWT配置
        String jwtSecret = StringUtils.hasText(request.getJwtSecret())
                ? request.getJwtSecret()
                : generateRandomJwtSecret();
        config.setJwtSecret(jwtSecret);
        config.setJwtExpiration(Long.valueOf(request.getJwtExpiration()));

        // 设置调试模式
        config.setDebug(request.getDebug());

        // 设置Referer控制(使用默认值)
        config.setAllowEmptyReferer(true);
        config.setAllowedReferers(new String[0]);
        config.setEnableRefererControl(false);

        // 保存系统配置
        systemConfigService.updateSystemConfig(config);

        SQLiteUtils.setValue("debug", String.valueOf(request.getDebug()));
        // 保存数据库配置(这些仍然使用独立的键值对存储)
        systemConfigService.setConfigValue("db.url", request.getDbUrl());
        systemConfigService.setConfigValue("db.username", request.getDbUsername());
        systemConfigService.setConfigValue("db.password", request.getDbPassword());

        // 标记系统为已初始化
        systemConfigService.setInitialized(true);
    }

    private void restartApplication() {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000); // 延迟1秒重启
                System.exit(0); // 终止JVM
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.setDaemon(false);
        thread.start();
    }
}