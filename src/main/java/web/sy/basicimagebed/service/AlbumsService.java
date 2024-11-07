package web.sy.basicimagebed.service;

import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.vo.AlbumsVO;
import web.sy.basicimagebed.pojo.vo.PaginationVO;

public interface AlbumsService {
    ResponseInfo<PaginationVO<AlbumsVO>> getAlbumsList(Integer page, String order, String keyword, String email);

    String deleteAlbums(String email, Integer album_id);

}
