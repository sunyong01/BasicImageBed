package web.sy.basicimagebed.service;

import org.springframework.web.multipart.MultipartFile;
import web.sy.basicimagebed.pojo.common.ResponseInfo;
import web.sy.basicimagebed.pojo.vo.ImageDataVO;
import web.sy.basicimagebed.pojo.vo.PaginationVO;


import java.io.IOException;

public interface ImageService {
    String downloadImage(String year, String month, String day, String imageName);
    String deleteImage(String  userEmail ,String key);
    ResponseInfo<ImageDataVO> uploadImage(MultipartFile file) throws IOException;
    ResponseInfo<ImageDataVO> uploadImage(MultipartFile file,String email) throws IOException;
    ResponseInfo<ImageDataVO> uploadImage(MultipartFile file,String email,int strategy) throws IOException;
    ResponseInfo<PaginationVO<ImageDataVO>> getImageList(String email, Integer page, Integer perPage, String order, String permission, Integer album_id, String keyword);
    void deleteImagesByUserEmail(String email);
}
