package web.sy.bed.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dromara.x.file.storage.core.Downloader;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.get.RemoteFileInfo;
import org.dromara.x.file.storage.core.hash.HashInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.base.exception.StrategyNotAvailableException;
import web.sy.bed.base.mapper.AlbumMapper;
import web.sy.bed.base.mapper.ImageInfoMapper;
import web.sy.bed.base.mapper.UserMapper;
import web.sy.bed.base.mapper.UserProfileMapper;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.pojo.entity.ImageInfo;
import web.sy.bed.base.pojo.entity.User;
import web.sy.bed.base.pojo.entity.UserProfile;
import web.sy.bed.base.pojo.enums.OperationType;
import web.sy.bed.base.utils.ImageUtil;
import web.sy.bed.base.utils.LinksUtil;
import web.sy.bed.base.utils.ResponseBuilder;
import web.sy.bed.base.utils.TimeUtils;
import web.sy.bed.service.ImageAccessLogService;
import web.sy.bed.service.ImageService;
import web.sy.bed.service.StorageStrategySelector;
import web.sy.bed.base.pojo.common.PaginationVO;
import web.sy.bed.base.exception.ImageException;
import lombok.RequiredArgsConstructor;
import web.sy.bed.base.utils.PaginationUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static web.sy.bed.base.utils.ImageUtil.getUniqueImageName;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final String THUMBNAIL_SUFFIX = "_thb.jpg";
    private static final String DATE_FORMAT_PATTERN = "yyyy/MM/dd";

    private final ImageAccessLogService imageAccessLogService;
    @Resource
    ImageInfoMapper imageInfoMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    UserProfileMapper userProfileMapper;
    @Resource
    AlbumMapper albumMapper;
    @Resource
    private StorageStrategySelector storageStrategySelector;

    private void setImageLinks(List<ImageInfo> images) {
        String serverUrl = GlobalConfig.getConfig().getServerUrl();
        for (ImageInfo image : images) {
            image.setLinks(LinksUtil.createLinks(serverUrl, image.getPathname()));
            image.setHumanDate(TimeUtils.dateTimeToHumanDate(image.getDate()));
        }
    }

    @Override
    public void downloadImage(String pathname, OutputStream outputStream) {
        // 获取去除后缀的文件名
        String imageName = pathname.substring(pathname.lastIndexOf("/") + 1);
        String key = imageName.substring(0, imageName.lastIndexOf("."));
        val imageInfoByKey = imageInfoMapper.getImageInfoByKey(key);
        if (imageInfoByKey == null) {
            throw new RuntimeException("数据库无记录");
        }

        int strategy = imageInfoByKey.getStrategy();
        String strategyName = storageStrategySelector.getStrategyName((long) strategy);
        FileInfo download = new FileInfo()
                .setPlatform(strategyName)
                .setPath(imageInfoByKey.getPathname().replace(imageInfoByKey.getName(), ""))
                .setFilename(imageInfoByKey.getName());

        RemoteFileInfo remoteFileInfo = storageStrategySelector.getFileStorageService().getFile(download);
        if (remoteFileInfo == null) {
            throw new RuntimeException("文件不存在于对应的存储服务中!");
        }

        Downloader downloader = storageStrategySelector.getFileStorageService()
                .download(remoteFileInfo.toFileInfo())
                .setHashCalculatorMd5();
        downloader.outputStream(outputStream);
        String md5 = downloader.getHashCalculatorManager().getHashInfo().getMd5();

        // 添加日志记录
        imageAccessLogService.logAccess(
                pathname,
                userMapper.findByUsername(imageInfoByKey.getUploadUser()).getId(),
                Double.valueOf(imageInfoByKey.getSize()),
                OperationType.DOWNLOAD
        );

    }


    @Override
    public void downloadThumbnail(String pathname, OutputStream outputStream) throws IOException {
        String imageName = pathname.substring(pathname.lastIndexOf("/") + 1);
        String key = imageName.substring(0, imageName.lastIndexOf("."));
        String thumbName = key + THUMBNAIL_SUFFIX;
        ImageInfo imageInfoByKey = imageInfoMapper.getImageInfoByKey(key);
        if (imageInfoByKey == null) {
            throw new RuntimeException("数据库无记录");
        }
        String uploadUser = imageInfoByKey.getUploadUser();
        int strategy = imageInfoByKey.getStrategy();
        String strategyName = storageStrategySelector.getStrategyName((long) strategy);

        FileInfo download = new FileInfo()
                .setPlatform(strategyName)
                .setPath(imageInfoByKey.getPathname().replace(imageInfoByKey.getName(), ""))
                .setFilename(thumbName);

        RemoteFileInfo remoteDownFileInfo = storageStrategySelector.getFileStorageService().getFile(download);
        double size = 0.0;
        if (remoteDownFileInfo != null) {
            size = remoteDownFileInfo.getSize() / 1024.0;
            storageStrategySelector.getFileStorageService().download(remoteDownFileInfo.toFileInfo()).outputStream(outputStream);
            // 添加日志记录
            imageAccessLogService.logAccess(
                    pathname,
                    userMapper.findByUsername(uploadUser).getId(),
                    (double) remoteDownFileInfo.getSize() / 1024,
                    OperationType.THUMBNAIL_DOWNLOAD
            );
        } else {
            FileInfo original = new FileInfo()
                    .setPlatform(strategyName)
                    .setPath(imageInfoByKey.getPathname().replace(imageInfoByKey.getName(), ""))
                    .setBasePath("bed/")
                    .setFilename(imageInfoByKey.getName());
            RemoteFileInfo remoteFileInfo = storageStrategySelector.getFileStorageService().getFile(original);
            if (remoteFileInfo == null) {
                throw new RuntimeException("文件不存在于对应的存储服务中!");
            }

            log.debug("缩略图不存在，开始生成缩略图");
            byte[] thumbBytes = createAndUploadThumbnail(remoteFileInfo, imageInfoByKey, thumbName);

            // 将缩略图写入到输出流
            outputStream.write(thumbBytes);
        }
    }


    @Override
    public String deleteImage(String username, String key) {
        ImageInfo image = imageInfoMapper.getImageInfoByKey(key);
        if (image == null) {
            throw new ImageException("图片不存在");
        }
        if (!username.equals(image.getUploadUser())) {
            throw new ImageException("无权删除此图片");
        }
        
        double totalCapacityToAdd = deleteImageFiles(image);
        updateStorageCapacity(image, username, totalCapacityToAdd);
        
        return "删除成功";
    }

    private double deleteImageFiles(ImageInfo image) {
        int strategy = image.getStrategy();
        String strategyName = storageStrategySelector.getStrategyName((long) strategy);
        double totalCapacityToAdd = 0;

        // 删除原图
        FileInfo deleteFile = new FileInfo()
                .setPlatform(strategyName)
                .setPath(image.getPathname().replace(image.getName(), ""))
                .setFilename(image.getName());
        RemoteFileInfo remoteFileInfo = storageStrategySelector.getFileStorageService().getFile(deleteFile);
        if (remoteFileInfo != null) {
            storageStrategySelector.getFileStorageService().delete(remoteFileInfo.toFileInfo());
            totalCapacityToAdd += (double) remoteFileInfo.getSize() / 1024;
        }

        // 删除缩略图
        String thumbName = image.getName().substring(0, image.getName().lastIndexOf(".")) + THUMBNAIL_SUFFIX;
        FileInfo thumbFile = new FileInfo()
                .setPlatform(strategyName)
                .setPath(image.getPathname().replace(image.getName(), ""))
                .setFilename(thumbName);
        RemoteFileInfo remoteThumbFileInfo = storageStrategySelector.getFileStorageService().getFile(thumbFile);
        if (remoteThumbFileInfo != null) {
            storageStrategySelector.getFileStorageService().delete(remoteThumbFileInfo.toFileInfo());
            totalCapacityToAdd += (double) remoteThumbFileInfo.getSize() / 1024;
        }

        // 删除数据库记录
        int result = imageInfoMapper.deleteImageInfoByKey(image.getKey());
        if (result != 1) {
            throw new ImageException("数据库删除失败");
        }

        return totalCapacityToAdd;  // 返回释放的总空间
    }

    private void updateStorageCapacity(ImageInfo image, String username, double totalCapacityToAdd) {
        // 更新用户已用空间
        UserProfile userProfile = userProfileMapper.findByUserId(userMapper.findByUsername(username).getId());
        userProfileMapper.updateCapacityUsed(userProfile.getId(), userProfile.getCapacityUsed() - totalCapacityToAdd);
        
        // 更新策略已用容量
        storageStrategySelector.updateStrategyUsedCapacity((long) image.getStrategy(), -totalCapacityToAdd);
    }

    @Override
    public ResponseInfo<ImageInfo> uploadImageByUserId(MultipartFile file, Long userId, Boolean isPublic, Integer albumId) throws IOException {
        // 获取用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new ImageException("用户不存在");
        }

        // 获取用户空间信息
        UserProfile userProfile = userProfileMapper.findByUserId(userId);
        if (userProfile == null) {
            throw new ImageException("用户配置信息不存在");
        }

        // 检查用户空间是否足够
        double remainingSpace = userProfile.getCapacity() - userProfile.getCapacityUsed();
        ImageUtil.validateFile(file, remainingSpace);

        try {
            String key = getUniqueImageName();
            String timePath = new SimpleDateFormat(DATE_FORMAT_PATTERN).format(new Date()) + "/";
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new ImageException("文件名不能为空");
            }

            String extension = ImageUtil.getFileExtension(originalFilename);
            String newFileName = key + "." + extension;
            
            HashMap<String, String> usedStrategyByPriority = storageStrategySelector.getUseStrategyIdAndNameByPriority();

            ImageInfo imageInfoToSave = new ImageInfo();
            imageInfoToSave.setKey(key);
            imageInfoToSave.setName(newFileName);
            imageInfoToSave.setOriginName(originalFilename);
            imageInfoToSave.setDate(new Date());
            imageInfoToSave.setAlbumId(albumId != null ? albumId : 0);
            imageInfoToSave.setSize((float) (file.getSize() / 1024.0));
            imageInfoToSave.setStrategy(Integer.valueOf(usedStrategyByPriority.get("StrategyId")));
            imageInfoToSave.setUploadUser(user.getUsername());
            imageInfoToSave.setIsPublic(isPublic != null ? isPublic : false);
            InputStream inputStream = file.getInputStream();
            // 读取图片输入流获取 BufferedImage 对象
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            // 获取图片的宽度和高度
            int originalWidth = bufferedImage.getWidth();
            int originalHeight = bufferedImage.getHeight();
            imageInfoToSave.setWidth(originalWidth);
            imageInfoToSave.setHeight(originalHeight);

            FileInfo upload = storageStrategySelector.getFileStorageService()
                    .of(file.getInputStream())
                    .setHashCalculatorMd5()
                    .setHashCalculatorSha1()
                    .setPlatform(usedStrategyByPriority.get("StrategyName"))
                    .setPath(timePath)
                    .setSaveFilename(newFileName)
                    .upload();

            HashInfo hashInfo = upload.getHashInfo();

            // pathname为 path+name
            imageInfoToSave.setPathname(upload.getPath() + upload.getFilename());

            imageInfoToSave.setSha1(hashInfo.getSha1());
            imageInfoToSave.setMd5(hashInfo.getMd5());

            if (upload.getUrl() == null) {

                imageInfoMapper.deleteImageInfoByKey(imageInfoToSave.getKey());
                // 先不改数据库，再次测试后再失败再改数据库
                if (!storageStrategySelector.testStrategyByName(usedStrategyByPriority.get("StrategyName"))) {
                    storageStrategySelector.testStrategy(Integer.valueOf(usedStrategyByPriority.get("StrategyId")));
                }
                throw new StrategyNotAvailableException("上传到存储策略失败!");
            }
            log.debug("Saving ImageInfo: {}", imageInfoToSave);
            int result = imageInfoMapper.insertImageInfo(imageInfoToSave);

            if (result != 1) {
                throw new RuntimeException("DB insert Error");
            }

            // 更新用户已用空间
            userProfileMapper.updateCapacityUsed(userId, userProfile.getCapacityUsed() + file.getSize() / 1024.0);

            // 更新策略已用容量
            storageStrategySelector.updateStrategyUsedCapacity(
                    Long.valueOf(usedStrategyByPriority.get("StrategyId")),
                    file.getSize() / 1024.0
            );

            // 记录上传日志
            imageAccessLogService.logAccess(
                    imageInfoToSave.getPathname(),
                    userId,
                    (double) file.getSize() / 1024,
                    OperationType.UPLOAD
            );

            // 设置链接信息
            imageInfoToSave.setLinks(LinksUtil.createLinks(GlobalConfig.getConfig().getServerUrl(), imageInfoToSave.getPathname()));
            imageInfoToSave.setHumanDate(TimeUtils.dateTimeToHumanDate(imageInfoToSave.getDate()));

            return ResponseBuilder.success(imageInfoToSave);
        } catch (Exception e) {
            log.error("Upload failed: ", e);
            throw e;
        }
    }


    @Override
    public ResponseInfo<PaginationVO<ImageInfo>> getImageList(String username, Integer page, Integer perPage, String order, String is_public, Integer album_id, String keyword) {
        int offset = PaginationUtil.calculateOffset(page, perPage);

        List<ImageInfo> imageList = imageInfoMapper.getAllImageByUser(username, offset, perPage, order, is_public, album_id, keyword);
        int total = imageInfoMapper.countAllImageByUser(username);

        setImageLinks(imageList);
        return ResponseBuilder.success(PaginationUtil.buildPaginationVO(imageList, page, perPage, total));
    }

    @Override
    public ImageInfo getImageByKey(String key) {
        return imageInfoMapper.getImageByKey(key);
    }

    @Override
    public void updateImage(ImageInfo imageInfo) {
        imageInfoMapper.updateImage(imageInfo);
    }

    @Override
    public ResponseInfo<PaginationVO<ImageInfo>> searchImages(ImageInfo condition, Integer page, Integer pageSize, String order) {
        int offset = PaginationUtil.calculateOffset(page, pageSize);

        List<ImageInfo> imageList = imageInfoMapper.searchImages(condition, offset, pageSize, order);
        int total = imageInfoMapper.countSearchImages(condition);

        setImageLinks(imageList);
        return ResponseBuilder.success(PaginationUtil.buildPaginationVO(imageList, page, pageSize, total));
    }

    @Override
    public int countImagesByAlbumId(Long albumId) {
        return imageInfoMapper.countImagesByAlbumId(albumId);
    }


    private byte[] createAndUploadThumbnail(RemoteFileInfo remoteFileInfo,
                                            ImageInfo imageInfoByKey, String thumbName) throws IOException {
        FileInfo eOriginfileInfo = remoteFileInfo.toFileInfo();

        // 下载原图
        byte[] originByte = storageStrategySelector.getFileStorageService()
                .download(eOriginfileInfo)
                .bytes();

        // 生成缩略图
        byte[] thumbBytes = ImageUtil.generateThumbnail(originByte, 300, 200);

        // 上传缩略图
        storageStrategySelector.getFileStorageService().of(thumbBytes)
                .setPlatform(eOriginfileInfo.getPlatform())
                .setPath(imageInfoByKey.getPathname().replace(imageInfoByKey.getName(), ""))
                .setSaveFilename(thumbName)
                .upload();

        // 更新用户已用空间
        String uploadUser = imageInfoByKey.getUploadUser();
        UserProfile userProfile = userProfileMapper.findByUserId(userMapper.findByUsername(uploadUser).getId());
        double thumbSizeKb = (double) thumbBytes.length / 1024;
        userProfileMapper.updateCapacityUsed(userProfile.getId(), userProfile.getCapacityUsed() + thumbSizeKb);

        // 更新策略已用容量
        storageStrategySelector.updateStrategyUsedCapacity((long) imageInfoByKey.getStrategy(), thumbSizeKb);

        // 添加日志记录
        imageAccessLogService.logAccess(
                imageInfoByKey.getPathname(),
                userMapper.findByUsername(uploadUser).getId(),
                (double) thumbBytes.length / 1024,
                OperationType.THUMBNAIL_UPLOAD
        );

        return thumbBytes;
    }

    @Transactional
    @Override
    public void deleteAllUserImages(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new ImageException("用户不存在");
        }

        List<ImageInfo> images = imageInfoMapper.getAllImageByUser(user.getUsername(), null, null, null, null, null, null);
        double totalCapacityToAdd = 0;

        // 删除每张图片及其缩略图
        for (ImageInfo image : images) {
            totalCapacityToAdd += deleteImageFiles(image);
        }

        // 删除数据库中的图片记录
        imageInfoMapper.deleteImagesByUserId(userId);

        // 删除相册
        albumMapper.getAlbumList(user.getUsername(), null, null, null)
                .forEach(album -> albumMapper.deleteAlbum(album.getId()));

        // 更新用户已用空间
        UserProfile userProfile = userProfileMapper.findByUserId(userId);
        userProfileMapper.updateCapacityUsed(userProfile.getId(), 0.0);  // 重置为0

        // 更新所有使用的策略的容量
        storageStrategySelector.updateStrategyUsedCapacity((long) images.get(0).getStrategy(), -totalCapacityToAdd);

        log.debug("Deleted all images and albums for user: {}, freed space: {}KB", user.getUsername(), totalCapacityToAdd);
    }

}
