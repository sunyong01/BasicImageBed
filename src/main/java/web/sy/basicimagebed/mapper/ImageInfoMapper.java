package web.sy.basicimagebed.mapper;

import org.apache.ibatis.annotations.*;
import web.sy.basicimagebed.pojo.dao.ImageInfoDAO;

import java.util.List;

@Mapper
public interface ImageInfoMapper {


    ImageInfoDAO getImageInfoByKey(String key);

    int insertImageInfo(ImageInfoDAO imageInfoDAO);

    @Update("UPDATE tb_image_info SET `key` = #{key}, name = #{name}, origin_name = #{originName}, pathname = #{pathname}, size = #{size}, width = #{width}, height = #{height}, md5 = #{md5}, sha1 = #{sha1}, date = #{date}, strategy = #{strategy}, is_public = #{isPublic}, upload_user = #{uploadUser},album_id = #{albumsId} WHERE `key` = #{key}")
    int updateImageInfo(ImageInfoDAO imageInfoDAO);

    @Delete("DELETE FROM tb_image_info WHERE `key` = #{key}")
    int deleteImageInfoByKey(String key);

    @Select("SELECT COUNT(*) FROM tb_image_info WHERE upload_user = #{email}")
    int countAllImageByUser(String email);

    List<ImageInfoDAO> getAllImageByUser(
            @Param("email") String email,
            @Param("page") Integer page,
            @Param("perPage") Integer perPage,
            @Param("order") String order,
            @Param("is_public") String is_public,
            @Param("album_id") Integer album_id,
            @Param("keyword") String keyword
    );

    int getTotalImageCount(@Param("email") String email,
                           @Param("is_public") String is_public,
                           Integer album_id,
                           String keyword);

}
