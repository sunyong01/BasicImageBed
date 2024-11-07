package web.sy.basicimagebed.controller.use;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.vo.AlbumsVO;
import web.sy.basicimagebed.pojo.vo.ImageDataVO;
import web.sy.basicimagebed.pojo.vo.PaginationVO;
import web.sy.basicimagebed.service.AlbumsService;
import web.sy.basicimagebed.service.ImageService;
import web.sy.basicimagebed.utils.ResponseBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Image Bed API", description = "图床相关接口")

public class ImageBedAPIv1 {
    @Resource
    ImageService imageService;

    @Resource
    AlbumsService albumsService;

    @PostMapping("/upload")
    public ResponseInfo<ImageDataVO> uploadImage(HttpServletRequest request,
                                                 @RequestBody MultipartFile file) throws IOException {
        String userEmail = (String) request.getAttribute("userEmail");
        //因为已经在拦截器中验证了用户是否登录，所以当配置了允许游客上传时，userEmail可能为空
        if (userEmail == null) {
            return imageService.uploadImage(file);
        }
        return imageService.uploadImage(file, userEmail);
    }

    @GetMapping("/images")
    public ResponseInfo<PaginationVO<ImageDataVO>> getImageList(
            HttpServletRequest request,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String permission,
            @RequestParam(required = false) Integer album_id,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page_size

    ) {
        String userEmail = (String) request.getAttribute("userEmail");

        return imageService.getImageList(userEmail, page, page_size, order, permission, album_id, keyword);
    }

    @DeleteMapping("/images/{key}")
    public ResponseInfo<String> deleteImage(HttpServletRequest request, @PathVariable String key) {
        String userEmail = (String) request.getAttribute("userEmail");
        if("guest".equals(userEmail)){
            return ResponseBuilder.error("Guest can't delete image!");
        }
        return ResponseBuilder.success(imageService.deleteImage(userEmail, key));
    }

    @GetMapping("/albums")
    public ResponseInfo<PaginationVO<AlbumsVO>> getAlbumsList(
            HttpServletRequest request,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String keyword
    ) {
        String userEmail = (String) request.getAttribute("userEmail");
        return albumsService.getAlbumsList(page, order, keyword, userEmail);
    }

    @DeleteMapping("/albums/{id}")
    public ResponseInfo<String> deleteAlbums(
            HttpServletRequest request,
            @PathVariable Integer id) {
        String userEmail = (String) request.getAttribute("userEmail");
        return ResponseBuilder.success(albumsService.deleteAlbums(userEmail, id));
    }

}
