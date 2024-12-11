package web.sy.storage.strategy.config;

import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.HashMap;

public class ConfigFactories {


    public static class FtpConfigFactory implements FileStorageConfigFactory<FileStorageProperties.FtpConfig> {
        @Override
        public FileStorageProperties.FtpConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.FtpConfig ftpConfig = new FileStorageProperties.FtpConfig();
            ftpConfig.setPlatform(config.get("platform-name"));
            ftpConfig.setHost(config.get("host"));
            ftpConfig.setPort(Integer.parseInt(config.get("port")));
            ftpConfig.setUser(config.get("username"));
            ftpConfig.setPassword(config.get("password"));
            ftpConfig.setBasePath(config.get("base-path"));
            return ftpConfig;
        }
    }

    public static class SftpConfigFactory implements FileStorageConfigFactory<FileStorageProperties.SftpConfig> {
        @Override
        public FileStorageProperties.SftpConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.SftpConfig sftpConfig = new FileStorageProperties.SftpConfig();
            sftpConfig.setPlatform(config.get("platform-name"));
            sftpConfig.setHost(config.get("host"));
            sftpConfig.setPort(Integer.parseInt(config.get("port")));
            sftpConfig.setUser(config.get("username"));
            sftpConfig.setPassword(config.get("password"));
            sftpConfig.setPrivateKeyPath(config.get("private-key-path"));
            sftpConfig.setBasePath(config.get("base-path"));
            return sftpConfig;
        }
    }

    public static class WebDavConfigFactory implements FileStorageConfigFactory<FileStorageProperties.WebDavConfig> {
        @Override
        public FileStorageProperties.WebDavConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.WebDavConfig webDavConfig = new FileStorageProperties.WebDavConfig();
            webDavConfig.setPlatform(config.get("platform-name"));
            webDavConfig.setServer(config.get("server"));
            webDavConfig.setUser(config.get("username"));
            webDavConfig.setPassword(config.get("password"));
            webDavConfig.setBasePath(config.get("base-path"));
            return webDavConfig;
        }
    }

    public static class TencentCosConfigFactory implements FileStorageConfigFactory<FileStorageProperties.TencentCosConfig> {
        @Override
        public FileStorageProperties.TencentCosConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.TencentCosConfig cosConfig = new FileStorageProperties.TencentCosConfig();
            cosConfig.setPlatform(config.get("platform-name"));
            cosConfig.setSecretId(config.get("secret-id"));
            cosConfig.setSecretKey(config.get("secret-key"));
            cosConfig.setRegion(config.get("region"));
            cosConfig.setBucketName(config.get("bucket-name"));
            cosConfig.setBasePath(config.get("base-path"));
            return cosConfig;
        }
    }

    public static class AliyunOssConfigFactory implements FileStorageConfigFactory<FileStorageProperties.AliyunOssConfig> {
        @Override
        public FileStorageProperties.AliyunOssConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.AliyunOssConfig ossConfig = new FileStorageProperties.AliyunOssConfig();
            ossConfig.setPlatform(config.get("platform-name"));
            ossConfig.setAccessKey(config.get("access-key-id"));
            ossConfig.setSecretKey(config.get("access-key-secret"));
            ossConfig.setEndPoint(config.get("endpoint"));
            ossConfig.setBucketName(config.get("bucket-name"));
            ossConfig.setBasePath(config.get("base-path"));
            return ossConfig;
        }
    }

    public static class AmazonS3ConfigFactory implements FileStorageConfigFactory<FileStorageProperties.AmazonS3Config> {
        @Override
        public FileStorageProperties.AmazonS3Config getConfig(HashMap<String, String> config) {
            FileStorageProperties.AmazonS3Config s3Config = new FileStorageProperties.AmazonS3Config();
            s3Config.setPlatform(config.get("platform-name"));
            s3Config.setAccessKey(config.get("access-key-id"));
            s3Config.setSecretKey(config.get("access-key-secret"));
            s3Config.setRegion(config.get("region"));
            s3Config.setBucketName(config.get("bucket-name"));
            s3Config.setBasePath(config.get("base-path"));
            return s3Config;
        }

    }

    public static class MinioConfigFactory implements FileStorageConfigFactory<FileStorageProperties.MinioConfig> {
        @Override
        public FileStorageProperties.MinioConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.MinioConfig minioConfig = new FileStorageProperties.MinioConfig();
            minioConfig.setPlatform(config.get("platform-name"));
            minioConfig.setAccessKey(config.get("access-key-id"));
            minioConfig.setSecretKey(config.get("access-key-secret"));
            minioConfig.setEndPoint(config.get("endpoint"));
            minioConfig.setBucketName(config.get("bucket-name"));
            minioConfig.setBasePath(config.get("base-path"));
            return minioConfig;
        }
    }

    public static class HuaweiObsConfigFactory implements FileStorageConfigFactory<FileStorageProperties.HuaweiObsConfig> {
        @Override
        public FileStorageProperties.HuaweiObsConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.HuaweiObsConfig obsConfig = new FileStorageProperties.HuaweiObsConfig();
            obsConfig.setPlatform(config.get("platform-name"));
            obsConfig.setAccessKey(config.get("access-key-id"));
            obsConfig.setSecretKey(config.get("access-key-secret"));
            obsConfig.setEndPoint(config.get("endpoint"));
            obsConfig.setBucketName(config.get("bucket-name"));
            obsConfig.setBasePath(config.get("base-path"));
            return obsConfig;
        }
    }

    public static class BaiduBosConfigFactory implements FileStorageConfigFactory<FileStorageProperties.BaiduBosConfig> {
        @Override
        public FileStorageProperties.BaiduBosConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.BaiduBosConfig bosConfig = new FileStorageProperties.BaiduBosConfig();
            bosConfig.setPlatform(config.get("platform-name"));
            bosConfig.setAccessKey(config.get("access-key-id"));
            bosConfig.setSecretKey(config.get("access-key-secret"));
            bosConfig.setEndPoint(config.get("endpoint"));
            bosConfig.setBucketName(config.get("bucket-name"));
            bosConfig.setBasePath(config.get("base-path"));
            return bosConfig;
        }
    }

    public static class UpyunUssConfigFactory implements FileStorageConfigFactory<FileStorageProperties.UpyunUssConfig> {
        @Override
        public FileStorageProperties.UpyunUssConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.UpyunUssConfig ussConfig = new FileStorageProperties.UpyunUssConfig();
            ussConfig.setPlatform(config.get("platform-name"));
            ussConfig.setUsername(config.get("username"));
            ussConfig.setPassword(config.get("password"));
            ussConfig.setBucketName(config.get("bucket-name"));
            ussConfig.setBasePath(config.get("base-path"));
            return ussConfig;
        }
    }

    public static class FastDfsConfigFactory implements FileStorageConfigFactory<FileStorageProperties.FastDfsConfig> {
        @Override
        public FileStorageProperties.FastDfsConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.FastDfsConfig fastDfsConfig = new FileStorageProperties.FastDfsConfig();
            FileStorageProperties.FastDfsConfig.FastDfsTrackerServer trackerServer = new FileStorageProperties.FastDfsConfig.FastDfsTrackerServer();
            trackerServer.setServerAddr(config.get("tracker-server-host"));
            trackerServer.setHttpPort(Integer.parseInt(config.get("tracker-server-http-port")));

            fastDfsConfig.setPlatform(config.get("platform-name"));
            fastDfsConfig.setTrackerServer(trackerServer);
            fastDfsConfig.setBasePath(config.get("base-path"));
            return fastDfsConfig;
        }
    }

    public static class QiniuKodoConfigFactory implements FileStorageConfigFactory<FileStorageProperties.QiniuKodoConfig> {
        @Override
        public FileStorageProperties.QiniuKodoConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.QiniuKodoConfig kodoConfig = new FileStorageProperties.QiniuKodoConfig();
            kodoConfig.setPlatform(config.get("platform-name"));
            kodoConfig.setAccessKey(config.get("access-key-id"));
            kodoConfig.setSecretKey(config.get("access-key-secret"));
            kodoConfig.setBucketName(config.get("bucket-name"));
            kodoConfig.setBasePath(config.get("base-path"));
            return kodoConfig;
        }
    }

    public static class GoogleCloudStorageConfigFactory implements FileStorageConfigFactory<FileStorageProperties.GoogleCloudStorageConfig> {
        @Override
        public FileStorageProperties.GoogleCloudStorageConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.GoogleCloudStorageConfig gcsConfig = new FileStorageProperties.GoogleCloudStorageConfig();
            gcsConfig.setPlatform(config.get("platform-name"));
            gcsConfig.setProjectId(config.get("project-id"));
            gcsConfig.setCredentialsPath(config.get("credential-file-path"));
            gcsConfig.setBucketName(config.get("bucket-name"));
            gcsConfig.setBasePath(config.get("base-path"));
            return gcsConfig;
        }
    }

    public static class AzureBlobStorageConfigFactory implements FileStorageConfigFactory<FileStorageProperties.AzureBlobStorageConfig> {
        @Override
        public FileStorageProperties.AzureBlobStorageConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.AzureBlobStorageConfig azureConfig = new FileStorageProperties.AzureBlobStorageConfig();
            azureConfig.setPlatform(config.get("platform-name"));
            azureConfig.setConnectionString(config.get("connection-string"));
            azureConfig.setContainerName(config.get("container-name"));
            azureConfig.setBasePath(config.get("base-path"));
            return azureConfig;
        }
    }

    public static class LocalFileStorageFactory implements FileStorageConfigFactory<FileStorageProperties.LocalPlusConfig> {
        @Override
        public FileStorageProperties.LocalPlusConfig getConfig(HashMap<String, String> config) {
            FileStorageProperties.LocalPlusConfig localConfig = new FileStorageProperties.LocalPlusConfig();
            localConfig.setPlatform(config.get("platform-name"));
            localConfig.setStoragePath(config.get("storage-path"));
            localConfig.setBasePath(config.get("base-path"));
            return localConfig;
        }
    }


}
