package web.sy.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import web.sy.base.pojo.entity.Role;

import java.util.Set;

@Mapper
public interface RoleMapper {
    @Select("SELECT * FROM tb_role WHERE id IN (SELECT role_id FROM tb_user_role WHERE user_id = #{userId})")
    Set<Role> findRolesByUserId(Long userId);
} 