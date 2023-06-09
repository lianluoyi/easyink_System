package com.easyink.wecom.domain.entity.wechatopen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 企业公众号配置表实体
 *
 * @author : wangzimo
 * @date : 2022-07-25 14:01:57
 */
@Data
@ApiModel("企业公众号配置表实体")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeOpenConfig extends BaseEntity {
    @TableField("corp_id")
    @ApiModelProperty(value = "企业id ")
    private String corpId;

    @TableField("official_account_app_id")
    @ApiModelProperty(value = "公众号appid ")
    private String officialAccountAppId;

    @TableField("official_account_app_secret")
    @ApiModelProperty(value = "公众号secret ")
    private String officialAccountAppSecret;

    @TableField("official_account_domain")
    @ApiModelProperty(value = "公众号域名 ")
    private String officialAccountDomain;

    @TableField("service_type_info")
    /**
     * {@link com.easyink.common.enums.wechatopen.WechatOpenEnum.ServiceType}
     */
    @ApiModelProperty("授权方公众号类型,(0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号),自建应用为空")
    private Integer serviceTypeInfo;

    @TableField("nick_name")
    @ApiModelProperty("公众号的昵称")
    private String nickName;

    @TableField("principal_name")
    @ApiModelProperty("公众号的主体名称，自建应用为空")
    private String principalName;

    @TableField("head_img")
    @ApiModelProperty("授权方头像，自建应用为空")
    private String headImg;

    @TableField("authorizer_access_token")
    @ApiModelProperty("授权方接口调用凭据")
    private String authorizerAccessToken;

    @TableField("authorizer_refresh_token")
    @ApiModelProperty("接口调用凭据刷新令牌(上面令牌过期，需用此令牌刷新)")
    private String authorizerRefreshToken;



}
