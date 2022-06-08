package com.easywecom.wecom.domain.vo;

import com.easywecom.common.core.domain.entity.WeCorpAccount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WeInternalPreLoginParamVO
 *
 * @author: 1*+
 * @date: 2021-10-11 9:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("内部应用登录二维码参数实体")
public class WeInternalPreLoginParamVO {


    @ApiModelProperty("企业corpId")
    private String corpId;

    @ApiModelProperty("帐号状态（0正常 1停用)")
    private String status;

    @ApiModelProperty("应用id")
    private String agentId;

    @ApiModelProperty("H5域名链接")
    private String h5DoMainName;


    public WeInternalPreLoginParamVO(WeCorpAccount weCorpAccount) {
        if (weCorpAccount == null) {
            return;
        }
        this.agentId = weCorpAccount.getAgentId();
        this.corpId = weCorpAccount.getCorpId();
        this.h5DoMainName = weCorpAccount.getH5DoMainName();
        this.status = weCorpAccount.getStatus();
    }

}
