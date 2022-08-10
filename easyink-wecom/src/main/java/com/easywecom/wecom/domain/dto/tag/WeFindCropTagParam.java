package com.easywecom.wecom.domain.dto.tag;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @description: 获取标签接口参数实体
 * @author admin
 * @create: 2020-10-20 13:03
 **/
@Data
@Builder
public class WeFindCropTagParam {
    private String[] tag_id;
    private List<String> group_id;
}
