package web.sy.basicimagebed.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import web.sy.basicimagebed.configuration.ConfigProperties;
import web.sy.basicimagebed.pojo.vo.ImageDataVO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j

@Component
public class ImageUtil {

    private static ConfigProperties configProperties;

    @Autowired
    public void setConfigProperties(ConfigProperties configProperties) {
        ImageUtil.configProperties = configProperties;
    }


    public static ImageDataVO getPrimaryImageInfo(ImageDataVO data) {
        if (data.getPathname() ==null ||data.getName() == null){
            throw new RuntimeException("文件不存于本地或已删除");
        }
        File file = new File(configProperties.getImageLocalStoragePath() + data.getPathname());

        try {
            // 获取文件大小
            data.setSize(Files.size(file.toPath()) / 1024.0f);

            // 获取图像宽度和高度
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                data.setWidth(image.getWidth());
                data.setHeight(image.getHeight());
            }

            // 计算文件的 MD5 和 SHA-1 哈希值
            data.setMd5(calculateHash(file, "MD5"));
            data.setSha1(calculateHash(file, "SHA-1"));

            // 获取文件的创建日期和格式化日期
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            Date creationDate = new Date(attr.creationTime().toMillis());
            data.setDate(creationDate);
            data.setHuman_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(creationDate));

        } catch (IOException | NoSuchAlgorithmException e) {
            log.warn("Failed to get primary image info: {}", e.getMessage());
        }
        data.setLinks(LinksUtil.createLinks(configProperties.getServerBaseUrl(), data.getPathname()));
        return data;
    }

    private static String calculateHash(File file, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();

        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static ImageDataVO storageFile(MultipartFile file) throws IOException{
        ImageDataVO imageData = new ImageDataVO();
        //当前时间按照yyyy/mm/dd格式生成文件夹
        String timePath =  new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String path = configProperties.getImageLocalStoragePath() + timePath + "/";

        String originalFilename = file.getOriginalFilename();
        imageData.setOrigin_name(originalFilename);

        String extension = "";
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex >= 0) {
                extension = "."+originalFilename.substring(dotIndex + 1);
            }
        }
        String key = getUniqueImageName();
        imageData.setKey(key);
        String filename =  key+ extension;
        imageData.setName(filename);
        File dest = new File(path +filename);
        imageData.setPathname(timePath + "/" + filename);
        dir(dest.getParent());
        file.transferTo(dest);

        return getPrimaryImageInfo(imageData);
    }

    // 如果文件夹不存在就递归创建文件夹
    public static void dir(String path) {
        File file = new File(path);
        boolean created=false;
        if (!file.exists()) {
            created = file.mkdirs();
        }
        if (created){
            log.atDebug().log("文件夹创建成功: "+path);
        }
    }

    public static void deleteFile(String path){
        File file = new File(configProperties.getImageLocalStoragePath() + path);
        if (file.exists()){
            file.delete();
        }
    }

    public static String getUniqueImageName() {
        // 获取当前时间的毫秒数
        long currentMillis = System.currentTimeMillis();
        // 获取毫秒数的后四位
        String millisPart = String.format("%04d", currentMillis % 10000);
        // 生成UUID并获取前12位
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        // 组合成最终的唯一字符串
        return millisPart + uuidPart;
    }

    public static MediaType getContentTypeForImage(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "bmp":
                return MediaType.parseMediaType("image/bmp");
            case "webp":
                return MediaType.parseMediaType("image/webp");
            default:
                return MediaType.parseMediaType("image/" + fileExtension.replace(".", ""));
//                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
