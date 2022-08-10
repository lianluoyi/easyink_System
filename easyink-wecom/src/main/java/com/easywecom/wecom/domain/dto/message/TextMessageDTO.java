package com.easywecom.wecom.domain.dto.message;

import com.easywecom.wecom.domain.dto.common.Text;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMessageDTO {

    /**
     * 消息文本内容，最多2000个字符
     */
    @Size(max = 2000, message = "消息文本长度已超出限制")
    private String content;


    public Text toText() {
        return new Text(this.content);
    }
}
