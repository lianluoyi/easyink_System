package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.CustomerExtendPropertyEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.mapper.ExtendPropertyMultipleOptionMapper;
import com.easyink.wecom.service.ExtendPropertyMultipleOptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类名: 客户扩展属性多选值业务接口实现类
 *
 * @author : silver_chariot
 * @date : 2021/11/12 13:42
 */
@Service
@Slf4j
public class ExtendPropertyMultipleOptionServiceImpl extends ServiceImpl<ExtendPropertyMultipleOptionMapper, ExtendPropertyMultipleOption> implements ExtendPropertyMultipleOptionService {

    private final ExtendPropertyMultipleOptionMapper extendPropertyMultipleOptionMapper;

    public ExtendPropertyMultipleOptionServiceImpl(ExtendPropertyMultipleOptionMapper extendPropertyMultipleOptionMapper) {
        this.extendPropertyMultipleOptionMapper = extendPropertyMultipleOptionMapper;
    }

    @Override
    public void edit(List<ExtendPropertyMultipleOption> list, Long propId) {
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_MULTIPLE_OPTION_MISSING);
        }
        // 获取原来的所有额外属性字段列表
        List<ExtendPropertyMultipleOption> originList = list(new LambdaQueryWrapper<ExtendPropertyMultipleOption>().eq(ExtendPropertyMultipleOption::getExtendPropertyId, propId));
        // 去除当前编辑后还存在的字段属性
        originList.removeAll(list);
        // 获取要删除的id
        List<Long> removeIdList = originList.stream().map(ExtendPropertyMultipleOption::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(removeIdList)) {
            // 不为空，则删除
            removeByIds(removeIdList);
        }
        for (int i = 0; i < list.size(); i++) {
            // 按照列表的顺序，给属性值排序
            list.get(i).setOptionSort(i + 1);
        }
        // 批量插入或更新
        extendPropertyMultipleOptionMapper.batchSaveOrUpdate(list);
    }

    @Override
    public void edit(WeCustomerExtendProperty property) {
        if (property == null) {
            return;
        }
        if (!CustomerExtendPropertyEnum.isMultiple(property.getType())) {
            // 如果自定义属不是多选类型,删除数据库里该自定义属性的所有多选选项值
            this.remove(new LambdaQueryWrapper<ExtendPropertyMultipleOption>()
                    .eq(ExtendPropertyMultipleOption::getExtendPropertyId, property.getId()));
            return;
        }
        // 校验参数
        Long propId = property.getId();
        if (CollectionUtils.isNotEmpty(property.getOptionList())) {
            // 校验自定义字段里的多选值列表的propId是否正确
            for (ExtendPropertyMultipleOption option : property.getOptionList()) {
                if (option.getId() == null && propId != null) {
                    option.setExtendPropertyId(propId);
                }else if (propId == null || !propId.equals(option.getExtendPropertyId())) {
                    log.warn("自定义参数:编辑多选值的自定义参数id与自定义参数实体id不一致");
                    throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
                }
            }
        }
        this.edit(property.getOptionList(), propId);
    }

    @Override
    public Map<Long, ExtendPropertyMultipleOption> getMapByProp(List<WeCustomerExtendProperty> extendPropList) {
        if (CollectionUtils.isEmpty(extendPropList)) {
            return Collections.emptyMap();
        }
        List<Long> idList = extendPropList.stream().map(WeCustomerExtendProperty::getId).collect(Collectors.toList());
        return getMapByPropId(idList);
    }

    @Override
    public Map<Long, ExtendPropertyMultipleOption> getMapByPropId(List<Long> extendPropIdList) {
        if (CollectionUtils.isEmpty(extendPropIdList)) {
            return Collections.emptyMap();
        }
        List<ExtendPropertyMultipleOption> list = list(new LambdaQueryWrapper<ExtendPropertyMultipleOption>()
                .in(ExtendPropertyMultipleOption::getExtendPropertyId, extendPropIdList)
        );
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(ExtendPropertyMultipleOption::getId, option -> option));

    }

}
