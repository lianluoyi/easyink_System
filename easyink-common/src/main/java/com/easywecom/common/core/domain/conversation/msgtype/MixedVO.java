package com.easywecom.common.core.domain.conversation.msgtype;

import com.easywecom.common.core.domain.conversation.ChatBodyVO;
import lombok.Data;

import java.util.List;

/**
 * 混合VO
 *
 * @author tigger
 * 2022/1/25 18:56
 **/
@Data
public class MixedVO extends AttachmentBaseVO{

    private List<ItemContext> item;


    @Data
    public static class ItemContext extends ChatBodyVO {
        private String type;

    }
}
