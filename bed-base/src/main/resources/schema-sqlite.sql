CREATE TABLE IF NOT EXISTS system_config (
    key TEXT PRIMARY KEY,
    value TEXT,
    description TEXT,
    encrypted BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 预置系统配置
INSERT OR IGNORE INTO system_config (key, value, description) VALUES
('system.initialized', 'false', '系统是否已初始化'),
('db.url', '', '数据库连接URL'),
('db.username', '', '数据库用户名'),
('db.password', '', '数据库密码'),
('storage.path', '', '本地存储路径'),
('server.url', '', '服务访问地址'),
('upload.max_size', '5120', '单文件大小限制(KB)'),
('upload.allowed_types', 'jpg,jpeg,png,gif,bmp,webp', '允许的文件类型'),
('auth.allow_guest', 'false', '是否允许游客访问'),
('auth.allow_register', 'true', '是否允许注册'),
('jwt.secret', '', 'JWT密钥'),
('jwt.expiration', '86400', 'JWT过期时间(秒)'),
('system.debug', 'false', '调试模式'),
('admin.username', 'admin', '管理员用户名'),
('admin.email', 'admin@local.host', '管理员邮箱'); 