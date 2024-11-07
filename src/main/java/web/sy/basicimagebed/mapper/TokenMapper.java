package web.sy.basicimagebed.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import web.sy.basicimagebed.pojo.dao.UserDAO;

@Mapper
public interface TokenMapper {

    @Select("select email from tb_user_token where token = #{token}")
    String getUserEmailByToken(String token);

    @Update("insert into tb_user_token (token, email) values (#{token}, #{email})")
    int insertToken(String token, String email);

    @Select("select * from tb_user where email = #{email} and password = #{password}")
    UserDAO getUserByEmailAndPassword(String email, String password);

    @Select("select count(*) from tb_user_token where email = #{userEmail}")
    int countUserToken(String userEmail);

    @Update("delete from tb_user_token where email = #{userEmail}")
    void clearToken(String userEmail);

    @Update("delete from tb_user_token where token = #{token}")
    void deleteToken(String token);
}
