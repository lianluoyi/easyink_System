package com.easyink.common.utils;

import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.easyink.common.core.page.TableSupport.PAGE_NUM;
import static com.easyink.common.core.page.TableSupport.PAGE_SIZE;

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
     * @param arrayList 返回的数据列表
     * @param total 数据总数
     * @return
     * @param <T>
     */
    public static <T> TableDataInfo<T> getDataTable(List<T> arrayList, Long total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        rspData.setMsg("查询成功");
        rspData.setRows(arrayList);
        rspData.setTotal(total.intValue());
        return rspData;
    }

    /**
     * 默认查询成功响应方法
     *
     * @return 结果
     * @param <T>
     */
    public static <T> TableDataInfo<T> emptyData(){
        TableDataInfo respData = new TableDataInfo();
        respData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        respData.setMsg("查询成功");
        respData.setRows(new ArrayList<>());
        respData.setTotal(0);
        return respData;
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
    public static <T> TableDataInfo<T> getDataTable(List<T> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal((int) new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 设置分页参数
     *
     * @param pageNum  页数
     * @param pageSize 页码
     */
    public static void setPage(Integer pageNum, Integer pageSize) {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(pageNum);
        pageDomain.setPageSize(pageSize);
        ServletUtils.getRequest().setAttribute(PAGE_NUM, pageNum);
        ServletUtils.getRequest().setAttribute(PAGE_SIZE, pageSize);
    }

    /**
     * 获取分页起始位置
     *
     * @return 分页起始位置
     */
    public static Integer getStartIndex() {
        Integer pageSize = getPageSize();
        Integer pageNum = getPageNum();
        if (pageNum == null || pageSize == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取分页起始位置
     *
     * @return 分页起始位置
     */
    public static Integer getStartIndex(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取分页页数
     *
     * @return 分页页数
     */
    public static Integer getPageNum() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        return pageDomain.getPageNum();
    }

    /**
     * 获取分页大小
     *
     * @return 分页大小
     */
    public static Integer getPageSize() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        return pageDomain.getPageSize();
    }

    /**
     * 预设置分页参数
     */
    public static void setPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        setPage(pageDomain.getPageNum(), pageDomain.getPageSize());
    }

    /**
     * 开始分页,从session获取分页参数
     */
    public static void startPage() {
//        PageDomain pageDomain = TableSupport.buildPageRequest();
        Object pageNum =  ServletUtils.getRequest().getAttribute(PAGE_NUM);
        Object pageSize = ServletUtils.getRequest().getAttribute(PAGE_SIZE);
       if(pageNum == null ||  pageSize == null) {
           return;
       }
        PageHelper.startPage((Integer)pageNum, (Integer)pageSize);
    }

    /**
     * 开始分页，但不使用count查询，从session获取分页参数
     */
    public static void startPageNoCount() {
        Object pageNum =  ServletUtils.getRequest().getAttribute(PAGE_NUM);
        Object pageSize = ServletUtils.getRequest().getAttribute(PAGE_SIZE);
        if(pageNum == null ||  pageSize == null) {
            return;
        }
        PageHelper.startPage((Integer)pageNum, (Integer)pageSize, false);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }
}
