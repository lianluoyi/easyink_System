package com.easyink.common.core.controller;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.PageDomainAdvice;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author admin
 */
@Slf4j
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 手动分页方法
     * 用于处理执行逻辑中存在其他sql操作,在需要执行分页的时候才进行分页处理,调用{@link PageDataAdvice#page()}
     *
     * @return PageDataAdvice
     */
    protected PageDomain startPageManual() {
        return new PageDomainAdvice<>();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> TableDataInfo<T> getDataTable(List<T> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal((int) new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应请求分页数据
     *
     * @param list  查询数据
     * @param total 总数
     * @param <T>
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> TableDataInfo<T> getDataTable(List<T> list, Long total) {
        if (total == null) {
            return getDataTable(list);
        }
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total.intValue());
        return rspData;
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> TableDataInfo getDataTable(PageInfo<T> pageInfo) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        rspData.setMsg("查询成功");
        rspData.setRows(pageInfo.getList());
        rspData.setTotal((int) pageInfo.getTotal());
        return rspData;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }
}
