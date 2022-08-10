package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.domain.wecom.WeDepartment;
import com.easywecom.wecom.domain.vo.sop.DepartmentVO;

import java.util.List;

/**
 * 企业微信组织架构相关Service接口
 *
 * @author admin
 * @date 2020-09-01
 */
public interface WeDepartmentService extends IService<WeDepartment> {

    /**
     * 查询企业微信组织架构相关列表
     *
     * @param corpId     公司id
     * @param isActivate 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除
     * @param loginUser
     * @return 企业微信组织架构相关集合
     */
    List<WeDepartment> selectWeDepartmentList(String corpId, Integer isActivate, LoginUser loginUser);

    /**
     * 根据用户ID获取部门名字 ,隔开
     *
     *
     * @param corpId 企业id
     * @param userId 用户id
     * @return
     */
    String selectNameByUserId(String corpId,String userId);

    /**
     * 查询企业微信组织架构详情列表
     *
     *
     * @param corpId 公司ID
     * @param isActivate 成员的激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除
     * @param loginUser
     * @return 企业微信组织架构详情列表
     */
    List<WeDepartment> selectWeDepartmentDetailList(String corpId, Integer isActivate, LoginUser loginUser);

    /**
     * 新增企业微信组织架构相关
     *
     * @param weDepartment 企业微信组织架构相关
     * @return true新增成功 false 新增失败
     */
    Boolean insertWeDepartment(WeDepartment weDepartment);

    int insertWeDepartmentNoToWeCom(WeDepartment weDepartment);

    /**
     * 修改企业微信组织架构相关
     *
     * @param weDepartment 企业微信组织架构相关
     * @return 结果
     */
    void updateWeDepartment(WeDepartment weDepartment);


    /**
     * 同步部门
     *
     * @param corpId 公司ID
     * @param userKey 用户redisKey
     * @return 同步部门集合
     */
    List<WeDepartment> synchWeDepartment(String corpId, String userKey);

    /**
     * 同步部门 不刷新可见部门权限
     *
     * @param corpId 公司ID
     * @return 同步部门集合
     */
    List<WeDepartment> synchWeDepartment(String corpId);


    /**
     * 根据部门id删除部门
     *
     * @param corpId 公司ID
     * @param ids
     */
    void deleteWeDepartmentByIds(String corpId, String[] ids);

    /**
     * 获取部门详情
     *
     * @param corp_id
     * @param departmentIdList
     * @return
     */
    List<DepartmentVO> getDeparmentDetailByIds(String corp_id, List<String> departmentIdList);
}
