package web.sy.basicimagebed.strategy.impl;

import web.sy.basicimagebed.pojo.vo.ImageDataVO;
import web.sy.basicimagebed.strategy.StorageStrategy;

import java.io.File;

// Base策略 同时为本地存储策略的实现
public class BaseStrategy implements StorageStrategy {

    // 从本地上传图片到策略对应的云存储
    boolean uploadImage(String pathname, String localBase) {
        // 因为是本地存储策略 所以不需要上传
        return true;
    }
    // 从云存储下载图片到本地
    boolean getImageLocal(String pathname, String localBase) {
        // 因为是本地存储策略 所以不需要下载
        return true;
    }
    // 从云存储删除图片
    boolean deleteImage(String pathname, String localBase) {
        // 因为是本地存储策略 所以不需要删除
        return true;
    }

    boolean localExist(String pathname, String localBase) {
        String full_path = localBase + pathname;
        File file = new File(full_path);
        return file.exists();
    }

    boolean deleteLocal(String pathname, String localBase) {
        String full_path = localBase + pathname;
        File file = new File(full_path);
        return file.delete();
    }

    @Override
    public boolean getImage(ImageDataVO imageDataVO,String localBase) {
        if (imageDataVO.getPathname() == null)
            throw new RuntimeException("Image path is null");
        //如果本地存在返回本地的
        if (this.localExist(imageDataVO.getPathname(),localBase))
            return true;
        boolean download_ok = this.getImageLocal(imageDataVO.getPathname(),localBase);
        if (!download_ok)
            throw new RuntimeException("Download image from Strategy failed");
        return true;
    }

    @Override
    public boolean uploadImage(ImageDataVO imageDataVO,String localBase) {
        if (imageDataVO.getPathname() == null)
            throw new RuntimeException("Image path is null");
        if (!this.localExist(imageDataVO.getPathname(),localBase))
            throw new RuntimeException("Image not exist in local");
        boolean upload_ok = this.uploadImage(imageDataVO.getPathname(),localBase);
        if (!upload_ok)
            throw new RuntimeException("Upload image to Strategy failed");
        return true;
    }

    @Override
    public boolean deleteImage(ImageDataVO imageDataVO,String localBase) {
        if (imageDataVO.getPathname() == null)
            throw new RuntimeException("Image path is null");
        boolean delete_ok = this.deleteImage(imageDataVO.getPathname(),localBase);
        if (!delete_ok)
            throw new RuntimeException("Delete image from Strategy failed");
        if (this.localExist(imageDataVO.getPathname(),localBase))
            this.deleteLocal(imageDataVO.getPathname(),localBase);
        return true;
    }

}
