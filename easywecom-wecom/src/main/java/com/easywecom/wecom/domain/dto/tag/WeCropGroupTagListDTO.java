package com.easywecom.wecom.domain.dto.tag;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import lombok.Data;

import java.util.List;

/**
 * @description: 标签列表实体
 * @author admin
 * @create: 2020-10-17 20:36
 **/
@Data
public class WeCropGroupTagListDTO extends WeResultDTO {

    private List<WeCropGroupTagDTO> tag_group;
}
