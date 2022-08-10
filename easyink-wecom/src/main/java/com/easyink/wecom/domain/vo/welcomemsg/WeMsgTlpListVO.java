package com.easyink.wecom.domain.vo.welcomemsg;

import com.easyink.wecom.domain.WeMsgTlp;
import com.easyink.wecom.domain.WeMsgTlpSpecialRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 类名：WeMsgTlpListVO
 *
 * @author Society my sister Li
 * @date 2021-10-18 16:51
 */
@Data
@ApiModel("欢迎语模板列表")
public class WeMsgTlpListVO extends WeMsgTlp {

    @ApiModelProperty("默认欢迎语")
    private String defaultWelcomeMsg;
    @ApiModelProperty("创建人姓名")
    private String createName;
    @ApiModelProperty("创建人所属部门名字")
    private String mainDepartmentName;
    @ApiModelProperty("创建时间")
    private Date createTime;

//    @ApiModelProperty("主素材，不包含特殊时段")
//    private List<WeMsgTlpMaterial> mainMaterialList;
    @ApiModelProperty("使用员工list")
    private List<WeUserUseScopeVO> useUsers;
    @ApiModelProperty("特殊时段list")
    private List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules;

}
