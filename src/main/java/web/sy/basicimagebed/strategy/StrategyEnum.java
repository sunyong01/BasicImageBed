package web.sy.basicimagebed.strategy;


import lombok.Getter;

import web.sy.basicimagebed.strategy.impl.BaseStrategy;
import web.sy.basicimagebed.strategy.impl.S3Strategy;
@Getter
public enum StrategyEnum {
    Local(1, "本地", new BaseStrategy()),
    S3(2, "AWS S3", new S3Strategy());
//    Oss(3, "阿里云 OSS"),
//    Cos(4, "腾讯云 COS"),
//    Kodo(5, "七牛云 Kodo"),
//    Uss(6, "又拍云 USS"),
//    Sftp(7, "SFTP"),
//    Ftp(8, "FTP"),
//    Webdav(9, "WebDav"),
//    Minio(10, "Minio");

    private final String description;
    private final int id;
    private final StorageStrategy strategy;

    StrategyEnum(int i, String description, StorageStrategy strategy1) {
        this.id = i;
        this.description = description;
        this.strategy = strategy1;
    }

    public static StorageStrategy getStrategyById(int id) {
        for (StrategyEnum strategyEnum : StrategyEnum.values()) {
            if (strategyEnum.getId() == id) {
                return strategyEnum.getStrategy();
            }
        }
        throw new IllegalArgumentException("Invalid strategy ID: " + id);
    }
}