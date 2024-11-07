package web.sy.basicimagebed.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import web.sy.basicimagebed.configuration.ConfigProperties;
import web.sy.basicimagebed.mapper.ImageInfoMapper;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.dao.ImageInfoDAO;
import web.sy.basicimagebed.pojo.vo.ImageDataVO;
import web.sy.basicimagebed.pojo.vo.PaginationVO;
import web.sy.basicimagebed.service.ImageService;
import web.sy.basicimagebed.strategy.StorageStrategy;
import web.sy.basicimagebed.strategy.StrategyEnum;
import web.sy.basicimagebed.utils.ImageUtil;
import web.sy.basicimagebed.utils.LinksUtil;
import web.sy.basicimagebed.utils.ResponseBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    ConfigProperties configProperties;

    @Resource
    ImageInfoMapper imageInfoMapper;

    @Override
    public String downloadImage(String year, String month, String day, String imageName) {
        String filename = year + "/" + month + "/" + day + "/" + imageName;
        ImageDataVO data = new ImageDataVO();
        data.setPathname(filename);
        // 获取去除后缀的文件名
        String key = imageName.substring(0, imageName.lastIndexOf("."));
        val imageInfoByKey = imageInfoMapper.getImageInfoByKey(key);
        if (imageInfoByKey == null) {
            throw new RuntimeException("数据库无记录");
        }
        // 获取策略
        int strategy = imageInfoByKey.getStrategy();
        StorageStrategy storageStrategy = StrategyEnum.getStrategyById(strategy);
        // 获取文件路径
        boolean get = storageStrategy.getImage(data, configProperties.getImageLocalStoragePath());
        if (!get) {
            throw new RuntimeException("Download image from Strategy failed");
        }

        return configProperties.getImageLocalStoragePath() + year + "/" + month + "/" + day + "/" + imageName;
    }

    @Override
    public String deleteImage(String username,String key) {
        ImageInfoDAO imageInfoByKey = imageInfoMapper.getImageInfoByKey(key);
        if (imageInfoByKey == null) {
            throw new RuntimeException("数据库无记录");
        }
        if (!username.equals(imageInfoByKey.getUploadUser())) {

            throw new RuntimeException(username+" , You have no permission to delete this image");
        }

        int strategy = imageInfoByKey.getStrategy();
        StorageStrategy storageStrategy = StrategyEnum.getStrategyById(strategy);
        ImageDataVO vo = new ImageDataVO();
        vo.setPathname(imageInfoByKey.getPathname());
        boolean delete = storageStrategy.deleteImage(vo, configProperties.getImageLocalStoragePath());
        if (!delete) {
            throw new RuntimeException("Delete image from Strategy failed");
        }
        int result = imageInfoMapper.deleteImageInfoByKey(key);
        if (result != 1) {
            throw new RuntimeException("DB delete Error");
        }
        return "Delete success";
    }


    @Override
    public ResponseInfo<ImageDataVO> uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, "guest");
    }

    @Override
    public ResponseInfo<ImageDataVO> uploadImage(MultipartFile file, String email) throws IOException {
        return uploadImage(file, email, 1);
    }

    @Override
    public ResponseInfo<ImageDataVO> uploadImage(MultipartFile file, String email, int strategy) throws IOException {
        // 获取配置参数
        Set<String> allowedSuffixes = configProperties.getAllowSuffix();
        int allowedSizeKb = configProperties.getAllowSizeKb();

        // 判断文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new RuntimeException("File name is null");
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedSuffixes.contains(fileSuffix)) {
            throw new RuntimeException("Unsupported file type: " + fileSuffix);
        }

        // 判断文件大小
        long fileSizeKb = file.getSize() / 1024;
        if (fileSizeKb > allowedSizeKb) {
            throw new RuntimeException("File size exceeds the limit: " + fileSizeKb + "KB");
        }

        ImageDataVO s = ImageUtil.storageFile(file);
        s.setOrigin_name(fileName);
        log.atInfo().log("Image uploaded to local: {}", s);
        //存储到数据库
        ImageInfoDAO imageInfoDAO = new ImageInfoDAO();

        imageInfoDAO.setByImageDataVO(s);
        // 默认相册设置为0
        imageInfoDAO.setAlbumId(0);
        // TODO 设置public 上传用户 是否公开
        imageInfoDAO.setStrategy(strategy);
        imageInfoDAO.setUploadUser(Objects.requireNonNullElse(email, "guest"));
        imageInfoDAO.setPublic(true);
        log.info("ImageInfoDAO: {}", imageInfoDAO);
        int result = imageInfoMapper.insertImageInfo(imageInfoDAO);

        if (result != 1) {
            ImageUtil.deleteFile(s.getPathname());
            throw new RuntimeException("DB insert Error");
        }

        StorageStrategy storageStrategy = StrategyEnum.getStrategyById(strategy);
        boolean uploaded = storageStrategy.uploadImage(s, configProperties.getImageLocalStoragePath());
        if (!uploaded) {
            ImageUtil.deleteFile(s.getPathname());
            imageInfoMapper.deleteImageInfoByKey(imageInfoDAO.getKey());
            throw new RuntimeException("Upload image to Strategy failed");
        }
        return ResponseBuilder.success(s);
    }

    @Transactional
    @Override
    public void deleteImagesByUserEmail(String email) {
        List<ImageInfoDAO> images = imageInfoMapper.getAllImageByUser(email, null, null, null, null, null, null);
        for (ImageInfoDAO image : images) {
            int strategy = image.getStrategy();
            StorageStrategy storageStrategy = StrategyEnum.getStrategyById(strategy);
            boolean delete = storageStrategy.deleteImage(image.toImageDataVO(), configProperties.getImageLocalStoragePath());
            if (!delete) {
                throw new RuntimeException("Delete image from Strategy failed");
            }
            int result = imageInfoMapper.deleteImageInfoByKey(image.getKey());
            if (result != 1) {
                throw new RuntimeException("DB delete Error");
            }
        }
    }
    @Override
    public ResponseInfo<PaginationVO<ImageDataVO>> getImageList(String email, Integer page, Integer perPage, String order, String permission, Integer album_id, String keyword) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (perPage == null || perPage < 1) {
            perPage = 20; // 默认每页20条数据
        }
        List<ImageInfoDAO> imageList = imageInfoMapper.getAllImageByUser(email, page, perPage, order, permission, album_id, keyword);

        int total = imageInfoMapper.countAllImageByUser(email);

        PaginationVO<ImageDataVO> paginationVO= new PaginationVO<>();
        paginationVO.setCurrent_page(page);
        paginationVO.setPer_page(perPage);
        paginationVO.setTotal(total);
        paginationVO.setLast_page((int) Math.ceil((double) total / perPage));
        List<ImageDataVO> data= imageList.stream().map(ImageInfoDAO::toImageDataVO).toList();
        for (ImageDataVO imageDataVO : data) {
            imageDataVO.setLinks(LinksUtil.createLinks(configProperties.getServerBaseUrl(), imageDataVO.getPathname()));
        }
        paginationVO.setData(data);
        return ResponseBuilder.success(paginationVO);
    }
}
