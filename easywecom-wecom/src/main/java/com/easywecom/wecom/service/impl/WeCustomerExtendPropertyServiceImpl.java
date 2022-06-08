package com.easywecom.wecom.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.Constants;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.constant.UserConstants;
import com.easywecom.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easywecom.common.enums.CustomerExtendPropertyEnum;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.dto.BatchSaveCustomerExtendPropertyDTO;
import com.easywecom.wecom.domain.dto.SaveCustomerExtendPropertyDTO;
import com.easywecom.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import com.easywecom.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easywecom.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import com.easywecom.wecom.domain.vo.WeCustomerExportVO;
import com.easywecom.wecom.mapper.WeCustomerExtendPropertyMapper;
import com.easywecom.wecom.service.ExtendPropertyMultipleOptionService;
import com.easywecom.wecom.service.WeCustomerExtendPropertyRelService;
import com.easywecom.wecom.service.WeCustomerExtendPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名: 客户扩展属性业务接口实现类
 *
 * @author : silver_chariot
 * @date : 2021/11/10 18:05
 */
@Service
@Slf4j
public class WeCustomerExtendPropertyServiceImpl extends ServiceImpl<WeCustomerExtendPropertyMapper, WeCustomerExtendProperty> implements WeCustomerExtendPropertyService {

    private final WeCustomerExtendPropertyMapper weCustomerExtendPropertyMapper;
    private final ExtendPropertyMultipleOptionService extendPropertyMultipleOptionService;
    private final WeCustomerExtendPropertyRelService weCustomerExtendPropertyRelService;
    /**
     * 文件类型的自定义属性url的字段名
     */
    private static final String FILE_URL = "url";

    @Autowired
    @Lazy
    public WeCustomerExtendPropertyServiceImpl(@NotNull WeCustomerExtendPropertyMapper weCustomerExtendPropertyMapper, @NotNull ExtendPropertyMultipleOptionService extendPropertyMultipleOptionService, @NotNull WeCustomerExtendPropertyRelService weCustomerExtendPropertyRelService) {
        this.weCustomerExtendPropertyMapper = weCustomerExtendPropertyMapper;
        this.extendPropertyMultipleOptionService = extendPropertyMultipleOptionService;
        this.weCustomerExtendPropertyRelService = weCustomerExtendPropertyRelService;
    }

