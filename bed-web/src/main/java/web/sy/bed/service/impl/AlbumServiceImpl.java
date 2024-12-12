package web.sy.bed.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sy.base.exception.AlbumException;
import web.sy.base.mapper.AlbumMapper;
import web.sy.base.pojo.common.PaginationVO;
import web.sy.base.pojo.common.ResponseInfo;
import web.sy.base.pojo.dto.AlbumDTO;
import web.sy.base.pojo.entity.Album;
import web.sy.base.pojo.entity.ImageInfo;
import web.sy.base.utils.PaginationUtil;
import web.sy.base.utils.ResponseBuilder;
import web.sy.bed.service.AlbumService;
import web.sy.bed.service.ImageService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final ImageService imageService;

    @Override
    public ResponseInfo<PaginationVO<Album>> getAlbumList(String username, Integer page, Integer pageSize,
            String keyword) {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<Album> albums = albumMapper.getAlbumList(username, offset, pageSize, keyword);
        int total = albumMapper.countAlbums(username, keyword);

        return ResponseBuilder.success(PaginationUtil.buildPaginationVO(albums, page, pageSize, total));
    }

    @Override
    @Transactional
    public ResponseInfo<Album> createAlbum(AlbumDTO albumDTO) {
        // 检查相册名是否已存在
        if (albumMapper.checkAlbumNameExists(albumDTO.getUsername(), albumDTO.getName()) > 0) {
            throw new AlbumException("相册名已存在");
        }

        Album album = convertToAlbum(albumDTO);
        albumMapper.insertAlbum(album);
        return ResponseBuilder.success(album);
    }

    @Override
    @Transactional
    public ResponseInfo<Album> updateAlbum(Long id, AlbumDTO albumDTO) {
        Album existingAlbum = getAlbumAndCheckPermission(id, albumDTO.getUsername());

        // 如果修改了相册名，检查新名称是否已存在
        if (!existingAlbum.getName().equals(albumDTO.getName()) &&
                albumMapper.checkAlbumNameExists(albumDTO.getUsername(), albumDTO.getName()) > 0) {
            throw new AlbumException("相册名已存在");
        }

        updateAlbumFromDTO(existingAlbum, albumDTO);
        albumMapper.updateAlbum(existingAlbum);
        return ResponseBuilder.success(existingAlbum);
    }

    @Override
    @Transactional
    public ResponseInfo<String> deleteAlbum(Long id, String username) {
        Album album = getAlbumAndCheckPermission(id, username);

        // 检查相册是否为空
        if (album.getImageCount() > 0) {
            throw new AlbumException("相册不为空，无法删除");
        }

        albumMapper.deleteAlbum(id);
        return ResponseBuilder.success("相册删除成功");
    }

    @Override
    public ResponseInfo<String> linkImageToAlbum(Long albumId, String key, String username) {
        // 验证图片是否存在且属于当前用户
        ImageInfo imageInfo = validateImage(key, username);

        // 验证相册是否存在且属于当前用户
        if (albumId != null) {
            Album album = getAlbumAndCheckPermission(albumId, username);
            imageInfo.setAlbumId(albumId.intValue());
        } else {
            imageInfo.setAlbumId(null);
        }

        imageService.updateImage(imageInfo);
        return ResponseBuilder.success("关联成功");
    }

    @Override
    public ResponseInfo<Album> getAlbumById(Long id, String username) {
        Album album = albumMapper.getAlbumById(id);
        if (album == null) {
            return ResponseBuilder.error("相册不存在");
        }

        // 检查权限：只能查看自己的相册或公开的相册
        if (!album.getIsPublic() && !album.getUsername().equals(username)) {
            return ResponseBuilder.error("无权限访问此相册");
        }

        return ResponseBuilder.success(album);
    }

    @Override
    public ResponseInfo<PaginationVO<Album>> searchAlbums(Album condition, Integer page, Integer pageSize,
            String order) {
        int offset = PaginationUtil.calculateOffset(page, pageSize);

        List<Album> albumList = albumMapper.searchAlbums(condition, offset, pageSize, order);

        // 获取每个相册的图片数量
        for (Album album : albumList) {
            int imageCount = imageService.countImagesByAlbumId(album.getId());
            album.setImageCount(imageCount);
        }

        int total = albumMapper.countSearchAlbums(condition);

        return ResponseBuilder.success(PaginationUtil.buildPaginationVO(albumList, page, pageSize, total));
    }

    private Album getAlbumAndCheckPermission(Long id, String username) {
        Album album = albumMapper.getAlbumById(id);
        if (album == null) {
            throw new AlbumException("相册不存在");
        }
        if (!album.getUsername().equals(username)) {
            throw new AlbumException("无权操作此相册");
        }
        return album;
    }

    private ImageInfo validateImage(String key, String username) {
        ImageInfo imageInfo = imageService.getImageByKey(key);
        if (imageInfo == null || !imageInfo.getUploadUser().equals(username)) {
            throw new AlbumException("图片不存在或无权限");
        }
        return imageInfo;
    }

    private Album convertToAlbum(AlbumDTO albumDTO) {
        Album album = new Album();
        album.setName(albumDTO.getName());
        album.setDescription(albumDTO.getDescription());
        album.setCoverUrl(albumDTO.getCoverUrl());
        album.setIsPublic(albumDTO.getIsPublic());
        album.setUsername(albumDTO.getUsername());
        return album;
    }

    private void updateAlbumFromDTO(Album album, AlbumDTO albumDTO) {
        album.setName(albumDTO.getName());
        album.setDescription(albumDTO.getDescription());
        album.setCoverUrl(albumDTO.getCoverUrl());
        album.setIsPublic(albumDTO.getIsPublic());
    }
}