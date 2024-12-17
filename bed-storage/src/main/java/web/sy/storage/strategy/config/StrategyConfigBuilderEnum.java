package web.sy.storage.strategy.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;
import org.dromara.x.file.storage.core.platform.FileStorage;
import web.sy.storage.strategy.config.ConfigFactories.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public enum StrategyConfigBuilderEnum {


    @Schema(description = "本地存储")
    LOCAL(1, LocalFileStorageFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildLocalPlusFileStorage(Collections.singletonList((FileStorageProperties.LocalPlusConfig) configs));
        }
    },

    @Schema(description = "FTP")
    FTP(2, FtpConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildFtpFileStorage(Collections.singletonList((FileStorageProperties.FtpConfig) configs), null);
        }
    },

    @Schema(description = "SFTP")
    SFTP(3, SftpConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildSftpFileStorage(Collections.singletonList((FileStorageProperties.SftpConfig) configs), null);
        }
    },

    @Schema(description = "WebDav")
    WEBDAV(4, WebDavConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildWebDavFileStorage(Collections.singletonList((FileStorageProperties.WebDavConfig) configs), null);
        }
    },

    @Schema(description = "Amazon S3")
    S3(5, AmazonS3ConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildAmazonS3FileStorage(Collections.singletonList((FileStorageProperties.AmazonS3Config) configs), null);
        }
    },

    @Schema(description = "Minio")
    MINIO(6, MinioConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildMinioFileStorage(Collections.singletonList((FileStorageProperties.MinioConfig) configs), null);
        }
    },

    @Schema(description = "阿里云OSS")
    ALIYUN_OSS(7, AliyunOssConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildAliyunOssFileStorage(Collections.singletonList((FileStorageProperties.AliyunOssConfig) configs), null);
        }
    },

    @Schema(description = "华为云OBS")
    HUAWEI_OBS(8, HuaweiObsConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildHuaweiObsFileStorage(Collections.singletonList((FileStorageProperties.HuaweiObsConfig) configs), null);
        }
    },

    @Schema(description = "腾讯云COS")
    TENCENT_COS(9, TencentCosConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildTencentCosFileStorage(Collections.singletonList((FileStorageProperties.TencentCosConfig) configs), null);
        }
    },

    @Schema(description = "百度云BOS")
    BAIDU_BOS(10, BaiduBosConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildBaiduBosFileStorage(Collections.singletonList((FileStorageProperties.BaiduBosConfig) configs), null);
        }
    },

    @Schema(description = "又拍云 USS")
    USS(11, UpyunUssConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildUpyunUssFileStorage(Collections.singletonList((FileStorageProperties.UpyunUssConfig) configs), null);
        }
    },

    @Schema(description = "七牛云 Kodo")
    KODO(12, QiniuKodoConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildQiniuKodoFileStorage(Collections.singletonList((FileStorageProperties.QiniuKodoConfig) configs), null);
        }
    },

    @Schema(description = "Google Cloud Storage")
    GOOGLE_CLOUD_STORAGE(13, GoogleCloudStorageConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildGoogleCloudStorageFileStorage(Collections.singletonList((FileStorageProperties.GoogleCloudStorageConfig) configs), null);
        }
    },

    @Schema(description = "FastDFS")
    FASTDFS(14, FastDfsConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            // 这里被封装为私有方法，那没有办法 只能反射了
            try {
                Method method = FileStorageServiceBuilder.class.getDeclaredMethod("buildFastDfsFileStorage", List.class, String.class);
                method.setAccessible(true);
                return (List<? extends FileStorage>) method.invoke(null, Collections.singletonList((FileStorageProperties.FastDfsConfig) configs), null);
            } catch (Exception e) {
                log.error("Get fastdfs file storage failed", e);
            }

            return List.of();
        }
    },

    @Schema(description = "Azure Blob Storage")
    AZURE_BLOB_STORAGE(15, AzureBlobStorageConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildAzureBlobFileStorage(Collections.singletonList((FileStorageProperties.AzureBlobStorageConfig) configs), null);
        }
    },

    @Schema(description = "Amazon S3 Compatible")
    OTHER_S3_COMPATIBLE(16, AmazonS3ConfigFactory::new) {
        @Override
        public List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs) {
            return FileStorageServiceBuilder.buildAmazonS3FileStorage(Collections.singletonList((FileStorageProperties.AmazonS3Config) configs), null);
        }
    };

    @Getter
    private final int code;
    private final FileStorageConfigFactory<? extends FileStorageProperties.BaseConfig> factory;

    <T extends FileStorageProperties.BaseConfig> StrategyConfigBuilderEnum(int code, Supplier<FileStorageConfigFactory<T>> factorySupplier) {
        this.code = code;
        this.factory = factorySupplier.get();
    }

    public static StrategyConfigBuilderEnum getByCode(int code) {
        for (StrategyConfigBuilderEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends FileStorageProperties.BaseConfig> T getConfig(HashMap<String, String> config) {
        return (T) factory.getConfig(config);
    }

    public abstract List<? extends FileStorage> buildSpecificFileStorage(List<? extends FileStorageProperties.BaseConfig> configs);

    public static List<? extends FileStorage> getFileStorage(Integer type, HashMap<String, String> config) {
        StrategyConfigBuilderEnum builderEnum = StrategyConfigBuilderEnum.getByCode(type);
        if (builderEnum == null) {
            throw new IllegalArgumentException("不支持的存储类型");
        }
         return builderEnum.buildSpecificFileStorage(builderEnum.getConfig(config));
    }

}
