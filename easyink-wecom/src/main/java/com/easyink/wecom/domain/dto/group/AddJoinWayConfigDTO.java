package com.easyink.wecom.domain.dto.group;

import cn.hutool.core.util.StrUtil;
import com.easyink.wecom.domain.WeGroupCodeActual;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 配置客户群进群方式DTO
 *
 * @author tigger
 * 2022/2/9 15:25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddJoinWayConfigDTO {
    private Integer scene;
    private String remark;
    private Integer auto_create_room;
    private String room_base_name;
    private Integer room_base_id;
    private List<String> chat_id_list;
    private String state;

    /**
     * 转换为接口调用DTO
     *
     * @param corpActual
     * @return
     * @see WeGroupCodeCorpActual
     */
    public AddJoinWayConfigDTO copyFromWeGroupCodeActual(WeGroupCodeActual corpActual) {
        this.scene = corpActual.getScene();
        this.remark = corpActual.getRemark();
        this.auto_create_room = corpActual.getAutoCreateRoom();
        this.room_base_name = corpActual.getRoomBaseName();
        this.room_base_id = corpActual.getRoomBaseId();
        this.chat_id_list = Arrays.asList(corpActual.getChatIds().split(StrUtil.COMMA));
        this.state = corpActual.getState();
        return this;
    }

}
