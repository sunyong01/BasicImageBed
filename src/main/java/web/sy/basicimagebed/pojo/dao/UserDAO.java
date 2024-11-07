package web.sy.basicimagebed.pojo.dao;

import lombok.Data;
import web.sy.basicimagebed.pojo.vo.ProfileVO;

@Data
public class UserDAO {
    private long id;
    private String email;
    private String password;
    private String nickname;
    private String url;
    private String avatar;
    private String registerIp;
    private double capacity;
    private double capacityUsed;
    private int role;

    public ProfileVO toProfileVO() {
        ProfileVO profileVO = new ProfileVO();
        profileVO.setName(this.nickname);
        profileVO.setAvatar(this.avatar);
        profileVO.setEmail(this.email);
        profileVO.setCapacity((float) this.capacity);
        profileVO.setUsedCapacity((float) this.capacityUsed);
        profileVO.setUrl(this.url);
        profileVO.setImageNum(0);
        profileVO.setAlbumNum(0);
        profileVO.setRegisteredIp(this.registerIp);
        return profileVO;
    }
}
