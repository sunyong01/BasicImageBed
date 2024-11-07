package web.sy.basicimagebed.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import web.sy.basicimagebed.pojo.vo.AlbumsVO;


import java.util.List;

@Mapper
public interface AlbumsMapper {


    List<AlbumsVO> getAllAlbumsByUser(@Param("email") String email,
                                      @Param("page") int page,
                                      @Param("perPage") int perPage,
                                      @Param("order") String order,
                                      @Param("keyword") String keyword);

    @Select("SELECT COUNT(*)  FROM tb_albums WHERE create_user = #{email}")
    int countAllAlbumsByUser(@Param("email") String email);

    @Select("SELECT * FROM tb_albums WHERE create_user = #{email} AND id = #{album_id}")
    AlbumsVO getAlbumsById(@Param("email") String email, @Param("album_id") Integer album_id);

    @Delete("DELETE FROM tb_albums WHERE create_user = #{email}")
    int deleteAlbumsByUserEmail(String email);

    int deleteAlbumsByEmailAndId(@Param("email") String email, @Param("album_id") String album_id);

    int setAlbumsToDefaultWhileDeleteAlbum(@Param("email") String email, @Param("album_id") Integer album_id);
}
