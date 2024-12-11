package web.sy.bed.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import web.sy.bed.base.pojo.entity.Role;

import java.util.Date;
import java.util.Set;


@AllArgsConstructor
@Data
public class AuthResponse {
    private String jwt;
    private Date expireTime;
    private Set<Role> role;
}
