package web.sy.bed.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import web.sy.bed.base.pojo.entity.UserToken;

import java.util.List;

@Mapper
public interface UserTokenMapper {
    
    // 插入新token
    int insert(UserToken userToken);
    
    // 根据ID获取token
    UserToken getById(@Param("id") Long id);
    
    // 根据用户ID获取所有token
    List<UserToken> getByUserId(@Param("userId") Long userId);
    
    // 根据token获取记录
    UserToken getByToken(@Param("token") String token);
    
    // 更新token状态
    int updateEnabled(@Param("id") Long id, @Param("enabled") Boolean enabled);
    
    // 删除token
    int deleteById(@Param("id") Long id);
    
    // 删除用户的所有token
    int deleteByUserId(@Param("userId") Long userId);
    
    // 获取用户下一个可用的tokenId
    Integer getNextTokenId(@Param("userId") Long userId);
    
    /**
     * 根据token ID和token值查找token记录
     * @param tokenId Token ID
     * @param token Token值
     * @return UserToken对象,如果未找到返回null
     */
    UserToken findByIdAndToken(@Param("tokenId") Long tokenId, @Param("token") String token);
} 