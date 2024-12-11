package web.sy.storage.strategy.config;

import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.Collections;
import java.util.HashMap;

public class StrategyInitPropertiesHelper {
    public static FileStorageProperties getInitProperties(Integer type, HashMap<String, String> config)
    {
        FileStorageProperties properties = new FileStorageProperties();
        StrategyConfigBuilderEnum byCode = StrategyConfigBuilderEnum.getByCode(type);
        if (byCode == null) {
            throw new RuntimeException("不支持的存储类型");
        }
        FileStorageProperties.BaseConfig config1 = byCode.getConfig(config);

        switch (byCode) {
            case LOCAL:
                FileStorageProperties.LocalPlusConfig localPlusConfig = (FileStorageProperties.LocalPlusConfig) config1;
                properties.setLocalPlus(Collections.singletonList(localPlusConfig));
                break;
            case HUAWEI_OBS:
                FileStorageProperties.HuaweiObsConfig obsConfig = (FileStorageProperties.HuaweiObsConfig) config1;
                properties.setHuaweiObs(Collections.singletonList(obsConfig));
                break;
            case ALIYUN_OSS:
                FileStorageProperties.AliyunOssConfig ossConfig = (FileStorageProperties.AliyunOssConfig) config1;
                properties.setAliyunOss(Collections.singletonList(ossConfig));
                break;
            case KODO:
                FileStorageProperties.QiniuKodoConfig kodoConfig = (FileStorageProperties.QiniuKodoConfig) config1;
                properties.setQiniuKodo(Collections.singletonList(kodoConfig));
                break;
            case TENCENT_COS:
                FileStorageProperties.TencentCosConfig cosConfig = (FileStorageProperties.TencentCosConfig) config1;
                properties.setTencentCos(Collections.singletonList(cosConfig));
                break;
            case BAIDU_BOS:
                FileStorageProperties.BaiduBosConfig bosConfig = (FileStorageProperties.BaiduBosConfig) config1;
                properties.setBaiduBos(Collections.singletonList(bosConfig));
                break;
            case USS:
                FileStorageProperties.UpyunUssConfig ussConfig = (FileStorageProperties.UpyunUssConfig) config1;
                properties.setUpyunUss(Collections.singletonList(ussConfig));
                break;
            case MINIO:
                FileStorageProperties.MinioConfig minioConfig = (FileStorageProperties.MinioConfig) config1;
                properties.setMinio(Collections.singletonList(minioConfig));
                break;
            case S3:
                FileStorageProperties.AmazonS3Config s3Config = (FileStorageProperties.AmazonS3Config) config1;
                properties.setAmazonS3(Collections.singletonList(s3Config));
                break;
            case GOOGLE_CLOUD_STORAGE:
                FileStorageProperties.GoogleCloudStorageConfig gcsConfig = (FileStorageProperties.GoogleCloudStorageConfig) config1;
                properties.setGoogleCloudStorage(Collections.singletonList(gcsConfig));
                break;
            case FASTDFS:
                FileStorageProperties.FastDfsConfig fastdfsConfig = (FileStorageProperties.FastDfsConfig) config1;
                properties.setFastdfs(Collections.singletonList(fastdfsConfig));
                break;
            case AZURE_BLOB_STORAGE:
                FileStorageProperties.AzureBlobStorageConfig azureBlobConfig = (FileStorageProperties.AzureBlobStorageConfig) config1;
                properties.setAzureBlob(Collections.singletonList(azureBlobConfig));
                break;
            case FTP:
                FileStorageProperties.FtpConfig ftpConfig = (FileStorageProperties.FtpConfig) config1;
                properties.setFtp(Collections.singletonList(ftpConfig));
                break;
            case SFTP:
                FileStorageProperties.SftpConfig sftpConfig = (FileStorageProperties.SftpConfig) config1;
                properties.setSftp(Collections.singletonList(sftpConfig));
                break;
            case WEBDAV:
                FileStorageProperties.WebDavConfig webdavConfig = (FileStorageProperties.WebDavConfig) config1;
                properties.setWebdav(Collections.singletonList(webdavConfig));
                break;
            default:
                throw new RuntimeException("尚未实现的存储类型处理逻辑");
        }
        return properties;
    }


}
