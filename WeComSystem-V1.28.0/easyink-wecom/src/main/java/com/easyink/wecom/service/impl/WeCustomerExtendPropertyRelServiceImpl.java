package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.core.domain.sop.CustomerSopPropertyRel;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.dto.customersop.Column;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import com.easyink.wecom.mapper.WeCustomerExtendPropertyRelMapper;
import com.easyink.wecom.service.WeCustomerExtendPropertyRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名: 客户扩展属性关系业务接口实现类
 *
 * @author : silver_chariot
 * @date : 2021/11/10 18:05
 */
@Service
@Slf4j
public class WeCustomerExtendPropertyRelServiceImpl extends ServiceImpl<WeCustomerExtendPropertyRelMapper, WeCustomerExtendPropertyRel> implements WeCustomerExtendPropertyRelService {


    private final WeCustomerExtendPropertyRelMapper weCustomerExtendPropertyRelMapper;

    @Autowired
    public WeCustomerExtendPropertyRelServiceImpl(@NotBlank WeCustomerExtendPropertyRelMapper weCustomerExtendPropertyRelMapper) {
        this.weCustomerExtendPropertyRelMapper = weCustomerExtendPropertyRelMapper;
    }

    @Override
    public void updateBatch(WeCustomer weCustomer) {
        String corpId = weCustomer.getCorpId();
        String externalUserId = weCustomer.getExternalUserid();
        String userId = weCustomer.getUserId();
        if (StringUtils.isAnyBlank(corpId, externalUserId,userId)
                || weCustomer.getExtendProperties() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        List<Long> extendPropIdList = weCustomer.getExtendProperties().stream().map(BaseExtendPropertyRel::getExtendPropertyId).collect(Collectors.toList());
        // 清除原扩展属性关系
        LambdaQueryWrapper<WeCustomerExtendPropertyRel> condition = new LambdaQueryWrapper<WeCustomerExtendPropertyRel>()
                .eq(WeCustomerExtendPropertyRel::getCorpId, corpId)
                .eq(WeCustomerExtendPropertyRel::getUserId,weCustomer.getUserId())
                .eq(WeCustomerExtendPropertyRel::getExternalUserid, externalUserId);
        if (CollectionUtils.isNotEmpty(extendPropIdList)) {
            condition.in(WeCustomerExtendPropertyRel::getExtendPropertyId, extendPropIdList);
        }
        this.remove(condition);
        // 构建用户-扩展属性关系 实体集合
        List<WeCustomerExtendPropertyRel> list = new ArrayList<>();
        for (BaseExtendPropertyRel source : weCustomer.getExtendProperties()) {
            if (source.getPropertyValue() != null) {
                list.add(new WeCustomerExtendPropertyRel(source, corpId, externalUserId,userId));
            }
        }
        // 批量插入该客户的新扩展属性关系
        if (CollectionUtils.isNotEmpty(list)) {
            weCustomerExtendPropertyRelMapper.batchInsert(list);
        }
    }

    @Override
    public List<String> listOfPropertyIdAndValue(List<Column> columnList) {
        return weCustomerExtendPropertyRelMapper.listOfPropertyIdAndValue(columnList);
    }

    /**
     * 根据extend_property_id查询所有符合条件的客户额外字段关系
     *
     * @param columnList 字段属性值
     * @return {@link BaseExtendPropertyRel}
     */
    @Override
    public List<CustomerSopPropertyRel> selectBaseExtendValue(List<Column> columnList) {
        return weCustomerExtendPropertyRelMapper.selectBaseExtendValue(columnList);
    }

}
