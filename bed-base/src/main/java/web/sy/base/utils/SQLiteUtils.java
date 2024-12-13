package web.sy.base.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import web.sy.base.pojo.entity.SystemConfig;

import java.io.File;
import java.sql.*;

@Slf4j
public class SQLiteUtils {
    private static final String DB_PATH = "data/config.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static final String CONFIG_KEY = "system.config";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        initDatabase();
    }

    private static void initDatabase() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            // 确保目录存在
            File dbDir = dbFile.getParentFile();
            if (!dbDir.exists() && !dbDir.mkdirs()) {
                throw new RuntimeException("Failed to create database directory");
            }
            
            // 创建数据库文件和表
            try (Connection ignored = getConnection()) {
                log.info("Database file created at: {}", dbFile.getAbsolutePath());
                initSqliteTable();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize database", e);
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initSqliteTable() {
        String createSystemConfigTable = """
                CREATE TABLE IF NOT EXISTS system_config (
                    key TEXT PRIMARY KEY,
                    value TEXT,
                    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSystemConfigTable);
            log.info("System config table initialized");
        } catch (SQLException e) {
            log.error("Failed to create table", e);
            throw new RuntimeException("Failed to create table", e);
        }
    }

    public static String getValue(String key) {
        String sql = "SELECT value FROM system_config WHERE key = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static void setValue(String key, String value) {
        String sql = "INSERT OR REPLACE INTO system_config (key, value) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set value", e);
        }
    }

    public static SystemConfig getSystemConfig() {
        try {
            String json = getValue(CONFIG_KEY);
            if (json == null) {
                return new SystemConfig();
            }
            return objectMapper.readValue(json, SystemConfig.class);
        } catch (Exception e) {
            log.error("Failed to parse system config", e);
            return new SystemConfig();
        }
    }

    public static void saveSystemConfig(SystemConfig config) {
        try {
            String json = objectMapper.writeValueAsString(config);
            setValue(CONFIG_KEY, json);
        } catch (Exception e) {
            log.error("Failed to save system config", e);
            throw new RuntimeException("Failed to save system config", e);
        }
    }
} 