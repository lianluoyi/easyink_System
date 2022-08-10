package com.easyink.wecom.domain.vo.moment;

import com.easyink.wecom.domain.WeWordsDetailEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 类名： MomentAttachmentVO
 *
 * @author 佚名
 * @date 2022/1/13 11:06
 */
@Data
@ApiModel("查询朋友圈创建附件VO（不包含文本）")
public class MomentAttachmentVO extends WeWordsDetailEntity {
    private Long momentTaskId;
}
