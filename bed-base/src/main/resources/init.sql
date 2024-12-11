CREATE TABLE IF NOT EXISTS tb_image_info
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `key`       VARCHAR(255) NOT NULL UNIQUE COMMENT '图片唯一密钥',
    name        VARCHAR(255) NOT NULL COMMENT '图片名称',
    origin_name VARCHAR(255) NOT NULL COMMENT '图片原始名称',
    pathname    VARCHAR(255) NOT NULL COMMENT '图片路径名',
    size        FLOAT        NOT NULL COMMENT '图片大小，单位 KB',
    width       INT          NOT NULL COMMENT '图片宽度',
    height      INT          NOT NULL COMMENT '图片高度',
    md5         VARCHAR(255) NOT NULL COMMENT '图片 md5 值',
    sha1        VARCHAR(255) NOT NULL COMMENT '图片 sha1 值',
    date        DATETIME COMMENT '上传日期(yyyy-MM-dd HH:mm:ss)',
    strategy    INT COMMENT '存储策略枚举',
    is_public   BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否公开',
    upload_user VARCHAR(255) COMMENT '上传用户',
    album_id    INT COMMENT '相册 ID'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='图片信息表';

CREATE TABLE IF NOT EXISTS tb_user
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username                VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password                VARCHAR(255) NOT NULL COMMENT '用户密码',
    enabled                 BOOLEAN               DEFAULT TRUE COMMENT '账号是否可用',
    account_non_expired     BOOLEAN               DEFAULT TRUE COMMENT '账号是否未过期',
    credentials_non_expired BOOLEAN               DEFAULT TRUE COMMENT '密码是否未过期',
    account_non_locked      BOOLEAN               DEFAULT TRUE COMMENT '账号是否未锁定',
    create_time             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login_time         DATETIME COMMENT '最后登录时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户认证表';

