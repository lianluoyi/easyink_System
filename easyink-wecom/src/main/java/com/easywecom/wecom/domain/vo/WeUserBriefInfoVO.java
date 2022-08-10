package com.easywecom.wecom.domain.vo;

import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.utils.bean.BeanUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 用户概要信息VO
 *
 * @author : silver_chariot
 * @date : 2021/8/30 16:23
 */
@Data
@NoArgsConstructor
@ApiModel("客户概要信息")
public class WeUserBriefInfoVO {

    @ApiModelProperty(value = "企微用户ID")
    private String userId;

    @ApiModelProperty(value = "企微用户名称")
    private String name;

    @ApiModelProperty(value = "企微用户头像")
    private String avatarMediaid;

    @ApiModelProperty(value = "主部门id")
    private Long mainDepartment;

    /**
     * weUser转换成 weUserBriefInfo的构造方法
     *
     * @param user 企微用户实体
     */
    public WeUserBriefInfoVO(WeUser user) {
        BeanUtils.copyPropertiesASM(user, this);
    }
}
