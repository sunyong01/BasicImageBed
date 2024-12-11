package web.sy.bed.base.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserToken {

    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Long userId;
    private String token;
    private String tokenName;
    private Integer tokenId;
    private Boolean enabled;
} 