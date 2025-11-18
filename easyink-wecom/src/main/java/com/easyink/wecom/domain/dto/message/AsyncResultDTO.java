package com.easyink.wecom.domain.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 同步群发消息dto
 *
 * @author 佚名
 * @date 2021/9/30 9:55
 */
@Data
@ApiModel("同步群发消息dto")
public class AsyncResultDTO {
    @ApiModelProperty("id实体列表")
    private List<MessageIdDTO> messageIdDTOList;
}
