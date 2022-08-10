package com.easyink.wecom.domain.dto.welcomemsg;

import com.easyink.wecom.domain.WeMsgTlpMaterial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 命中的特殊欢迎语素材以及文本消息VO
 *
 * @author tigger
 * 2022/1/11 14:55
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HitSpecialWelcomeMsgMaterialVO {
    /**
     * 特殊欢迎语
     */
    private String specialMsg;
    /**
     * 特殊欢迎语对应的素材
     */
    private List<WeMsgTlpMaterial> specialMaterial;
}
