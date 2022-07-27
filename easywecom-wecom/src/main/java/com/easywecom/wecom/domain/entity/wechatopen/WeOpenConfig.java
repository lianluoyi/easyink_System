package com.easywecom.wecom.domain.entity.wechatopen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easywecom.common.core.domain.BaseEntity;
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



}
