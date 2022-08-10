package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 模板使用人员范围对象 we_msg_tlp_scope
 *
 * @author tigger
 * @date 2022-01-0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("we_msg_tlp_scope")
public class WeMsgTlpScope  {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "默认欢迎语模板id")
    @TableField("msg_tlp_id")
    private Long msgTlpId;

    @ApiModelProperty(value = "使用人id")
    @TableField("use_user_id")
    private String useUserId;

}
