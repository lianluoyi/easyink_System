package com.easywecom.wecom.domain.vo.welcomemsg;

import com.easywecom.wecom.domain.WeMsgTlpMaterial;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 好友欢迎语素材VO
 *
 * @author tigger
 * 2022/1/10 16:19
 **/
@Builder
@Data
public class WeEmployMaterialVO {

    private String defaultMsg;

    private List<WeMsgTlpMaterial> weMsgTlpMaterialList;
}
