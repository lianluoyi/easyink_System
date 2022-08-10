package com.easywecom.wecom.domain.dto.group;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取结果
 *
 * @author tigger
 * 2022/2/9 15:38
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetJoinWayResult extends WeResultDTO {

    private JoinWay join_way;

    @Data
    public static class JoinWay{
        private String config_id;
        private Integer scene;
        private String remark;
        private Integer auto_create_room;
        private String room_base_name;
        private Integer room_base_id;
        private List<String> chat_id_list;
        private String qr_code;
        private String state;

    }
}
