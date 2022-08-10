package com.easywecom.wecom.mapper;

import com.easywecom.wecom.domain.WeCustomerTagRel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户标签关系Mapper接口
 *
 * @author admin
 * @date 2020-09-13
 */
@Repository
public interface WeCustomerTagRelMapper {
    /**
     * 查询客户标签关系
     *
     * @param id 客户标签关系ID
     * @return 客户标签关系
     */
    WeCustomerTagRel selectWeCustomerTagRelById(Long id);

    /**
     * 查询客户标签关系列表
     *
     * @param weCustomerTagRel 客户标签关系
     * @return 客户标签关系集合
     */
    List<WeCustomerTagRel> selectWeCustomerTagRelList(WeCustomerTagRel weCustomerTagRel);

    /**
     * 新增客户标签关系
     *
     * @param weCustomerTagRel 客户标签关系
     * @return 结果
     */
    int insertWeCustomerTagRel(WeCustomerTagRel weCustomerTagRel);

    /**
     * 修改客户标签关系
     *
     * @param weCustomerTagRel 客户标签关系
     * @return 结果
     */
    int updateWeCustomerTagRel(WeCustomerTagRel weCustomerTagRel);

    /**
     * 删除客户标签关系
     *
     * @param id 客户标签关系ID
     * @return 结果
     */
    int deleteWeCustomerTagRelById(Long id);

    /**
     * 批量删除客户标签关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeCustomerTagRelByIds(Long[] ids);
}
