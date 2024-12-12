package web.sy.bed.controller.content;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.sy.base.annotation.Anonymous;
import web.sy.base.utils.ImageUtil;
import web.sy.bed.service.ImageService;

import java.io.IOException;

@Anonymous
@RestController
@Slf4j
public class ImageController {

    ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @GetMapping("/image/**")
    public void downloadImage(HttpServletRequest request,
                            @RequestParam(value = "sign", required = false) String sign,
                            HttpServletResponse response) throws IOException {
        String pathname = extractPathFromPattern(request);
        String fileExtension = pathname.substring(pathname.lastIndexOf(".") + 1);
        MediaType mediaType = ImageUtil.getContentTypeForImage(fileExtension);
        if (mediaType == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(mediaType.toString());
        try {
            imageService.downloadImage(pathname, response.getOutputStream());
        } catch (Exception e) {
            log.error("Error downloading image: {}", pathname, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/thumb/**")
    public void thumbnail(HttpServletRequest request,
                         @RequestParam(value = "sign", required = false) String sign,
                         HttpServletResponse response) throws IOException {
        String pathname = extractPathFromPattern(request);
        // 缩略图总是 JPEG 格式
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        try {
            imageService.downloadThumbnail(pathname, response.getOutputStream());
        } catch (Exception e) {
            log.error("Error downloading thumbnail: {}", pathname, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String extractPathFromPattern(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        path = path.substring(contextPath.length());
        // 移除 /image/ 或 /thumb/ 前缀
        return path.substring(path.indexOf("/", 1) + 1);
    }
}