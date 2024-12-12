package web.sy.bed.service;

import org.springframework.web.multipart.MultipartFile;
import web.sy.base.pojo.common.PaginationVO;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.entity.ImageInfo;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageService {

    /**
     * 下载图片
     *
     * @param pathname     图片路径，格式为 "year/month/day/imageName"
     * @param outputStream 输出流
     */
    void downloadImage(String pathname, OutputStream outputStream);

    /**
     * 下载缩略图
     *
     * @param pathname     图片路径，格式为 "year/month/day/imageName"
     * @param outputStream 输出流
     */
    void downloadThumbnail(String pathname, OutputStream outputStream) throws IOException;

    String deleteImage(String userName, String key);
    ResponseInfo<ImageInfo> uploadImageByUserId(MultipartFile file, Long userId, Boolean isPublic, Integer albumId) throws IOException;
    ResponseInfo<PaginationVO<ImageInfo>> getImageList(String username, Integer page, Integer perPage, String order, String permission, Integer album_id, String keyword);

    /**
     * 根据key获取图片信息
     * @param key 图片key
     * @return 图片信息
     */
    ImageInfo getImageByKey(String key);

    /**
     * 更新图片信息
     * @param imageInfo 图片信
     */
    void updateImage(ImageInfo imageInfo);

    /**
     * 根据条件搜索图片
     * @param condition 搜索条件
     * @param page 页码
     * @param pageSize 每页数量
     * @param order 排序方式
     * @return 图片列表
     */
    ResponseInfo<PaginationVO<ImageInfo>> searchImages(ImageInfo condition, Integer page, Integer pageSize, String order);

    /**
     * 统计相册中的图片数量
     * @param albumId 相册ID
     * @return 图片数量
     */
    int countImagesByAlbumId(Long albumId);

    /**
     * 删除用户的所有图片和相册
     * @param userId 用户ID
     */
    void deleteAllUserImages(Long userId);
}
