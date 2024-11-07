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
    date        DATETIME     COMMENT '上传日期(yyyy-MM-dd HH:mm:ss)',
    strategy    INT          COMMENT '存储策略枚举',
    is_public   BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否公开',
    upload_user VARCHAR(255) COMMENT '上传用户',
    album_id    INT          COMMENT '相册 ID'
) COMMENT ='图片信息表';

CREATE TABLE IF NOT EXISTS tb_strategy
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(255) NOT NULL COMMENT '策略名称',
    config_json json         NOT NULL COMMENT '策略配置 JSON'
) COMMENT ='存储策略表';

CREATE TABLE IF NOT EXISTS tb_user
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    email    VARCHAR(255) NOT NULL COMMENT '用户邮箱',
    password VARCHAR(255) NOT NULL COMMENT '用户密码',
    nickname VARCHAR(255) COMMENT '用户昵称',
    url      VARCHAR(255) COMMENT '个人主页URL',
    avatar   VARCHAR(255) COMMENT '头像URL',
    register_ip VARCHAR(255) COMMENT '注册IP',
    capacity DOUBLE COMMENT '用户容量，单位 KB',
    capacity_used DOUBLE COMMENT '用户已用容量，单位 KB',
    role     INT          NOT NULL default 1 COMMENT '用户角色'
) COMMENT ='用户表';
CREATE TABLE IF NOT EXISTS tb_user_token
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    email       VARCHAR(255) NOT NULL COMMENT '用户ID',
    token       VARCHAR(255) NOT NULL COMMENT 'token'
) COMMENT ='用户 token 表';

CREATE TABLE IF NOT EXISTS tb_albums
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '相册自增 ID',
    name        VARCHAR(255) NOT NULL COMMENT '相册名称',
    intro       TEXT COMMENT '相册简介',
    create_user VARCHAR(255) NOT NULL COMMENT '创建用户'
) COMMENT ='相册表';