CREATE TABLE IF NOT EXISTS tb_user_profile
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id       BIGINT       NOT NULL UNIQUE COMMENT '关联的用户ID',
    email         VARCHAR(255) NOT NULL UNIQUE COMMENT '用户邮箱',
    nickname      VARCHAR(255) COMMENT '用户昵称',
    url           VARCHAR(255) COMMENT '个人主页URL',
    avatar        VARCHAR(255) COMMENT '头像URL',
    register_ip   VARCHAR(255) COMMENT '注册IP',
    capacity      DOUBLE COMMENT '用户容量，单位 KB',
    capacity_used DOUBLE COMMENT '用户已用容量，单位 KB',
    FOREIGN KEY (user_id) REFERENCES tb_user (id) ON DELETE CASCADE,
    UNIQUE KEY `uk_email` (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户信息表';

CREATE TABLE IF NOT EXISTS tb_user_token
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token VARCHAR(255) NOT NULL COMMENT 'token',
    token_name VARCHAR(255) NOT NULL COMMENT 'token名称',
    token_id INT NOT NULL COMMENT '该用户的第几个Token',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户 token 表';

CREATE TABLE IF NOT EXISTS tb_album
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '相册ID',
    name        VARCHAR(255) NOT NULL COMMENT '相册名称',
    description TEXT COMMENT '相册描述',
    cover_url   VARCHAR(255) COMMENT '相册封面图片URL',
    is_public   BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    create_time DATETIME     NOT NULL COMMENT '创建时间',
    update_time DATETIME     NOT NULL COMMENT '更新时间',
    username    VARCHAR(255) NOT NULL COMMENT '创建者用户名',
    INDEX idx_username (username) COMMENT '用户名索引',
    INDEX idx_create_time (create_time) COMMENT '创建时间索引'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '相册表';

-- 创建存储策略表
CREATE TABLE IF NOT EXISTS tb_strategy
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '策略ID',
    strategy_type VARCHAR(50)  NOT NULL COMMENT '策略类型',
    config_json   TEXT         NOT NULL COMMENT '配置JSON',
    max_capacity  DOUBLE       NOT NULL DEFAULT 0 COMMENT '最大容量(KB)',
    used_capacity DOUBLE       NOT NULL DEFAULT 0 COMMENT '已用容量(KB)',
    strategy_name VARCHAR(100) NOT NULL COMMENT '策略名称',
    available     BOOLEAN      NOT NULL DEFAULT TRUE COMMENT '是否可用',
    sort_order    INT          NOT NULL DEFAULT 0 COMMENT '优先级(数值越小优先级越高)',
    UNIQUE KEY uk_strategy_type (strategy_type)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='存储策略配置表';


-- 创建图片访问日志表
CREATE TABLE IF NOT EXISTS tb_image_access_log
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    pathname       VARCHAR(255) NOT NULL COMMENT '图片路径',
    user_id        BIGINT       NOT NULL COMMENT '关联用户ID',
    file_size      DOUBLE       NOT NULL COMMENT '文件大小(KB)',
    operation_type VARCHAR(20)  NOT NULL COMMENT '操作类型(UPLOAD/DOWNLOAD)',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='图片访问日志表';



CREATE TABLE IF NOT EXISTS tb_role
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name    VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code    VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    description  VARCHAR(200) COMMENT '角色描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='角色表';

CREATE TABLE IF NOT EXISTS tb_permission
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(50)  NOT NULL UNIQUE COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    description     VARCHAR(200) COMMENT '权限描述',
    resource_type   VARCHAR(50) COMMENT '资源类型：API/MENU/BUTTON',
    resource_path   VARCHAR(200) COMMENT '资源路径',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='权限表';

CREATE TABLE IF NOT EXISTS tb_user_role
(
    user_id      BIGINT COMMENT '用户ID',
    role_id      BIGINT COMMENT '角色ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_user (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_role (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户角色关联表';

CREATE TABLE IF NOT EXISTS tb_role_permission
(
    role_id       BIGINT COMMENT '角色ID',
    permission_id BIGINT COMMENT '权限ID',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES tb_role (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES tb_permission (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='角色权限关联表';



INSERT INTO tb_role (role_name, role_code, description)
VALUES ('超级管理员', 'ROLE_SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
       ('管理员', 'ROLE_ADMIN', '系统管理员，拥有大部分权限'),
       ('普通用户', 'ROLE_USER', '普通用户，拥有基本权限');

INSERT INTO tb_permission (permission_name, permission_code, resource_type, resource_path)
VALUES
-- 用户管理权限
('查看用户', 'USER:VIEW', 'API', '/api/v1/users/**'),
('创建用户', 'USER:CREATE', 'API', '/api/v1/users'),
('修改用户', 'USER:UPDATE', 'API', '/api/v1/users/**'),
('删除用户', 'USER:DELETE', 'API', '/api/v1/users/**'),

-- 图片管理权限
('查看图片', 'IMAGE:VIEW', 'API', '/api/v1/images/**'),
('上传图片', 'IMAGE:UPLOAD', 'API', '/api/v1/images'),
('删除图片', 'IMAGE:DELETE', 'API', '/api/v1/images/**'),

-- 相册管理权限
('查看相册', 'ALBUM:VIEW', 'API', '/api/v1/albums/**'),
('创建相册', 'ALBUM:CREATE', 'API', '/api/v1/albums'),
('修改相册', 'ALBUM:UPDATE', 'API', '/api/v1/albums/**'),
('删除相册', 'ALBUM:DELETE', 'API', '/api/v1/albums/**'),

-- 存储策略权限
('查看策略', 'STRATEGY:VIEW', 'API', '/api/v1/strategies/**'),
('创建策略', 'STRATEGY:CREATE', 'API', '/api/v1/strategies'),
('修改策略', 'STRATEGY:UPDATE', 'API', '/api/v1/strategies/**'),
('删除策略', 'STRATEGY:DELETE', 'API', '/api/v1/strategies/**'),

-- 系统管理权限
('查看系统信息', 'SYSTEM:VIEW', 'API', '/api/v1/system/**'),
('修改系统信息', 'SYSTEM:UPDATE', 'API', '/api/v1/system/**');

INSERT INTO tb_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM tb_role r,
     tb_permission p
WHERE r.role_code = 'ROLE_SUPER_ADMIN';

INSERT INTO tb_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM tb_role r,
     tb_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.permission_code NOT LIKE 'STRATEGY%';

INSERT INTO tb_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM tb_role r,
     tb_permission p
WHERE r.role_code = 'ROLE_USER'
  AND p.permission_code IN (
                            'IMAGE:VIEW', 'IMAGE:UPLOAD', 'IMAGE:DELETE',
                            'ALBUM:VIEW', 'ALBUM:CREATE', 'ALBUM:UPDATE', 'ALBUM:DELETE'
    );


