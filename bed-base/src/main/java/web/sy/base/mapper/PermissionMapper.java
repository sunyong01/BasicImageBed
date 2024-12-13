package web.sy.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import web.sy.base.pojo.entity.Permission;

import java.util.Set;

@Mapper
public interface PermissionMapper {
    @Select("SELECT p.* FROM tb_permission p " +
            "INNER JOIN tb_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN tb_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    Set<Permission> findPermissionsByUserId(Long userId);
} 