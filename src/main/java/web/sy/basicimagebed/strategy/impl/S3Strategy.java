package web.sy.basicimagebed.strategy.impl;

import web.sy.basicimagebed.strategy.StorageStrategy;

import java.util.List;

public class S3Strategy extends BaseStrategy{

    @Override
    boolean deleteImage(String pathname, String localBase) {
        //TODO 从S3 删除图片
        return super.deleteImage(pathname, localBase);
    }

    @Override
    boolean getImageLocal(String pathname, String localBase) {
        //TODO 从S3下载图片到本地
        return super.getImageLocal(pathname, localBase);
    }

    @Override
    boolean uploadImage(String pathname, String localBase) {
        // TODO 上传图片到S3
        return super.uploadImage(pathname, localBase);
    }
}
