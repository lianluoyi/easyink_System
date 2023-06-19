package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.core.domain.sop.CustomerSopPropertyRel;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.wecom.domain.dto.customersop.Column;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: 客户-扩展字段关系表数据库映射
 *
 * @author : silver_chariot
 * @date : 2021/11/15 18:22
 */
@Repository
public interface WeCustomerExtendPropertyRelMapper extends BaseMapper<WeCustomerExtendPropertyRel> {
    /**
     * 批量 修改/保存
     *
     * @param list {@link List<WeCustomerExtendPropertyRel>}
     * @return affected rows
     */
    Integer batchInsert(@Param("list") List<WeCustomerExtendPropertyRel> list);

    /**
     * 自定义字段转接
     *
     * @param corpId         企业id
     * @param externalUserId 外部联系人id
     * @param handoverUserId 原跟进人id
     * @param takeoverUserId 接替人id
     */
    void transferProp(@Param("corpId") String corpId, @Param("externalUserid") String externalUserId,
                      @Param("handoverUserid") String handoverUserId, @Param("takeoverUserid") String takeoverUserId);

    /**
     * 查询客户关系
     *
     * @param columnList 字段属性值
     * @return {@link List<WeCustomerExtendPropertyRel>}
     */
    List<String> listOfPropertyIdAndValue(@Param("list") List<Column> columnList);

    /**
     * 根据extend_property_id, userId查询所有符合条件的客户额外字段关系
     *
     * @param columnList 字段属性值
     * @param userIds 员工ID
     * @return {@link BaseExtendPropertyRel}
     */
    List<CustomerSopPropertyRel> selectBaseExtendValue(@Param("columnList") List<Column> columnList, @Param("userIds") String userIds);

    /**
     * 根据extend_property_id查询客户所属的除日期范围选择外的所有额外字段值
     *
     * @param extendPropertyIds extend_property_id 列表
     * @param userIds userIds 员工ID，以","分隔
     * @return 结果
     */
    List<CustomerSopPropertyRel> selectExtendGroupByCustomer(@Param("extendPropertyIds") List<String> extendPropertyIds, @Param("userIds") String userIds);
}
