package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.common.core.domain.wecom.WeDepartment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业微信组织架构相关Mapper接口
 *
 * @author admin
 * @date 2020-09-01
 */
@Repository
public interface WeDepartmentMapper extends BaseMapper<WeDepartment> {
    /**
     * 查询企业微信组织架构相关
     *
     * @param corpId 公司 ID
     * @param id     企业微信组织架构相关ID
     * @return 企业微信组织架构相关
     */
    WeDepartment selectWeDepartmentById(@Param("corpId") String corpId, @Param("id") Long id);

    /**
     * 查询企业微信组织架构相关列表
     *
     * @return 企业微信组织架构相关集合
     * @param corpId 公司iD
     */
    List<WeDepartment> selectWeDepartmentList(@Param("corpId") String corpId);

    /**
     * 根据企业ID查询其下的部门id列表
     *
     * @param corpId 企业ID
     * @return 部门ID列表
     */
    List<WeDepartment> selectDepartmentByCorpId(String corpId);

    /**
     * 新增企业微信组织架构相关
     *
     * @param weDepartment 企业微信组织架构相关
     * @return 结果
     */
    int insertWeDepartment(WeDepartment weDepartment);

    /**
     * 修改企业微信组织架构相关
     *
     * @param weDepartment 企业微信组织架构相关
     * @return 结果
     */
    int updateWeDepartment(WeDepartment weDepartment);


    /**
     * 删除部门表所有数据
     *
     * @return
     */
    int deleteAllWeDepartment();


    /**
     * 批量保存
     *
     * @param weDepartments
     * @return
     */
    int batchInsertWeDepartment(@Param("weDepartments") List<WeDepartment> weDepartments);

    /**
     * 根据用户ID获取部门名字 ,隔开
     *
     *
     * @param corpId 企业id
     * @param userId 用户id
     * @return
     */
    String selectNameByUserId(@Param("corpId") String corpId, @Param("userId") String userId);

    /**
     * 根据部门ID获取 部门和其下级部门的所有人数
     *
     * @param corpId     公司ID
     * @param ids        部门ID ,隔开
     * @param isActivate 成员的激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除
     * @return 部门和下级部门总人数
     */
    Integer selectTotalUserCount(@Param("corpId") String corpId, @Param("ids") String ids, @Param("isActivate") Integer isActivate);

    /**
     * 查询部门和其所有下级部门
     *
     * @param department 部门详情
     * @return 部门和其下级部门ID
     */
    String selectDepartmentAndChild(WeDepartment department);
}
