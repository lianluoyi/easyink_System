package com.easyink.wecom.domain.dto.emplecode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 生成客户专属活码dto
 * @author tigger
 * 2025/1/13 11:14
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenCustomerEmployQrcodeDTO {

    /**
     * 原活码id
     */
    @NotNull
    private String employCodeId;

    /**
     * 是否打标签
     */
    private Boolean remarkOpen;
    /**
     * 打标签类型
     */
    private Integer remarkType;
    /**
     * 备注名称
     */
    private String remarkName;

    /**
     * 标签列表
     */
    private List<String> tagIds;

    /**
     * 客户属性列表
     */
    @Valid
    private List<CustomerExtendPropertyDTO> extendPropertyList;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerExtendPropertyDTO {

        /**
         * 扩展属性id
         */
        @NotBlank
        private String id;
        /**
         * 属性字段类型
         */
        @NotNull
        private Integer type;
        /**
         * 属性名
         */
        @NotBlank
        private String name;

        /**
         * 扩展属性值
         */
        @NotBlank
        private String value;
    }

}
