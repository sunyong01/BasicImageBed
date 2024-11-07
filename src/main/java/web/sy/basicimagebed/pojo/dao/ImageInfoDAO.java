package web.sy.basicimagebed.pojo.dao;

import java.util.Date;

import lombok.Data;
import web.sy.basicimagebed.pojo.vo.ImageDataVO;
import web.sy.basicimagebed.utils.TimeUtils;


@Data
public class ImageInfoDAO {
    private String key;
    private String name;
    private String originName;
    private String pathname;
    private float size;
    private int width;
    private int height;
    private String md5;
    private String sha1;
    private Date date;
    private int strategy;
    private boolean isPublic;
    private String uploadUser;
    private int albumId;

    public void setByImageDataVO(ImageDataVO imageDataVO){
        this.key = imageDataVO.getKey();
        this.name = imageDataVO.getName();
        this.originName = imageDataVO.getOrigin_name();
        this.pathname = imageDataVO.getPathname();
        this.size = imageDataVO.getSize();
        this.width = imageDataVO.getWidth();
        this.height = imageDataVO.getHeight();
        this.md5 = imageDataVO.getMd5();
        this.sha1 = imageDataVO.getSha1();
        this.date = imageDataVO.getDate();
    }

    public ImageDataVO toImageDataVO(){
        ImageDataVO imageDataVO = new ImageDataVO();
        imageDataVO.setKey(this.key);
        imageDataVO.setName(this.name);
        imageDataVO.setOrigin_name(this.originName);
        imageDataVO.setPathname(this.pathname);
        imageDataVO.setSize(this.size);
        imageDataVO.setWidth(this.width);
        imageDataVO.setHeight(this.height);
        imageDataVO.setMd5(this.md5);
        imageDataVO.setSha1(this.sha1);
        imageDataVO.setDate(this.date);
        imageDataVO.setHuman_date(TimeUtils.dateTimeToHumanDate(this.date));
        return imageDataVO;
    }

}