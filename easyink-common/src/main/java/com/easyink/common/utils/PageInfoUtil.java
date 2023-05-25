package com.easyink.common.utils;

import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.ResultTip;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.LinkedList;
import java.util.List;

/**
 * 无法通过sql直接进行分页，需要对列表直接分页时 使用此工具类
 *
 * @author : zhaorui
 * @version : v1.0
 * @createTime : 2023/5/6 14:27
 */
public class PageInfoUtil {

    /**
     * 默认页面大小
     */
    private static final Integer DEFAULT_PAGE_SIZE=10;

    /**
     * 进行手动分页
     *
     * @param arrayList 待分页数据
     * @param pageNum 页数
     * @param pageSize 页面大小
     * @return 分页完成后的数据
     */
    public static <T> PageInfo<T> list2PageInfo(List<T> arrayList, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return new PageInfo<>(arrayList);
        }
        PageHelper.startPage(pageNum, pageSize);
        int pageStart = pageNum == 1 ? 0 : (pageNum - 1) * pageSize;
        int pageEnd = arrayList.size() < pageSize * pageNum ? arrayList.size() : pageSize * pageNum;
        List<T> pageResult = new LinkedList<T>();
        if (arrayList.size() > pageStart) {
            pageResult = arrayList.subList(pageStart, pageEnd);
        }
        PageInfo<T> pageInfo = new PageInfo<>(pageResult);
        //获取PageInfo其他参数
        pageInfo.setTotal(arrayList.size());
        int endRow = pageInfo.getEndRow() == 0 ? 0 : (int) ((pageNum - 1) * pageSize + pageInfo.getEndRow() + 1);
        pageInfo.setEndRow(endRow);
        boolean hasNextPage = arrayList.size() > pageSize * pageNum;
        pageInfo.setHasNextPage(hasNextPage);
        boolean hasPreviousPage = pageNum == 1 ? false : true;
        pageInfo.setHasPreviousPage(hasPreviousPage);
        pageInfo.setIsFirstPage(!hasPreviousPage);
        boolean isLastPage = arrayList.size() > pageSize * (pageNum - 1) && arrayList.size() <= pageSize * pageNum;
        pageInfo.setIsLastPage(isLastPage);
        int pages = arrayList.size() % pageSize == 0 ? arrayList.size() / pageSize : (arrayList.size() / pageSize) + 1;
        pageInfo.setNavigateLastPage(pages);
        int[] navigatePageNums = new int[pages];
        for (int i = 1; i < pages; i++) {
            navigatePageNums[i - 1] = i;
        }
        pageInfo.setNavigatepageNums(navigatePageNums);
        int nextPage = pageNum < pages ? pageNum + 1 : 0;
        pageInfo.setNextPage(nextPage);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages(pages);
        pageInfo.setPrePage(pageNum - 1);
        pageInfo.setSize(pageInfo.getList().size());
        int starRow = arrayList.size() < pageSize * pageNum ? 1 + pageSize * (pageNum - 1) : 0;
        pageInfo.setStartRow(starRow);
        PageHelper.clearPage();
        return pageInfo;
    }

    /**
     * 封装成前端所需分页数据
     *
     * @param arrayList 待分页数据
     * @param pageNum 页数
     * @return 封装后分页数据
     */
    public static  <T> TableDataInfo<T> getDataTable(List<T> arrayList, Integer pageNum) {
        return getDataTable(arrayList,pageNum,DEFAULT_PAGE_SIZE);
    }

    /**
     * 封装成前端所需分页数据
     *
     * @param arrayList 待分页数据
     * @param pageNum 页数
     * @param pageSize 页面大小
     * @return 封装后分页数据
     */
    public static  <T> TableDataInfo<T> getDataTable(List<T> arrayList, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return PageInfoUtil.getDataTable(arrayList);
        }
        PageInfo pageInfo=list2PageInfo(arrayList,pageNum,pageSize);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        rspData.setMsg("查询成功");
        rspData.setRows(pageInfo.getList());
        rspData.setTotal((int) pageInfo.getTotal());
        return rspData;
    }

    /**
     * 返回所有数据
     *
     * @param list 数据
     * @return 封装后的数据
     */
    public static  <T> TableDataInfo<T> getDataTable(List<T> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal((int) new PageInfo(list).getTotal());
        return rspData;
    }
}
