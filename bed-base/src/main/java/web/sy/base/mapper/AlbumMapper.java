package web.sy.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import web.sy.base.pojo.entity.Album;

import java.util.List;

@Mapper
public interface AlbumMapper {
    /**
     * 获取用户的相册列表
     */
    List<Album> getAlbumList(
        @Param("username") String username,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize,
        @Param("keyword") String keyword
    );

    /**
     * 统计用户的相册数量
     */
    int countAlbums(
        @Param("username") String username,
        @Param("keyword") String keyword
    );

    /**
     * 检查相册名是否已存在
     */
    int checkAlbumNameExists(
        @Param("username") String username,
        @Param("name") String name
    );

    /**
     * 插入新相册
     */
    void insertAlbum(Album album);

    /**
     * 根据ID获取相册信息
     */
    Album getAlbumById(@Param("id") Long id);

    /**
     * 更新相册信息
     */
    void updateAlbum(Album album);

    /**
     * 删除相册
     */
    void deleteAlbum(@Param("id") Long id);

    /**
     * 条件搜索相册
     */
    List<Album> searchAlbums(
        @Param("condition") Album condition,
        @Param("offset") int offset,
        @Param("pageSize") Integer pageSize,
        @Param("order") String order
    );

    /**
     * 统计符合条件的相册数量
     */
    int countSearchAlbums(@Param("condition") Album condition);

    /**
     * 统计所有相册数量
     */
    long countAllAlbums();
} 