package com.easyink.wecom.domain.dto;

import com.easyink.wecom.domain.WeChatContactMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会话存档-消息检索DTO
 *
 * @author lichaoyu
 * @date 2023/10/24 21:45
 */
@Data
@ApiModel("会话存档-消息检索DTO")
public class WeChatMappingDTO extends WeChatContactMapping {

    @ApiModelProperty("查询名称（员工、客户、群聊）")
    private String chatName;
}
