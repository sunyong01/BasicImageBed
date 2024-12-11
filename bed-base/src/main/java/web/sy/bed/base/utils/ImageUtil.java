package web.sy.bed.base.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.base.exception.ImageException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class ImageUtil {

    /**
     * 验证上传的图片文件
     *
     * @param file 上传的文件
     * @param remainingSpace 剩余空间（KB）
     * @throws ImageException 当验证失败时抛出异常
     */
    public static void validateFile(MultipartFile file, double remainingSpace) {
        double fileSizeKb = file.getSize() / 1024.0;
        if (fileSizeKb > remainingSpace) {
            throw new ImageException(String.format("用户空间不足，剩余空间: %.2fKB，文件大小: %.2fKB", 
                remainingSpace, fileSizeKb));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new ImageException("文件名不能为空");
        }

        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        Set<String> allowedSuffixes = Set.of(GlobalConfig.getConfig().getAllowedTypes().split(","));
        if (!allowedSuffixes.contains(fileSuffix)) {
            throw new ImageException("不支持的文件类型: " + fileSuffix);
        }

        int allowedSizeKb = GlobalConfig.getConfig().getMaxSize();
        if (fileSizeKb > allowedSizeKb) {
            throw new ImageException(String.format("文件大小超出限制: %.2fKB", fileSizeKb));
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

    public static byte[] generateThumbnail(byte[] originalBytes, int width, int height) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(originalBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
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
        }
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName 文件名
     * @return 文件后缀（小写）
     * @throws ImageException 当文件名格式无效时抛出异常
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty() || !fileName.contains(".")) {
            throw new ImageException("无效的文件名格式");
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
