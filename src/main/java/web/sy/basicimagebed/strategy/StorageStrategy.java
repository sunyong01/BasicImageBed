package web.sy.basicimagebed.strategy;


import web.sy.basicimagebed.pojo.vo.ImageDataVO;

import java.util.HashMap;

// 所有的策略都是为了保证 图片在本地路径上可以被读到
public interface StorageStrategy {

    boolean getImage(ImageDataVO imageDataVO,String localBase);

    boolean uploadImage(ImageDataVO imageDataVO,String localBase);

    boolean deleteImage(ImageDataVO imageDataVO,String localBase);
}
