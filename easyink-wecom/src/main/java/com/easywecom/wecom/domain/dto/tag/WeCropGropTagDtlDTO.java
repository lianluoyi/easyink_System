package com.easywecom.wecom.domain.dto.tag;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import lombok.Data;

/**
 * @description: 单个标签组
 * @author admin
 * @create: 2020-10-17 23:51
 **/
@Data
public class WeCropGropTagDtlDTO extends WeResultDTO {

    private WeCropGroupTagDTO tag_group;
}
