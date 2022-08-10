package com.easywecom.wecom.domain.entity.moment;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 类名： 朋友圈地理位置
 *
 * @author 佚名
 * @date 2022/1/6 14:43
 */
@ApiModel("朋友圈地理位置")
@Data
public class Location {
    private String latitude;
    private String longitude;
    private String name;
}
