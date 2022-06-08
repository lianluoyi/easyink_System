package com.easywecom.wecom.domain.dto.moment;

import com.easywecom.wecom.domain.dto.message.TextMessageDTO;
import com.easywecom.wecom.domain.entity.moment.VisibleRange;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 类名： 朋友圈创建发表任务DTO（企微接口）
 *
 * @author 佚名
 * @date 2022/1/6 16:27
 */
@Data
@ApiModel("朋友圈创建发表任务DTO")
@NoArgsConstructor
public class AddMomentTaskDTO {
    @ApiModelProperty("文本消息")
    private TextMessageDTO text;
    @ApiModelProperty("附件，不能与text.content同时为空，最多支持9个图片类型，或者1个视频，或者1个链接。类型只能三选一，若传了不同类型，报错’invalid attachments msgtype’")
    private List<MomentAttachment> attachments;
    @ApiModelProperty("指定的发表范围")
    private VisibleRange visible_range;

    public AddMomentTaskDTO(List<String> users,List<String> tags,String content) {
        this.text = new TextMessageDTO(content);
        this.visible_range = new VisibleRange(users, tags);
    }
}
