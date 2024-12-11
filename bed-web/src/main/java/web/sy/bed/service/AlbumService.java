package web.sy.bed.service;

import web.sy.bed.base.pojo.common.ResponseInfo;
import web.sy.bed.base.pojo.dto.AlbumDTO;
import web.sy.bed.base.pojo.entity.Album;
import web.sy.bed.base.pojo.common.PaginationVO;

public interface AlbumService {

    ResponseInfo<PaginationVO<Album>> getAlbumList(String username, Integer page, Integer pageSize, String keyword);
    
    ResponseInfo<Album> createAlbum(AlbumDTO albumDTO);
    
    ResponseInfo<Album> updateAlbum(Long id, AlbumDTO albumDTO);
    
    ResponseInfo<String> deleteAlbum(Long id, String username);
    
    /**
     * 关联图片到相册
     * @param albumId 相册ID
     * @param key 图片key
     * @param username 用户名
     * @return 操作结果
     */
    ResponseInfo<String> linkImageToAlbum(Long albumId, String key, String username);
    
    /**
     * 获取指定ID的相册信息
     * @param id 相册ID
     * @param username 用户名
     * @return 相册信息
     */
    ResponseInfo<Album> getAlbumById(Long id, String username);
    
    /**
     * 根据条件搜索相册
     * @param condition 搜索条件
     * @param page 页码
     * @param pageSize 每页数量
     * @param order 排序方式
     * @return 相册列表
     */
    ResponseInfo<PaginationVO<Album>> searchAlbums(Album condition, Integer page, Integer pageSize, String order);
} 