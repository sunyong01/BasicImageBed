package web.sy.basicimagebed.controller.use;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.sy.basicimagebed.service.ImageService;
import web.sy.basicimagebed.utils.ImageUtil;

import javax.print.attribute.standard.Media;
import java.io.File;


@RequestMapping("/image")
@Controller
public class ImageController {
    @jakarta.annotation.Resource
    ImageService imageService;

    private static ImageUtil imageUtil;

    @jakarta.annotation.Resource
    public void setImageUtil(ImageUtil imageUtil) {
        ImageController.imageUtil = imageUtil;
    }

    @GetMapping("/{year}/{month}/{day}/{imageName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String year,
                                                  @PathVariable String month,
                                                  @PathVariable String day,
                                                  @PathVariable String imageName,
                                                  @RequestParam(value = "sign", required = false) String sign
    ) {
        String filePath = imageService.downloadImage(year, month, day, imageName);
        if (filePath == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(filePath);
        Resource resource = new FileSystemResource(file);
        String fileExtension = imageName.substring(imageName.lastIndexOf(".") + 1);
        return ResponseEntity.ok()
                .contentType(ImageUtil.getContentTypeForImage(fileExtension))
                .body(resource);
    }
}
