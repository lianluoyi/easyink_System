package com.easywecom.wecom.domain.dto;

import lombok.Data;

/**
 * @description: 企业微信标签相关标签
 * @author admin
 * @create: 2020-09-15 17:55
 **/
@Data
public class WeTagDTO extends WeResultDTO {
    private String group_name;
    private String tag_name;
    private Integer type;
    private Long flower_customer_rel_id;
}
