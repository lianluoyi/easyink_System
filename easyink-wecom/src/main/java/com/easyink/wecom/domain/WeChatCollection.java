package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 聊天工具 侧边栏栏 素材收藏
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
public class WeChatCollection extends BaseEntity {

    @ApiModelProperty(value = "聊天工具 侧边栏栏 素材收藏")
    @TableId
    @TableField("collection_id")
    private Long collectionId;

    @ApiModelProperty(value = "素材id")
    @TableField("material_id")
    private Long materialId;

    @ApiModelProperty(value = "")
    @TableField("user_id")
    private String userId;


}
