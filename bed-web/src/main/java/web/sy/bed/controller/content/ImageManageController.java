package web.sy.bed.controller.content;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.sy.bed.base.annotation.ApiTokenSupport;
import web.sy.bed.base.annotation.RateLimit;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.controller.BaseController;
import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.pojo.entity.ImageInfo;
import web.sy.bed.service.WebUserService;
import web.sy.bed.base.pojo.common.PaginationVO;
import web.sy.bed.service.ImageService;
import web.sy.bed.base.service.UserService;
import web.sy.bed.base.utils.ResponseBuilder;
import web.sy.bed.base.annotation.RequireAuthentication;

import java.util.concurrent.TimeUnit;

@Tag(name = "图片管理接口", description = "图片上传、删除等操作")
@RestController
@RateLimit(count = 10, time = 10, timeUnit = TimeUnit.SECONDS, prefix = "images",limitByUser = true)
@RequestMapping("/api/v1")
public class ImageManageController extends BaseController {

    private final ImageService imageService;

    public ImageManageController(UserService userService, WebUserService webUserService, ImageService imageService) {
        super(userService,webUserService);
        this.imageService = imageService;
    }

    @Operation(summary = "上传图片", description = "上传图片文件并返回图片信息")
    @PostMapping({"/upload", "/images/upload"})
    @ApiTokenSupport
    @RequireAuthentication
    public ResponseInfo<ImageInfo> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic,
            @RequestParam(value = "albumId", required = false) Integer albumId
    ) {
        try {
            Long userId = getCurrentUserId();
            return imageService.uploadImageByUserId(file, userId, isPublic, albumId);
        } catch (Exception e) {
            return ResponseBuilder.error("图片上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/images")
    @RequireAuthentication
    @ApiTokenSupport
    @Operation(summary = "获取图片列表", description = "获取图片列表，普通用户只能查看自己的图片")
    public ResponseInfo<PaginationVO<ImageInfo>> getImageList(
            @RequestParam(required = false) String username,
            @Schema(description = "权限 1=公开 0=私有", example = "1")
            @RequestParam(required = false) String permission,
            @RequestParam(required = false) Integer album_id,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1")  @Min(0)Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(0) @Max(50) Integer page_size,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        if (!isAdmin()||username==null){
            username = getCurrentUsername();
        }
        return imageService.getImageList(username, page, page_size, order, permission, album_id, keyword);
    }

    @DeleteMapping("/images/{id}")
    @RequireAuthentication
    @ApiTokenSupport
    @Operation(summary = "删除图片", description = "删除指定图片")
    public ResponseInfo<String> deleteImage(@PathVariable String id) {
        String userName = getCurrentUsername();
        return ResponseBuilder.success(imageService.deleteImage(userName, id));
    }

    @PostMapping("/images/search")
    @RequireAuthentication
    @Operation(summary = "条件搜索图片", description = "根据图片信息条件搜索图片列表")
    public ResponseInfo<PaginationVO<ImageInfo>> searchImages(
            @RequestBody ImageInfo condition,
            @RequestParam(required = false, defaultValue = "1")  @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(0) @Max(50) Integer page_size,
            @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        if (!isAdmin()||condition.getUploadUser()==null){
            condition.setUploadUser(getCurrentUsername());
        }
        return imageService.searchImages(condition, page, page_size, order);
    }

    @PutMapping("/images/open/{key}")
    @RequireAuthentication
    @Operation(summary = "改变某图片的公开状态", description = "改变某图片的公开状态")
    public ResponseInfo<String> changeImagePublic(@PathVariable String key) {
        ImageInfo imageInfo = imageService.getImageByKey(key);
        if (imageInfo == null) {
            return ResponseBuilder.error("图片不存在");
        }
        if (!isAdmin() && !imageInfo.getUploadUser().equals(getCurrentUsername())) {
            return ResponseBuilder.error("无权限操作");
        }
        imageInfo.setIsPublic(!imageInfo.getIsPublic());
        imageService.updateImage(imageInfo);
        return ResponseBuilder.success("操作成功");
    }

    @PostMapping("/images/public")
    @RequireAuthentication
    @Operation(summary = "条件搜索图片(只看公开)", description = "获取匿名的图片列表")
    public ResponseInfo<PaginationVO<ImageInfo>> searchGalleryImages(
            @RequestBody ImageInfo condition,
            @RequestParam(required = false, defaultValue = "1") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(0) @Max(50) Integer page_size,
            @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        if (!GlobalConfig.getConfig().getAllowGuest()) {
            return ResponseBuilder.error("系统策略禁用了此功能");
        }
        condition.setIsPublic(true);
        return imageService.searchImages(condition, page, page_size, order);
    }

} 