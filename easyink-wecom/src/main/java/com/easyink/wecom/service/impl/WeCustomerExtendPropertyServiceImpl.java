package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.model.customer.CustomerExtendedProperties;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSetting;
import com.easyink.wecom.service.*;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.encrypt.StrategyCryptoUtil;
import com.easyink.common.enums.CustomerExtendPropertyEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.dto.BatchSaveCustomerExtendPropertyDTO;
import com.easyink.wecom.domain.dto.SaveCustomerExtendPropertyDTO;
import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushSysData;
import com.easyink.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import com.easyink.wecom.domain.model.customer.AddressModel;
import com.easyink.wecom.domain.model.customer.CustomerExtendTypeValueModel;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.domain.vo.WeCustomerExportVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.handler.ExtendPropHolder;
import com.easyink.wecom.mapper.WeCustomerExtendPropertyMapper;
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
    private final WeCustomerTempEmpleCodeSettingService customerTempEmpleCodeSettingService;
    private final WeFormSendRecordService weFormSendRecordService;
    /**
     * 文件类型的自定义属性url的字段名
     */
    private static final String FILE_URL = "url";

    @Autowired
    @Lazy
    public WeCustomerExtendPropertyServiceImpl(@NotNull WeCustomerExtendPropertyMapper weCustomerExtendPropertyMapper, @NotNull ExtendPropertyMultipleOptionService extendPropertyMultipleOptionService, @NotNull WeCustomerExtendPropertyRelService weCustomerExtendPropertyRelService, @NotNull WeCustomerTempEmpleCodeSettingService customerTempEmpleCodeSettingService,
                                               WeFormSendRecordService weFormSendRecordService) {
        this.weCustomerExtendPropertyMapper = weCustomerExtendPropertyMapper;
        this.extendPropertyMultipleOptionService = extendPropertyMultipleOptionService;
        this.weCustomerExtendPropertyRelService = weCustomerExtendPropertyRelService;
        this.customerTempEmpleCodeSettingService = customerTempEmpleCodeSettingService;
        this.weFormSendRecordService = weFormSendRecordService;
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

    /**
     * 合并预设字段和客户字段，预设数据优先
     *
     * @param presetFields 预设表的扩展字段
     * @param customerFields 客户表的扩展字段
     * @return 合并后的扩展字段列表
     */
    private List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> mergeExtendFields(
            List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> presetFields,
            List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> customerFields) {

        List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> result = new ArrayList<>();

        // 记录预设表中已有的字段ID，用于去重
        Set<String> presetFieldIds = new HashSet<>();

        // 1. 先添加预设表的字段
        if (CollectionUtils.isNotEmpty(presetFields)) {
            for (ThirdPartyPushSysData.CustomerExtendPropertyInfo presetField : presetFields) {
                result.add(presetField);
                presetFieldIds.add(String.valueOf(presetField.getPropId()));
            }
        }

        // 2. 添加客户表中预设表没有的字段
        if (CollectionUtils.isNotEmpty(customerFields)) {
            for (ThirdPartyPushSysData.CustomerExtendPropertyInfo customerField : customerFields) {
                // 如果预设表中没有这个字段，才添加
                if (!presetFieldIds.contains(String.valueOf(customerField.getPropId()))) {
                    result.add(customerField);
                }
            }
        }

        log.info("[表单提交推送处理] 字段合并完成 presetCount={}, customerCount={}, mergedCount={}",
                presetFields.size(), customerFields.size(), result.size());

        return result;
    }

    @Override
    public void validate(WeCustomerExtendProperty property) {
        if (property == null ||
                StringUtils.isAnyBlank(property.getCorpId(), property.getName())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 不允许与系统默认字段重复名字
        if (ListUtil.toList(UserConstants.getSysDefaultProperties()).contains(property.getName())) {
            throw new CustomException(ResultTip.TIP_IS_SYS_PROP_NAME);
        }
        if (!isUnique(property)) {
            throw new CustomException(ResultTip.TIP_EXTEND_PROP_NAME_EXISTED);
        }
        // 自定义选项属性值是否重复
        if (isOptionRepeat(property.getOptionList())) {
            throw new CustomException(ResultTip.TIP_OPTION_REPEAT);
        }
    }

    /**
     * 自定义选项属性值是否重复
     *
     * @param optionList {@link ExtendPropertyMultipleOption}
     * @return 重复 true; 不重复 false
     */
    private Boolean isOptionRepeat(List<ExtendPropertyMultipleOption> optionList) {
        if (CollectionUtils.isEmpty(optionList)) {
            return false;
        }
        Set<String> names = new HashSet<>();
        for (ExtendPropertyMultipleOption extendPropertyMultipleOption : optionList) {
            if (!names.add(extendPropertyMultipleOption.getMultipleValue())) {
                return true;
            }
        }
        return false;
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
        WeCustomerExtendProperty remark = new WeCustomerExtendProperty(corpId, "描述", type, sort++, createBy);
        WeCustomerExtendProperty label = new WeCustomerExtendProperty(corpId, "客户标签", type, sort, createBy);
        // 存入集合
        List<WeCustomerExtendProperty> list = new ArrayList<>();
        list.add(birthday);
        list.add(tel);
        list.add(email);
        list.add(address);
        list.add(remark);
        list.add(label);
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
        for (SaveCustomerExtendPropertyDTO property : dto.getProperties()) {
            // 自定义选项属性值是否重复
            if (isOptionRepeat(property.getOptionList())) {
                throw new CustomException(ResultTip.TIP_OPTION_REPEAT);
            }
        }
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
    public void setKeyValueMapper(String corpId, List<WeCustomerExportVO> customerList, List<String> selectedProperties, ExtendPropHolder extendPropHolder) {
        if (CollectionUtils.isEmpty(customerList) || CollectionUtils.isEmpty(selectedProperties)) {
            return;
        }
        if(extendPropHolder==null || !extendPropHolder.isHasExtendProp()) {
            return;
        }
        List<String> propList = selectedProperties.stream().filter(a -> !ListUtil.toList(UserConstants.getSysDefaultProperties()).contains(a)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(propList)) {
            return;
        }
        for (WeCustomerExportVO vo : customerList) {
            List<BaseExtendPropertyRel> extendProperties = vo.getExtendProperties();
            Map<String, CustomerExtendTypeValueModel> customerMap = this.mapPropertyName2Value(extendProperties, extendPropHolder.getExtendPropMap(), extendPropHolder.getOptionMap());
            LinkedHashMap<String, CustomerExtendTypeValueModel> resultMap = new LinkedHashMap<>();
            // 按选中的字段顺序设置值,如果没有则设为空字符串
            for (String selectedProp : propList) {
                resultMap.put(selectedProp, customerMap.getOrDefault(selectedProp, new CustomerExtendTypeValueModel()));
            }
            // 为导出客户实体设置 k:导出字段名 v:导出字段值 的映射
            vo.setExtendPropMapper(resultMap);
        }
    }

    @Override
    public Map<String, CustomerExtendTypeValueModel> mapPropertyName2Value(List<BaseExtendPropertyRel> extendProperties, Map<Long, WeCustomerExtendProperty> extendPropMap, Map<Long, ExtendPropertyMultipleOption> optionMap) {
        Map<String, CustomerExtendTypeValueModel> customerMap = new HashMap<>(16);
        for (BaseExtendPropertyRel rel : extendProperties) {
            if (extendPropMap.containsKey(rel.getExtendPropertyId())) {
                WeCustomerExtendProperty prop = extendPropMap.get(rel.getExtendPropertyId());
                // 多选字段和图片需要拼接
                if (CustomerExtendPropertyEnum.isMultiple(prop.getType())) {
                    // 如果多选id有效则从映射中获取值
                    Long propertyValue = Long.valueOf(rel.getPropertyValue());
                    if (optionMap.containsKey(propertyValue)) {
                        String value = optionMap.get(propertyValue).getMultipleValue();

                        CustomerExtendTypeValueModel customerExtendTypeValueModel = customerMap.get(prop.getName());
                        if(customerExtendTypeValueModel == null){
                            customerMap.put(prop.getName(), new CustomerExtendTypeValueModel(prop.getType(), value));
                            continue;
                        }
                        String preValue = customerExtendTypeValueModel.getPropValue();
                        if (StringUtils.isNotBlank(preValue)) {
                            // 如果存在多选选项则拼接并用,隔开
                            value = new StringBuilder(preValue)
                                    .append(",")
                                    .append(value).toString();
                        }
                        customerMap.put(prop.getName(), new CustomerExtendTypeValueModel(prop.getType(), value));
                    }
                    // 文件也需要拼接 但是需要对json进行解析处理
                } else if (CustomerExtendPropertyEnum.isFileOrPic(prop.getType())) {
                    putOrAppendFileUrlIntoMap(prop, rel, customerMap);
                }else if(CustomerExtendPropertyEnum.LOCATION.getType().equals(prop.getType())){
                    // 位置信息的内容拼接处理
                    AddressModel addressModel = JSONObject.parseObject(rel.getPropertyValue(), AddressModel.class);
                    customerMap.put(prop.getName(), addressModel == null ? new CustomerExtendTypeValueModel(prop.getType(), "") :new CustomerExtendTypeValueModel(prop.getType(), addressModel.transferToExportString()) );
                } else {
                    // 单选自定义字段直接取值
                    customerMap.put(prop.getName(), new CustomerExtendTypeValueModel(prop.getType(), rel.getPropertyValue()));
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
    private void putOrAppendFileUrlIntoMap(WeCustomerExtendProperty prop, BaseExtendPropertyRel rel, Map<String, CustomerExtendTypeValueModel> map) {
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
        CustomerExtendTypeValueModel customerExtendTypeValueModel = map.get(prop.getName());
        if(customerExtendTypeValueModel == null){
            map.put(prop.getName(), new CustomerExtendTypeValueModel(prop.getType(), newValue));
            return;
        }
        String oldValue = customerExtendTypeValueModel.getPropValue();
        if (StringUtils.isNotBlank(oldValue)) {
            // 不同文件,图片逗号隔开
            newValue = oldValue + "，" + newValue;
        }
        map.put(prop.getName(), new CustomerExtendTypeValueModel(prop.getType(), newValue));
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

    @Override
    public void setExtendPropertyForCustomers(String corpId, List<WeCustomerVO> list) {
        if(StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(list)) {
            return;
        }
        List<BaseExtendPropertyRel> extendPropertyRels = weCustomerExtendPropertyMapper.getPropByCustomer(corpId, list);
        // 解密位置类型的propValue
        for (BaseExtendPropertyRel extendPropertyRel : extendPropertyRels) {
            if(CustomerExtendPropertyEnum.LOCATION.getType().equals(extendPropertyRel.getPropertyType())) {
                AddressModel addressModel = JSON.parseObject(extendPropertyRel.getPropertyValue(), AddressModel.class);
                addressModel.setDetailAddress(StrategyCryptoUtil.decrypt(addressModel.getDetailAddressEncrypt()));
                extendPropertyRel.setPropertyValue(JSON.toJSONString(addressModel));
            }
        }
        if(CollectionUtils.isEmpty(extendPropertyRels)) {
            return;
        }
        for ( WeCustomerVO vo : list) {
            for ( BaseExtendPropertyRel propRel : extendPropertyRels) {
                if(ObjectUtil.equal(vo.getUserId(), propRel.getUserId()) && ObjectUtil.equal(vo.getExternalUserid(),propRel.getExternalUserid())) {
                    vo.getExtendProperties().add(propRel);
                }
            }
        }
    }

    @Override
    public List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> selectCustomerExternalPropertyMappingById(List<Integer> propIdList, CustomerId customerId, Long formId) {
        if(CollectionUtils.isEmpty(propIdList)){
            return new ArrayList<>();
        }

        // 1. 先从预设表获取最新的客户扩展字段数据
        List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> presetFields = getPresetExtendFields(customerId, propIdList, formId);

        // 2. 从客户表查询现有的扩展字段数据
        List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> customerFields = getCustomerExtendFields(propIdList, customerId);

        // 3. 合并数据，预设数据优先
        return mergeExtendFields(presetFields, customerFields);
    }

    /**
     * 从预设表获取客户扩展字段数据
     *
     * @param customerId 客户ID
     * @param propIdList 属性ID列表
     * @param state 预设表查询的state参数
     * @return 预设的扩展字段列表
     */
    private List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> getPresetExtendFields(CustomerId customerId, List<Integer> propIdList, Long formId) {
        List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> presetFields = new ArrayList<>();

        // 为空则不查
        if(formId == null){
            return new ArrayList<>();
        }

        // 根据表单id, 查询发送表单记录, 获取state
        String state = weFormSendRecordService.getStateByFormId(customerId, formId);

        if (StringUtils.isBlank(state)) {
            return new ArrayList<>();
        }
        log.info("[表单提交推送处理] 表单查询到的预设表state值为: {} customerId={}, formId: {}", state, customerId, formId);
        try {
            // 使用state查询预设表，参考WeEmpleCodeServiceImpl.customerTempEmpleCodeCallBackHandle的查询方式
            WeCustomerTempEmpleCodeSetting presetSetting = customerTempEmpleCodeSettingService.getByState(state, customerId.getCorpId());
            if (presetSetting != null && StringUtils.isNotBlank(presetSetting.getCustomerExtendInfo())) {
                try {
                    // 解析预设表中的扩展字段信息
                    List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> allPresetFields = JSON.parseArray(presetSetting.getCustomerExtendInfo(), CustomerExtendedProperties.class).stream().map(it -> {
                        ThirdPartyPushSysData.CustomerExtendPropertyInfo customerExtendPropertyInfo = new ThirdPartyPushSysData.CustomerExtendPropertyInfo();
                        customerExtendPropertyInfo.setPropId(it.getId());
                        customerExtendPropertyInfo.setPropType(it.getType());
                        customerExtendPropertyInfo.setPropName(it.getName());
                        customerExtendPropertyInfo.setPropValue(it.getValue());
                        return customerExtendPropertyInfo;
                    }).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(allPresetFields)) {
                        // 过滤出需要的属性ID
                        presetFields = allPresetFields.stream()
                                .filter(field -> propIdList.contains(field.getPropId().intValue()))
                                .collect(Collectors.toList());
                        log.info("[表单提交推送处理] 从预设表获取扩展字段成功 customerId={}, state={}, presetFieldCount={}", customerId, state, presetFields.size());
                    }
                } catch (Exception e) {
                    log.warn("[表单提交推送处理] 解析预设表扩展字段失败 customerId={}, state={}, error={}", customerId, state, ExceptionUtils.getStackTrace(e));
                }
            } else {
                log.info("[表单提交推送处理] 预设表中未找到对应数据 customerId={}, state={}", customerId, state);
            }
        } catch (Exception e) {
            log.warn("[表单提交推送处理] 查询预设表扩展字段失败 customerId={}, state={}, error={}", customerId, state, ExceptionUtils.getStackTrace(e));
        }

        return presetFields;
    }

    /**
     * 从客户表获取扩展字段数据
     *
     * @param propIdList 属性ID列表
     * @param customerId 客户ID
     * @return 客户表的扩展字段列表
     */
    private List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> getCustomerExtendFields(List<Integer> propIdList, CustomerId customerId) {
        // 根据客户自定义属性id, 查询设置得自定义属性值
        List<BaseExtendPropertyRel> list = weCustomerExtendPropertyRelService.list(new LambdaQueryWrapper<>(WeCustomerExtendPropertyRel.class)
                .in(WeCustomerExtendPropertyRel::getExtendPropertyId, propIdList)
                .eq(WeCustomerExtendPropertyRel::getUserId, customerId.getUserId())
                .eq(WeCustomerExtendPropertyRel::getExternalUserid, customerId.getExternalUserid())
        ).stream().map(it -> {
            BaseExtendPropertyRel baseExtendPropertyRel = new BaseExtendPropertyRel();
            baseExtendPropertyRel.setExtendPropertyId(it.getExtendPropertyId());
            baseExtendPropertyRel.setPropertyValue(it.getPropertyValue());
            return baseExtendPropertyRel;
        }).collect(Collectors.toList());
        // 转换为map, k: 属性实体 v: 属性值
        Map<WeCustomerExtendProperty, String> prop2valueMap = mapProperty2Value(list, customerId.getCorpId());


        // 映射成属性Info实体
        Set<Map.Entry<WeCustomerExtendProperty, String>> entries = prop2valueMap.entrySet();
            return entries.stream().map(entry -> {
                WeCustomerExtendProperty key = entry.getKey();
                String value = entry.getValue();


                return ThirdPartyPushSysData.CustomerExtendPropertyInfo.builder()
                        .propId(key.getId())
                        .propType(key.getType())
                        .propName(key.getName())
                        .propValue(CustomerExtendPropertyEnum.LOCATION.getType().equals(key.getType())? obtainDecryptAddress(value):value)
                        .build();
            }).collect(Collectors.toList());
    }

    /**
     * 获取解密的地址address
     * @param value 位置类型自定义属性value
     * @return
     */
    private String obtainDecryptAddress(String value) {
        AddressModel addressModel = JSON.parseObject(value, AddressModel.class);
        SensitiveFieldProcessor.decrypt(addressModel);
        String detail = addressModel.transferToTrajectoryString();
        return detail;
    }


}