    @Override
    public Boolean isUnique(WeCustomerExtendProperty property) {
        if (StringUtils.isAnyBlank(property.getCorpId(), property.getName())) {
            return false;
        }
        WeCustomerExtendProperty localProp = getOne(new LambdaQueryWrapper<WeCustomerExtendProperty>()
                .eq(WeCustomerExtendProperty::getCorpId, property.getCorpId())
                .eq(WeCustomerExtendProperty::getName, property.getName())
                .last(GenConstants.LIMIT_1)
        );
        // 数据库中是否存在
        boolean isNotExist = localProp == null;
        // 存在的是否是当前需要修改/插入的自定义属性
        boolean isSelf = property.getId() != null && property.getId().equals(localProp.getId());
        return isNotExist || isSelf;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(SaveCustomerExtendPropertyDTO dto) {
        WeCustomerExtendProperty property = new WeCustomerExtendProperty();
        BeanUtils.copyPropertiesASM(dto, property);
        return this.add(property);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(WeCustomerExtendProperty property) {
        // 对扩展字段进行校验
        this.validate(property);
        //保存自定义字段属性
        Integer result = weCustomerExtendPropertyMapper.insertOrUpdate(property);
        //保存多选框属性
        extendPropertyMultipleOptionService.edit(property);
        return result;
    }

    @Override
    public void validate(WeCustomerExtendProperty property) {
        if (property == null ||
                StringUtils.isAnyBlank(property.getCorpId(), property.getName())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 不允许与系统默认字段重复名字
        if (ListUtil.toList(UserConstants.SYS_DEFAULT_PROPERTIES).contains(property.getName())) {
            throw new CustomException(ResultTip.TIP_IS_SYS_PROP_NAME);
        }
        if (!isUnique(property)) {
            throw new CustomException(ResultTip.TIP_EXTEND_PROP_NAME_EXISTED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(String corpId, String[] ids) {
        if (StringUtils.isBlank(corpId) || ArrayUtils.isEmpty(ids)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 删除客户自定义字段属性
        weCustomerExtendPropertyMapper.delete(
                new LambdaQueryWrapper<WeCustomerExtendProperty>()
                        .eq(WeCustomerExtendProperty::getCorpId, corpId)
                        .in(WeCustomerExtendProperty::getId, ids)
                        .ne(WeCustomerExtendProperty::getType, CustomerExtendPropertyEnum.SYS_DEFAULT.getType())
        );
        // 删除对应多选框的多选值
        extendPropertyMultipleOptionService.remove(
                new LambdaQueryWrapper<ExtendPropertyMultipleOption>()
                        .in(ExtendPropertyMultipleOption::getExtendPropertyId, ids)
        );
        // 删除相关属性所有关联的客户
        weCustomerExtendPropertyRelService.remove(
                new LambdaQueryWrapper<WeCustomerExtendPropertyRel>()
                        .eq(WeCustomerExtendPropertyRel::getCorpId, corpId)
                        .in(WeCustomerExtendPropertyRel::getExtendPropertyId, ids)
        );
    }

    @Override
    public List<WeCustomerExtendProperty> getList(WeCustomerExtendProperty property) {
        if (property == null || StringUtils.isBlank(property.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 查询客户自定义字段列表
        return weCustomerExtendPropertyMapper.getList(property);

    }

    @Override
    public Map<Long, WeCustomerExtendProperty> getMap(WeCustomerExtendProperty property) {
        List<WeCustomerExtendProperty> list = getList(property);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(WeCustomerExtendProperty::getId, prop -> prop));
    }

    @Override
    public Boolean initSysProperty(String corpId, String createBy) {
        Integer type = CustomerExtendPropertyEnum.SYS_DEFAULT.getType();
        if (StringUtils.isBlank(createBy)) {
            createBy = Constants.SUPER_ADMIN;
        }
        // 创建系统默认字段实例
        int sort = 1;
        WeCustomerExtendProperty birthday = new WeCustomerExtendProperty(corpId, "出生日期", type, sort++, createBy);
        WeCustomerExtendProperty tel = new WeCustomerExtendProperty(corpId, "电话", type, sort++, createBy);
        WeCustomerExtendProperty email = new WeCustomerExtendProperty(corpId, "邮箱", type, sort++, createBy);
        WeCustomerExtendProperty address = new WeCustomerExtendProperty(corpId, "地址", type, sort++, createBy);
        WeCustomerExtendProperty remark = new WeCustomerExtendProperty(corpId, "描述", type, sort, createBy);
        // 存入集合
        List<WeCustomerExtendProperty> list = new ArrayList<>();
        list.add(birthday);
        list.add(tel);
        list.add(email);
        list.add(address);
        list.add(remark);
        // 批量插入
        try {
            weCustomerExtendPropertyMapper.batchInsert(list);
        } catch (Exception e) {
            log.info("初始化系统属性批量插入异常,corpId:{},e:{}", corpId, ExceptionUtils.getStackTrace(e));
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SaveCustomerExtendPropertyDTO dto) {
        WeCustomerExtendProperty property = new WeCustomerExtendProperty();
        BeanUtils.copyPropertiesASM(dto, property);
        this.edit(property);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(WeCustomerExtendProperty property) {
        // 对待编辑的扩展属性进行校验
        this.validate(property);
        // 编辑多选值
        extendPropertyMultipleOptionService.edit(property);
        // 不允许修改类型
        property.setType(null);
        // 修改自定义字段属性
        Integer result = weCustomerExtendPropertyMapper.updateById(property);
        if (result == 0) {
            throw new CustomException(ResultTip.TIP_UPDATE_EXTEND_PROP_FAIL);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editBatch(BatchSaveCustomerExtendPropertyDTO dto) {
        String corpId = dto.getCorpId();
        List<SaveCustomerExtendPropertyDTO> editList = dto.getProperties();
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(editList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        List<WeCustomerExtendProperty> list = new ArrayList<>();
        // 转换成 WeCustomerExtendProperty 集合
        for (SaveCustomerExtendPropertyDTO source : editList) {
            WeCustomerExtendProperty property = new WeCustomerExtendProperty();
            BeanUtils.copyPropertiesASM(source, property);
            property.setCorpId(corpId);
            list.add(property);
        }
        this.editBatch(list, corpId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editBatch(List<WeCustomerExtendProperty> list, String corpId) {
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 编辑多选框的值
        for (WeCustomerExtendProperty property : list) {
            extendPropertyMultipleOptionService.edit(property);
            // 类型不允许修改
            property.setType(null);
        }
        Boolean res;
        try {
            res = saveOrUpdateBatch(list);
        } catch (DuplicateKeyException e) {
            log.warn("批量更新客户自定义属性失败：重复键异常,corpId:{},e:{}", corpId, ExceptionUtils.getStackTrace(e));
            throw new CustomException(ResultTip.TIP_EXTEND_PROP_NAME_EXISTED);
        }
        if (!res) {
            throw new CustomException(ResultTip.TIP_UPDATE_EXTEND_PROP_FAIL);
        }
    }

    @Override
    public void setKeyValueMapper(String corpId, List<WeCustomerExportVO> customerList, List<String> selectedProperties) {
        if (CollectionUtils.isEmpty(customerList) || CollectionUtils.isEmpty(selectedProperties)) {
            return;
        }
        // 过滤系统默认字段
        selectedProperties = selectedProperties.stream().filter(a -> !ListUtil.toList(UserConstants.SYS_DEFAULT_PROPERTIES).contains(a)).collect(Collectors.toList());
        // 查询该企业所有的扩展属性详情
        List<WeCustomerExtendProperty> extendPropList = this.getList(
                WeCustomerExtendProperty.builder()
                        .corpId(corpId)
                        .build()
        );
        if (CollectionUtils.isEmpty(extendPropList)) {
            return;
        }
        // 扩展属性ID->详情的映射
        Map<Long, WeCustomerExtendProperty> extendPropMap = extendPropList.stream().collect(Collectors.toMap(WeCustomerExtendProperty::getId, prop -> prop));
        // 多选值ID-> 多选值的映射
        Map<Long, ExtendPropertyMultipleOption> optionMap = extendPropertyMultipleOptionService.getMapByProp(extendPropList);
        for (WeCustomerExportVO vo : customerList) {
            List<BaseExtendPropertyRel> extendProperties = vo.getExtendProperties();
            Map<String, String> customerMap = this.mapPropertyName2Value(extendProperties, extendPropMap, optionMap);
            LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();
            // 按选中的字段顺序设置值,如果没有则设为空字符串
            for (String selectedProp : selectedProperties) {
                resultMap.put(selectedProp, customerMap.getOrDefault(selectedProp, StringUtils.EMPTY));
            }
            // 为导出客户实体设置 k:导出字段名 v:导出字段值 的映射
            vo.setExtendPropMapper(resultMap);
        }
    }

    @Override
    public Map<String, String> mapPropertyName2Value(List<BaseExtendPropertyRel> extendProperties, Map<Long, WeCustomerExtendProperty> extendPropMap, Map<Long, ExtendPropertyMultipleOption> optionMap) {
        Map<String, String> customerMap = new HashMap<>(16);
        for (BaseExtendPropertyRel rel : extendProperties) {
            if (extendPropMap.containsKey(rel.getExtendPropertyId())) {
                WeCustomerExtendProperty prop = extendPropMap.get(rel.getExtendPropertyId());
                // 多选字段和图片需要拼接
                if (CustomerExtendPropertyEnum.isMultiple(prop.getType())) {
                    // 如果多选id有效则从映射中获取值
                    Long propertyValue = Long.valueOf(rel.getPropertyValue());
                    if (optionMap.containsKey(propertyValue)) {
                        String preValue = customerMap.get(prop.getName());
                        String value = optionMap.get(propertyValue).getMultipleValue();
                        if (StringUtils.isNotBlank(preValue)) {
                            // 如果存在多选选项则拼接并用,隔开
                            value = new StringBuilder(preValue)
                                    .append(",")
                                    .append(value).toString();
                        }
                        customerMap.put(prop.getName(), value);
                    }
                    // 文件也需要拼接 但是需要对json进行解析处理
                } else if (CustomerExtendPropertyEnum.isFileOrPic(prop.getType())) {
                    putOrAppendFileUrlIntoMap(prop, rel, customerMap);
                } else {
                    // 单选自定义字段直接取值
                    customerMap.put(prop.getName(), rel.getPropertyValue());
                }
            }
        }
        return customerMap;
    }

    /**
     * 根据扩展属性和客户的属性拼接 文件/图片url
     *
     * @param prop {@link WeCustomerExtendProperty}
     * @param rel  {@link BaseExtendPropertyRel}
     * @param map  属性名-> 属性值的映射
     * @return str of url
     */
    private void putOrAppendFileUrlIntoMap(WeCustomerExtendProperty prop, BaseExtendPropertyRel rel, Map<String, String> map) {
        if (prop == null || rel == null || map == null || prop.getType() == null) {
            return;
        }
        String newValue = null;
        if (CustomerExtendPropertyEnum.FILE.getType().equals(prop.getType())) {
            // 文件需要解析JSON 后拼接URL
            JSONObject target = JSONObject.parseObject(rel.getPropertyValue());
            newValue = target == null ? "" : (String) target.get(FILE_URL);
        } else if (CustomerExtendPropertyEnum.PIC.getType().equals(prop.getType())) {
            // 图片拼接URL
            newValue = rel.getPropertyValue();
        }
        if (StringUtils.isBlank(newValue)) {
            return;
        }
        String oldValue = map.get(prop.getName());
        if (StringUtils.isNotBlank(oldValue)) {
            // 不同文件,图片逗号隔开
            newValue = oldValue + "，" + newValue;
        }
        map.put(prop.getName(), newValue);
    }


    @Override
    public Map<WeCustomerExtendProperty, String> mapProperty2Value(List<BaseExtendPropertyRel> extendProperties, Map<Long, WeCustomerExtendProperty> extendPropMap, Map<Long, ExtendPropertyMultipleOption> optionMap) {
        Map<WeCustomerExtendProperty, String> customerMap = new HashMap<>(16);
        for (BaseExtendPropertyRel rel : extendProperties) {
            if (extendPropMap.containsKey(rel.getExtendPropertyId())) {
                WeCustomerExtendProperty prop = extendPropMap.get(rel.getExtendPropertyId());
                if (CustomerExtendPropertyEnum.isMultiple(prop.getType())) {
                    // 如果多选id有效则从映射中获取值
                    Long propertyValue = Long.valueOf(rel.getPropertyValue());
                    if (optionMap.containsKey(propertyValue)) {
                        String preValue = customerMap.get(prop);
                        String value = optionMap.get(propertyValue).getMultipleValue();
                        if (StringUtils.isNotBlank(preValue)) {
                            // 如果存在多选选项则拼接并用,隔开
                            value = new StringBuilder(preValue)
                                    .append(",")
                                    .append(value).toString();
                        }
                        customerMap.put(prop, value == null ? StringUtils.EMPTY : value);
                    }
                } else {
                    // 单选自定义字段直接取值
                    customerMap.put(prop, rel.getPropertyValue() == null ? StringUtils.EMPTY : rel.getPropertyValue());
                }
            }
        }
        return customerMap;
    }


    @Override
    public Map<WeCustomerExtendProperty, String> mapProperty2Value(List<BaseExtendPropertyRel> extendProperties, String corpId) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(extendProperties)) {
            return Collections.emptyMap();
        }
        List<WeCustomerExtendProperty> extendPropList = this.getList(
                WeCustomerExtendProperty.builder()
                        .corpId(corpId)
                        .build()
        );
        if (CollectionUtils.isEmpty(extendPropList)) {
            return Collections.emptyMap();
        }
        // 扩展属性ID->详情的映射
        Map<Long, WeCustomerExtendProperty> extendPropMap = extendPropList.stream().collect(Collectors.toMap(WeCustomerExtendProperty::getId, prop -> prop));
        // 多选值ID-> 多选值的映射
        Map<Long, ExtendPropertyMultipleOption> optionMap = extendPropertyMultipleOptionService.getMapByProp(extendPropList);
        return mapProperty2Value(extendProperties, extendPropMap, optionMap);
    }


}
