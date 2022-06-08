package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.dto.customersop.Column;
import com.easywecom.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
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
}
