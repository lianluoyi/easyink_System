package com.easywecom.wecom.domain.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名:RemoveWeCustomerTagDTO
 *
 * @author : silver_chariot
 * @date : 2021/9/13 11:18
 */
@Data
@ApiModel
public class RemoveWeCustomerTagDTO {

    @ApiModelProperty(value = "客户成员列表")
    private List<WeUserCustomer> customerList;
    @ApiModelProperty(value = "删除的标签ID列表")
    private List<String> weTagIdList;

    @Data
    public static class WeUserCustomer {

        @ApiModelProperty(value = "外部联系人id")
        private String externalUserid;
        @ApiModelProperty(value = "外部联系人所属的企微成员用户id")
        private String userId;

        /**
         * 公司id
         */
        private String corpId;

        public WeUserCustomer() {
        }

        public WeUserCustomer(String externalUserid, String userId, String corpId) {
            this.externalUserid = externalUserid;
            this.userId = userId;
            this.corpId = corpId;
        }
    }


}
