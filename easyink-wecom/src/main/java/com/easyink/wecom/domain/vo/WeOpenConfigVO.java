package com.easyink.wecom.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 微信公众号配置VO
 *
 * @author wx
 * 2023/1/11 17:26
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WeOpenConfigVO extends BaseEntity {

    @ApiModelProperty(value = "公众号appid ")
    private String officialAccountAppId;

    @ApiModelProperty(value = "公众号secret ")
    private String officialAccountAppSecret;

    @ApiModelProperty(value = "公众号域名 ")
    private String officialAccountDomain;

    /**
     * {@link com.easyink.common.enums.wechatopen.WechatOpenEnum.ServiceType}
     */
    @ApiModelProperty("授权方公众号类型,(0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号),自建应用为空")
    private Integer serviceTypeInfo;

    @ApiModelProperty("公众号昵称")
    private String nickName;

    @ApiModelProperty("公众号的主体名称，自建应用为空")
    private String principalName;

    @ApiModelProperty("授权方头像，自建应用为空")
    private String headImg;

    /**
     * 转化为VO
     *
     * @param config    {@link WeOpenConfig}
     * @return
     */
    public static WeOpenConfigVO covert2WeOpenConfigVO(WeOpenConfig config) {
        if (config == null) {
            return null;
        }
        WeOpenConfigVO weOpenConfigVO = new WeOpenConfigVO();
        BeanUtils.copyPropertiesASM(config, weOpenConfigVO);
        return weOpenConfigVO;
    }

}
