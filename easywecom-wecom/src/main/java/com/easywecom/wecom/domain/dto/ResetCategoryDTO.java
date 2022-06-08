package com.easywecom.wecom.domain.dto;

import lombok.Data;

/**
 * 更换分组传输对象
 */
@Data
public class ResetCategoryDTO {

    /**
     * 类目id
     */
    private String categoryId;

    /**
     * 素材id列表，通过逗号拼接
     */
    private String materials;

}
