package com.easyink.wecom.domain.dto.form.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 第三方推送数据DATA
 *
 * @author easyink
 */
@ApiModel("第三方推送数据DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThirdPartyPushSysData {

    @ApiModelProperty("企业id")
    private String corpId;

    @ApiModelProperty("推送类型(1:表单提交推送 2:超时未提交推送)")
    private Integer pushType;

    @ApiModelProperty("推送时间")
    private Date pushTime;

    @ApiModelProperty("客户信息")
    private CustomerInfoDTO customerInfo;

    @ApiModelProperty("员工信息")
    private EmployeeInfoDTO employeeInfo;

    @ApiModelProperty("表单信息")
    private FormInfoDTO formInfo;



    @ApiModel("客户信息")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerInfoDTO {

        @ApiModelProperty("企业id")
        private String corpId;
        @ApiModelProperty("所属员工id")
        private String userId;

        @ApiModelProperty("客户外部ID")
        private String externalUserid;

        @ApiModelProperty("客户姓名")
        private String name;
        @ApiModelProperty("电话")
        private String phone;

        @ApiModelProperty("出生日期")
        private Date birthday;
        @ApiModelProperty("邮箱")
        private String email;
        @ApiModelProperty("地址")
        private String address;

        @ApiModelProperty("描述")
        private String description;

        @ApiModelProperty("扩展字段信息")
        private List<CustomerExtendPropertyInfo> extendFields;

        /**
         * 获取第一个手机号
         * 如果手机号包含逗号分隔，则只返回第一个手机号
         *
         * @return 第一个手机号
         */
        public String getFirstPhone() {
            if (phone == null || phone.trim().isEmpty()) {
                return phone;
            }

            if (phone.contains(",")) {
                return phone.split(",")[0].trim();
            }

            return phone;
        }
    }

    @ApiModel("客户自定义字段信息")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerExtendPropertyInfo {
        @ApiModelProperty("属性id")
        private Long propId;

        @ApiModelProperty("属性类型")
        private Integer propType;

        @ApiModelProperty("属性名称")
        private String propName;

        @ApiModelProperty("属性值")
        private String propValue;
    }

    @ApiModel("员工信息")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeInfoDTO {
        @ApiModelProperty("员工ID")
        private String userId;

    }

    @ApiModel("表单信息")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FormInfoDTO {
        @ApiModelProperty("表单ID")
        private Long formId;

        @ApiModelProperty("发送时间")
        private Date sentTime;

        @ApiModelProperty("提交时间")
        private Date submitTime;


        @ApiModelProperty("表单提交内容")
        private String formSubmitResult;

    }


}
