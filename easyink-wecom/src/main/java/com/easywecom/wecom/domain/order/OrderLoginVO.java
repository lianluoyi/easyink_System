package com.easywecom.wecom.domain.order;

import com.easywecom.common.core.domain.model.LoginUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 类名: OrderLoginVO
 *
 * @author: 1*+
 * @date: 2021-12-14 11:39
 */
@Data
@ApiModel("工单系统登录返回")
public class OrderLoginVO implements Serializable {

    @ApiModelProperty("网点ID")
    private String networkId;
    @ApiModelProperty("网点名")
    private String networkName;
    @ApiModelProperty("快递公司标识")
    private String expressName;
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("企业ID")
    private String corpId;
    @ApiModelProperty("企业名")
    private String corpName;

    public OrderLoginVO(LoginUser loginUser) {
        if (loginUser.getWeUser() == null) {
            return;
        }
        this.avatar = loginUser.getWeUser().getAvatarMediaid();
        this.userId = loginUser.getWeUser().getUserId();
        this.userName = loginUser.getWeUser().getName();
        this.corpId = loginUser.getWeUser().getCorpId();
        this.corpName = loginUser.getCorpName();
    }


}
