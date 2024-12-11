package web.sy.bed.base.mapper;

import org.apache.ibatis.annotations.*;
import web.sy.bed.base.pojo.entity.ImageInfo;

import java.util.List;
import java.util.Map;

@Mapper
public interface ImageInfoMapper {

    ImageInfo getImageInfoByKey(String key);

    int insertImageInfo(ImageInfo imageInfo);

    @Update("UPDATE tb_image_info SET `key` = #{key}, name = #{name}, origin_name = #{originName}, pathname = #{pathname}, size = #{size}, width = #{width}, height = #{height}, md5 = #{md5}, sha1 = #{sha1}, date = #{date}, strategy = #{strategy}, is_public = #{isPublic}, upload_user = #{uploadUser},album_id = #{albumsId} WHERE `key` = #{key}")
    int updateImageInfo(ImageInfo imageInfo);

    @Delete("DELETE FROM tb_image_info WHERE `key` = #{key}")
    int deleteImageInfoByKey(String key);

    int countAllImageByUser(@Param("username") String username);

    List<ImageInfo> getAllImageByUser(
            @Param("username") String username,
            @Param("offset") Integer offset,
            @Param("perPage") Integer perPage,
            @Param("order") String order,
            @Param("is_public") String is_public,
            @Param("album_id") Integer album_id,
            @Param("keyword") String keyword
    );

    int getTotalImageCount(@Param("username") String username,
                           @Param("is_public") String is_public,
                           Integer album_id,
                           String keyword);

    /**
     * 根据key获取图片信息
     *
     * @param key 图片key
     * @return 图片信息
     */
    ImageInfo getImageByKey(String key);

    /**
     * 更新图片信息
     *
     * @param imageInfo 图片信息
     */
    void updateImage(ImageInfo imageInfo);

    /**
     * 条件搜索图片
     */
    List<ImageInfo> searchImages(@Param("condition") ImageInfo condition,
                                @Param("offset") Integer offset,
                                @Param("pageSize") Integer pageSize,
                                @Param("order") String order);

    /**
     * 统计符合条件的图片数量
     */
    int countSearchImages(@Param("condition") ImageInfo condition);

    /**
     * 统计相册中的图片数量
     * @param albumId 相册ID
     * @return 图片数量
     */
    int countImagesByAlbumId(@Param("albumId") Long albumId);

    /**
     * 统计用户的图片数量
     */
    long countImagesByUserId(@Param("userId") Long userId);

    /**
     * 获取图片类型分布统计
     */
    List<Map<String, Object>> getImageTypeDistribution(@Param("userId") Long userId);

    /**
     * 统计所有图片数量
     */
    long countAllImages();

    /**
     * 删除用户的所有图片
     * @param userId 用户ID
     */
    void deleteImagesByUserId(@Param("userId") Long userId);
}
