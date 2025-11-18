package com.easyink.wecom.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.wecom.config.ThirdPushFieldMappingConfig;
import com.easyink.wecom.constant.ThirdPushFieldConstants;
import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushRequest;
import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushSysData;
import com.easyink.wecom.domain.enums.form.FormItemPushTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Objects;

/**
 * 第三方推送数据转换工具类
 *
 * @author easyink
 */
@Slf4j
@Component
public class ThirdPartyPushDataConverter {

    private static final SimpleDateFormat FID_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    @Autowired
    private ThirdPushFieldMappingConfig fieldMappingConfig;

    /**
     * 将ThirdPartyPushSysData转换为第三方推送请求格式
     *
     * @param pushSysData 推送系统数据
     * @return 第三方推送请求
     */
    public ThirdPartyPushRequest convert(ThirdPartyPushSysData pushSysData) {
        if (pushSysData == null) {
            log.warn("[推送表单] 数据转换 pushSysData为空");
            return null;
        }

        try {
            ThirdPartyPushRequest.ThirdPartyPushRequestBuilder builder = ThirdPartyPushRequest.builder();

            Map<String, Object> content = new HashMap<>();

            content.put(fieldMappingConfig.getInteractiveMode(), fieldMappingConfig.getInteractiveModeValue());
            content.put(fieldMappingConfig.getProject(), fieldMappingConfig.getProjectValue());


            // 动态字段
            content.put(fieldMappingConfig.getFid(), generateFID());

            // 构建Fields字段映射
            Map<String, String> fields = new HashMap<>();
            // 填充客户信息字段（CustomerField2-5）
            fillCustomerFields(fields, pushSysData.getCustomerInfo());
            // 填充表单内容字段（CustomerField6-8）- 只在表单提交时填充
            fillFormContentFields(fields, pushSysData.getFormInfo());

            content.put(fieldMappingConfig.getFields() , fields);

            builder.content(content);

            ThirdPartyPushRequest build = builder.build();
            boolean  mustParam = build.invalidMustParam(content, fieldMappingConfig);
            build.setFastError(mustParam);
            return build;

        } catch (Exception e) {
            log.error("[推送表单] 转换ThirdPartyPushSysData异常, e {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 填充客户信息字段（CustomerField2-5）
     * 根据客户扩展字段中的propName进行匹配填充
     *
     * @param fields       字段映射
     * @param customerInfo 客户信息
     */
    private void fillCustomerFields(Map<String, String> fields, ThirdPartyPushSysData.CustomerInfoDTO customerInfo) {
        if (customerInfo == null || fieldMappingConfig == null) {
            return;
        }

        // 设置客户手机号 - 使用配置的字段名
        fields.put(fieldMappingConfig.getPhone(), customerInfo.getFirstPhone());


        // 设置客户昵称 - 使用配置的字段名
        String name = customerInfo.getName();
        if (StringUtils.isNotBlank(name)) {
            fields.put(fieldMappingConfig.getNickName(), name);
        }

        // 处理客户扩展字段
        List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> extendFields = customerInfo.getExtendFields();
        if (CollectionUtils.isNotEmpty(extendFields)) {
            for (ThirdPartyPushSysData.CustomerExtendPropertyInfo extendField : extendFields) {
                if (extendField == null) {
                    continue;
                }
                String propName = extendField.getPropName();
                String propValue = extendField.getPropValue();
                if (StringUtils.isBlank(propName) || StringUtils.isBlank(propValue)) {
                    continue;
                }

                // 根据propName匹配到对应的CustomerField字段
                String fieldName = mapPropNameToField(propName);
                if (StringUtils.isNotBlank(fieldName)) {
                    fields.put(fieldName, propValue);
                }
            }
        }
    }

    /**
     * 填充表单内容字段（CustomerField6-8）
     * 只在表单提交推送时填充，超时推送时无需传递
     *
     * @param fields   字段映射
     * @param formInfo 表单信息
     */
    private void fillFormContentFields(Map<String, String> fields, ThirdPartyPushSysData.FormInfoDTO formInfo) {
        if (formInfo == null || StringUtils.isBlank(formInfo.getFormSubmitResult())) {
            return;
        }

        try {
            // 解析表单提交结果JSON
            List<JSONObject> formResults = JSON.parseArray(formInfo.getFormSubmitResult(), JSONObject.class);
            if (CollectionUtils.isEmpty(formResults)) {
                return;
            }
            // 遍历表单字段，根据pushType判断填充到对应字段
            boolean hashPushType = isPushType(formResults);
            if (!hashPushType) {
                // 没有pushType的表单, 不填充表单信息
                return;
            }

            // 是否满意 - 使用配置的字段名
            formResults.stream()
                .filter(it -> it != null && FormItemPushTypeEnum.IS_SATISFIED.getCode().equals(it.getInteger("pushType")))
                .findFirst()
                .ifPresent(it -> {
                    String answer = it.getString("answer");
                    Integer code = null;
                    if (StringUtils.isNotBlank(answer)) {
                        if ("非常满意".equals(answer)) {
                            code = 1;
                        } else if ("满意".equals(answer)) {
                            code = 2;
                        } else if ("不满意".equals(answer)) {
                            code = 3;
                        }
                    }
                    if (code != null) {
                        String satisfactionField = fieldMappingConfig.getSatisfaction();
                        if (StringUtils.isNotBlank(satisfactionField)) {
                            fields.put(satisfactionField, String.valueOf(code));
                        }
                    }
                });

            // 服务不满意内容 - 使用配置的字段名
            formResults.stream()
                .filter(it -> it != null && FormItemPushTypeEnum.DISSATISFACTION_REASON.getCode().equals(it.getInteger("pushType")))
                .findFirst()
                .ifPresent(it -> {
                    String answer = it.getString("answer");
                    String dissatisfactionField = fieldMappingConfig.getDissatisfactionContent();
                    if (StringUtils.isNotBlank(answer) && StringUtils.isNotBlank(dissatisfactionField)) {
                        fields.put(dissatisfactionField, StringUtils.substring(answer, 0, ThirdPushFieldConstants.MAX_ANSWER_LENGTH));
                    }
                });
            // 服务建议内容 - 使用配置的字段名
            formResults.stream()
                .filter(it -> it != null && FormItemPushTypeEnum.SERVICE_SUGGESTION.getCode().equals(it.getInteger("pushType")))
                .findFirst()
                .ifPresent(it -> {
                    String answer = it.getString("answer");
                    String suggestionField = fieldMappingConfig.getServiceSuggestion();
                    if (StringUtils.isNotBlank(answer) && StringUtils.isNotBlank(suggestionField)) {
                        fields.put(suggestionField, StringUtils.substring(answer, 0, ThirdPushFieldConstants.MAX_ANSWER_LENGTH));
                    }
                });

        } catch (Exception e) {
            log.error("[数据转换] 解析表单提交结果异常 : {}", ExceptionUtils.getStackTrace(e));
        }
    }

    private static boolean isPushType(List<JSONObject> formResults) {
        return formResults.stream()
            .filter(Objects::nonNull)
            .anyMatch(it -> {
                Integer pushType = it.getInteger("pushType");
                Optional<FormItemPushTypeEnum> byCode = FormItemPushTypeEnum.getByCode(pushType);
                return byCode.isPresent();
            });
    }


    /**
     * 将属性名映射到对应的字段名
     * CustomerField3 -> CustomerField5是客户信息字段
     *
     * @param propName 属性名
     * @return 字段名
     */
    private String mapPropNameToField(String propName) {
        if (fieldMappingConfig == null || StringUtils.isBlank(propName)) {
            return null;
        }

        switch (propName) {
            case "安装地址":
                return fieldMappingConfig.getInstallAddress(); // 安装地址 - 使用配置的字段名
            case "所属外包单位":
                return fieldMappingConfig.getOutsourcingUnit(); // 所属外包单位 - 使用配置的字段名
            case "上门员工号":
                return fieldMappingConfig.getEmployeeId(); // 上门员工号 - 使用配置的字段名
            default:
                return null;
        }
    }

    /**
     * 生成FID（记录伪编号）
     * 格式：YYYYMMDDHHMMss + 2位随机数
     *
     * @return FID
     */
    private String generateFID() {
        String timestamp = FID_FORMAT.format(new Date());
        int randomNum = RandomUtils.nextInt(1, 100);
        return timestamp + String.format("%02d", randomNum);
    }
}
