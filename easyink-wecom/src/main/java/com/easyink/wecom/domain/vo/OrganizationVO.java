package com.easyink.wecom.domain.vo;

import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.page.TableDataInfo;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * 组织架构VO
 * 返回部门列表 和 员工在可见范围但部门不在可见范围的员工
 *
 * @author wx
 * 2023/3/1 11:13
 **/
@Data
public class OrganizationVO {

    /**
     * 部门列表
     */
    private List<WeDepartment> departmentList;


    /**
     * 其他员工列表 （员工在可见范围但部门不在可见范围的员工）
     * 若员工是个人权限范围也放在以下集合
     */
    private TableDataInfo<WeUser> otherUserList;

    /**
     * 全参构造函数
     *
     * @param departmentList    {@link WeDepartment} List
     * @param otherUserList     {@link WeUser} List
     */
    public OrganizationVO(List<WeDepartment> departmentList, List<WeUser> otherUserList) {
        this.departmentList = departmentList;
        this.otherUserList = getDataTable(otherUserList);
    }


    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> TableDataInfo<T> getDataTable(List<T> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(list);
        rspData.setTotal((int) new PageInfo(list).getTotal());
        return rspData;
    }


}
