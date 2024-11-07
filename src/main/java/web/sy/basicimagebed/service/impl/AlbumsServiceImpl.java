package web.sy.basicimagebed.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sy.basicimagebed.mapper.AlbumsMapper;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.vo.AlbumsVO;
import web.sy.basicimagebed.pojo.vo.PaginationVO;
import web.sy.basicimagebed.service.AlbumsService;
import web.sy.basicimagebed.utils.ResponseBuilder;

import java.util.List;

@Service
public class AlbumsServiceImpl implements AlbumsService {

    @Resource
    private AlbumsMapper albumsMapper;

    @Override
    public ResponseInfo<PaginationVO<AlbumsVO>> getAlbumsList(Integer page, String order, String keyword, String email) {
        if (page == null || page < 1) {
            page = 1;
        }
        int perPage = 20; // Default per page
        List<AlbumsVO> albumsList = albumsMapper.getAllAlbumsByUser(email, page, perPage, order, keyword);

        int total = albumsMapper.countAllAlbumsByUser(email);

        PaginationVO<AlbumsVO> paginationVO = new PaginationVO<>();
        paginationVO.setCurrent_page(page);
        paginationVO.setPer_page(perPage);
        paginationVO.setTotal(total);
        paginationVO.setLast_page((int) Math.ceil((double) total / perPage));
        paginationVO.setData(albumsList);
        return ResponseBuilder.success(paginationVO);
    }

    @Transactional
    @Override
    public String deleteAlbums(String email, Integer album_id) {
        if (album_id==0){
            throw new RuntimeException("默认相册不能删除");
        }
        // 不是他的相册 或者相册不存在
        AlbumsVO albumsVO = albumsMapper.getAlbumsById(email, album_id);
        if (albumsVO == null) {
            throw new RuntimeException("删除失败");
        }
        int i = albumsMapper.deleteAlbumsByEmailAndId(email, album_id.toString());
        if (i == 0) {
            throw new RuntimeException("删除失败");
        }
        int count =  albumsMapper.setAlbumsToDefaultWhileDeleteAlbum(email, album_id);


        return "已删除相册,"+count+"张图像移动到默认相册";
    }
}
