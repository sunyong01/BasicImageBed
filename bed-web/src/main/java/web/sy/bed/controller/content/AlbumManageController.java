package web.sy.bed.controller.content;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import web.sy.base.annotation.ApiTokenSupport;
import web.sy.base.annotation.RateLimit;
import web.sy.base.annotation.RequireAuthentication;
import web.sy.base.pojo.common.PaginationVO;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.dto.AlbumDTO;
import web.sy.base.pojo.entity.Album;
import web.sy.base.service.UserService;
import web.sy.bed.controller.BaseController;
import web.sy.bed.service.AlbumService;
import web.sy.bed.service.WebUserService;

import java.util.concurrent.TimeUnit;

@Tag(name = "相册管理接口", description = "相册的增删改查操作")
@RestController
@RequestMapping("/api/v1/albums")
@RateLimit(count = 10, time = 2, timeUnit = TimeUnit.SECONDS, prefix = "albums",limitByUser = true)
public class AlbumManageController extends BaseController {

    private final AlbumService albumService;

    public AlbumManageController(UserService userService, WebUserService webUserService, AlbumService albumService) {
        super(userService,webUserService);
        this.albumService = albumService;
    }

    @GetMapping
    @ApiTokenSupport
    @RequireAuthentication
    @Operation(summary = "获取相册列表", description = "获取当前用户的相册列表")
    public ResponseInfo<PaginationVO<Album>> getAlbumList(
            @RequestParam(required = false, defaultValue = "1")  @Min(0)Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(0) @Max(50) Integer page_size,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false) String keyword
    ) {
        String username = getCurrentUsername();
        return albumService.getAlbumList(username, page, page_size, keyword);
    }

    @PostMapping
    @RequireAuthentication
    @Operation(summary = "创建相册", description = "创建一个新的相册")
    public ResponseInfo<Album> createAlbum(@RequestBody @Valid AlbumDTO albumDTO) {
        String username = getCurrentUsername();
        albumDTO.setUsername(username);
        return albumService.createAlbum(albumDTO);
    }

    @PutMapping("/{id}")
    @RequireAuthentication
    @Operation(summary = "修改相册", description = "修改相册信息")
    public ResponseInfo<Album> updateAlbum(
            @PathVariable Long id,
            @RequestBody AlbumDTO albumDTO
    ) {
        String username = getCurrentUsername();
        albumDTO.setUsername(username);
        return albumService.updateAlbum(id, albumDTO);
    }

    @ApiTokenSupport
    @DeleteMapping("/{id}")
    @RequireAuthentication
    @Operation(summary = "删除相册", description = "删除指定相册")
    public ResponseInfo<String> deleteAlbum(@PathVariable Long id) {
        String username = getCurrentUsername();
        return albumService.deleteAlbum(id, username);
    }

    @PostMapping("/{albumId}/images")
    @RequireAuthentication
    @Operation(summary = "关联图片到相册", description = "将指定图片关联到相册中")
    public ResponseInfo<String> linkImageToAlbum(
            @PathVariable(required = false) String albumId,
            @RequestParam String key
    ) {
        String username = getCurrentUsername();
        if (albumId == null|| albumId.isEmpty()|| albumId.equals("null")) {
            return albumService.linkImageToAlbum(null, key, username);
        }
        return albumService.linkImageToAlbum(Long.parseLong(albumId), key, username);
    }

    @GetMapping("/{id}")
    @RequireAuthentication
    @Operation(summary = "获取相册信息", description = "获取指定ID的相册详细信息")
    public ResponseInfo<Album> getAlbumById(@PathVariable Long id) {
        String username = getCurrentUsername();
        return albumService.getAlbumById(id, username);
    }

    @PostMapping("/search")
    @RequireAuthentication
    @Operation(summary = "条件搜索相册", description = "根据相册信息条件搜索相册列表")
    public ResponseInfo<PaginationVO<Album>> searchAlbums(
            @RequestBody Album condition,
            @RequestParam(required = false, defaultValue = "1")  @Min(0)Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(0) @Max(50) Integer page_size,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        if (!isAdmin()||condition.getUsername()==null){
            String username = getCurrentUsername();
            // 如果不是管理员请求，强制设置用户名条件
            condition.setUsername(username);
        }
        return albumService.searchAlbums(condition, page, page_size, order);
    }
} 