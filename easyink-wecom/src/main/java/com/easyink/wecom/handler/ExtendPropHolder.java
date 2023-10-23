package com.easyink.wecom.handler;

import cn.hutool.core.collection.ListUtil;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.service.ExtendPropertyMultipleOptionService;
import com.easyink.wecom.service.WeCustomerExtendPropertyService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类名: 扩展字段属性容器
 *
 * @author : silver_chariot
 * @date : 2023/9/22 11:25
 **/
public class ExtendPropHolder {
    private Map<Long, WeCustomerExtendProperty> extendPropMap;
    private Map<Long, ExtendPropertyMultipleOption> optionMap;
    /**
     * 判断需要导出的字段里是否有扩展字段
     */
    private boolean hasExtendProp;

    public ExtendPropHolder(String corpId,  List<String> selectProperties) {
        init(corpId , selectProperties);
    }

    /**
     * 初始化
     *
     * @param corpId 企业ID
     */
    public void init(String corpId ,List<String> selectedProperties) {
        if(CollectionUtils.isEmpty(selectedProperties)) {
            return;
        }
        WeCustomerExtendPropertyService wecustomerExtendPropertyService = SpringUtils.getBean(WeCustomerExtendPropertyService.class);
        ExtendPropertyMultipleOptionService extendPropertyMultipleOptionService = SpringUtils.getBean(ExtendPropertyMultipleOptionService.class) ;
        // 过滤系统默认字段
        List<String> propList = selectedProperties.stream().filter(a -> !ListUtil.toList(UserConstants.getSysDefaultProperties()).contains(a)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(propList) ) {
            hasExtendProp = false ;
            return;
        }else {
            hasExtendProp =true ;
        }
        // 查询该企业所有的扩展属性详情
        List<WeCustomerExtendProperty> extendPropList = wecustomerExtendPropertyService.getList(
                WeCustomerExtendProperty.builder()
                                        .corpId(corpId)
                                        .build()
        );
        if (CollectionUtils.isEmpty(extendPropList)) {
            return;
        }
        // 扩展属性ID->详情的映射
        extendPropMap = extendPropList.stream()
                                      .collect(Collectors.toMap(WeCustomerExtendProperty::getId, prop -> prop));
        // 多选值ID-> 多选值的映射
        optionMap = extendPropertyMultipleOptionService.getMapByProp(extendPropList);
    }

    public Map<Long, WeCustomerExtendProperty> getExtendPropMap() {
        return extendPropMap;
    }

    public Map<Long, ExtendPropertyMultipleOption> getOptionMap() {
        return optionMap;
    }

    public boolean isHasExtendProp() {
        return hasExtendProp;
    }
}
