package com.easyink.wecom.domain.enums.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.easyink.common.exception.CustomException;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 推广类型
 *
 * @author tigger
 * 2023/1/11 10:06
 **/
public enum PromotionalType {
    /**
     * 链接推广
     */
    LINK(1, LinkPromotional.class),
    /**
     * 二维码推广
     */
    QR_CODE(2, QrCodePromotional.class),
    ;

    @Getter
    private final Integer code;
    @Getter
    private final Class<?> clazz;

    PromotionalType(Integer code, Class<?> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<PromotionalType> getByCode(Integer code) {
        for (PromotionalType value : values()) {
            if (value.code.equals(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 校验or 返回
     *
     * @param code code
     * @return 枚举
     */
    public static PromotionalType validCode(Integer code) {
        Optional<PromotionalType> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new CustomException("推广类型异常"));
    }

    /**
     * 校验并返回
     *
     * @param promotionalInfo 推广详情JSON
     * @return
     */
    public static List<BasePromotional> validAndReturn(String promotionalInfo) {
        if(StringUtils.isBlank(promotionalInfo)){
            return new ArrayList<>();
        }
        List<BasePromotional> promotionalList = new ArrayList<>();
        JSONArray promotionalJsonArray = JSONObject.parseArray(promotionalInfo);
        for (Object promotionalObj : promotionalJsonArray) {
            BasePromotional basePromotional = JSONObject.parseObject(JSONObject.toJSONString(promotionalObj), BasePromotional.class);
            promotionalList.add((BasePromotional)JSONObject.parseObject(JSONObject.toJSONString(promotionalObj), PromotionalType.validCode(basePromotional.getType()).getClazz()));
        }
        return promotionalList;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasePromotional {
        /**
         * 推广类型
         */
        private Integer type;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkPromotional extends BasePromotional {
        /**
         * 链接URL
         */
        private String linkUrl;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QrCodePromotional extends BasePromotional {
        /**
         * 二维码URL
         */
        private String qrCodeUrl;
    }
}
