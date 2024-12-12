package web.sy.base.pojo.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "存储策略枚举")
public enum Strategy {
    @Schema(description = "本地存储")
    LOCAL(1),

    @Schema(description = "FTP")
    FTP(2),

    @Schema(description = "SFTP")
    SFTP(3),

    @Schema(description = "WebDav")
    WEBDAV(4),

    @Schema(description = "Amazon S3")
    S3(5),

    @Schema(description = "Minio")
    MINIO(6),

    @Schema(description = "阿里云OSS")
    ALIYUN_OSS(7),

    @Schema(description = "华为云OBS")
    HUAWEI_OBS(8),

    @Schema(description = "腾讯云COS")
    TENCENT_COS(9),

    @Schema(description = "百度云BOS")
    BAIDU_BOS(10),

    @Schema(description = "又拍云 USS")
    USS(11),

    @Schema(description = "七牛云 Kodo")
    KODO(12),

    @Schema(description = "Google Cloud Storage")
    GOOGLE_CLOUD_STORAGE(13),

    @Schema(description = "FastDFS")
    FASTDFS(14),

    @Schema(description = "Azure Blob Storage")
    AZURE_BLOB_STORAGE(15),

    @Schema(description = "其他兼容Amazon S3的云存储")
    OTHER_S3_COMPATIBLE(16);
    private final int value;

    Strategy(int value) {
        this.value = value;
    }

    public static Strategy fromValue(int value) {
        for (Strategy strategy : Strategy.values()) {
            if (strategy.value == value) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unknown storage strategy value: " + value);
    }
} 