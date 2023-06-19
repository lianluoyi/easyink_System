package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: 客户扩展属性持久层映射
 *
 * @author : silver_chariot
 * @date : 2021/11/10 18:07
 */
@Repository
public interface WeCustomerExtendPropertyMapper extends BaseMapper<WeCustomerExtendProperty> {
    /**
     * 保存或修改 客户自定义扩展属性
     *
     * @param property {@link WeCustomerExtendProperty }
     * @return updated num
     */
    Integer insertOrUpdate(WeCustomerExtendProperty property);

    /**
     * 客户扩展属性列表
     *
     * @param property {@link WeCustomerExtendProperty }
     * @return result list
     */
    List<WeCustomerExtendProperty> getList(WeCustomerExtendProperty property);

    /**
     * 批量插入
     *
     * @param list 属性集合
     * @return updated num
     */
    Integer batchInsert(@Param("list") List<WeCustomerExtendProperty> list);

    /**
     * 批量插入更新
     *
     * @param list {@link  List<WeCustomerExtendProperty>}
     * @return updated num
     */
    Integer insertOrUpdateBatch(@Param("list") List<WeCustomerExtendProperty> list);

    /**
     * 根据客户 获取对应的扩展属性
     *
     * @param corpId 企业id
     * @param list   客户列表
     * @return {@link BaseExtendPropertyRel} list
     */
    List<BaseExtendPropertyRel> getPropByCustomer(@Param("corpId") String corpId, @Param("list") List<WeCustomerVO> list);
}
