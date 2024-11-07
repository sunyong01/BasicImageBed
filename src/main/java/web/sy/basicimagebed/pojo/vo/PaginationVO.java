package web.sy.basicimagebed.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class PaginationVO<T> {
    Integer current_page;
    Integer last_page;
    Integer per_page;
    Integer total;
    List<T> data;

}
