package com.easywecom.wecom.domain.dto.welcomemsg;

import com.easywecom.wecom.domain.WeMsgTlpMaterial;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 欢迎语添加dto
 *
 * @author tigger
 * 2022/1/4 16:52
 **/
@Data
public class WelComeMsgUpdateDTO {

    @NotNull(message = "请选择修改的欢迎语id")
    @ApiModelProperty("修改的默认欢迎语id")
    private Long id;

    @ApiModelProperty(value = "授权企业ID", hidden = true)
    private String corpId;

    @ApiModelProperty(value = "默认欢迎语")
    private String defaultWelcomeMsg;

    @ApiModelProperty(value = "欢迎语适用对象类型:1:员工欢迎语;2:客户群欢迎语", hidden = true)
    private Integer welcomeMsgTplType;

    @ApiModelProperty("默认欢迎语素材")
    private List<WeMsgTlpMaterial> defaultMaterialList;
    @ApiModelProperty("需要删除的默认欢迎素材Ids")
    private List<Long> removeMaterialIds;


}
