package web.sy.basicimagebed.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import web.sy.basicimagebed.pojo.dao.UserDAO;

@Mapper
public interface UserMapper {

    boolean createUser(UserDAO userDAO);

    boolean deleteUser(String email);

    int updateUser(UserDAO userDAO);

    int updatePassword(String username,String password);

    int updateCapacityUsed(String username,double capacityUsed);

    UserDAO getUserByEmail(String email);

}
