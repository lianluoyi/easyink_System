package com.easyink.wecom.domain.dto.autoconfig;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 类名: WeGetGroupListResp
 *
 * @author: 1*+
 * @date: 2021-12-23 15:46
 */
@Data
public class WeGetGroupListResp implements Serializable {


    private UseScope follower_datas;

    private UseScope momentsFollower_datas;


    @Data
    public static class UseScope {

        private JSONArray members;
        private JSONArray parties;
    }


}
