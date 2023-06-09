package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 类名：WeMaterialAndTagRel
 *
 * @author admin
 * @Date 2021/3/26 17:51
 */
@Data
@ApiModel("素材与标签关联实体")
public class WeMaterialAndTagRel {

    /**
     * 标签表的关联ID
     */
    private Long relId;

    /**
     * 标签ID
     */
    private Long materialTagId;
}