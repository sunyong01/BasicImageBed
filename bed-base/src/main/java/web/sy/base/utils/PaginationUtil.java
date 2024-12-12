package web.sy.base.utils;


import web.sy.base.pojo.common.PaginationVO;

import java.util.List;

/**
 * 分页工具类
 */
public class PaginationUtil {

    /**
     * 构建分页对象
     *
     * @param data     数据列表
     * @param page     当前页码
     * @param pageSize 每页大小
     * @param total    总记录数
     * @param <T>      数据类型
     * @return 分页对象
     */
    public static <T> PaginationVO<T> buildPaginationVO(List<T> data, int page, int pageSize, int total) {
        PaginationVO<T> paginationVO = new PaginationVO<>();
        paginationVO.setCurrent_page(page);
        paginationVO.setPer_page(pageSize);
        paginationVO.setTotal(total);
        paginationVO.setLast_page((int) Math.ceil((double) total / pageSize));
        paginationVO.setData(data);
        return paginationVO;
    }

    /**
     * 计算分页的偏移量
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @return 偏移量
     */
    public static int calculateOffset(int page, int pageSize) {
        return (page - 1) * pageSize;
    }
} 