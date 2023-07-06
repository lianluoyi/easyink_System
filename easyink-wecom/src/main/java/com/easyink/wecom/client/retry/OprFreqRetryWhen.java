package com.easyink.wecom.client.retry;

import cn.hutool.json.JSONUtil;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.easyink.wecom.domain.dto.WeResultDTO;
import org.springframework.stereotype.Component;

/**
 * 类名: 操作频繁重试条件 ,
 *  详见 ：Tower 任务: 客户扫活码加好友之后没有自动备注 ( https://tower.im/teams/636204/todos/69053 )
 *
 * @author : silver_chariot
 * @date : 2023/5/29 17:04
 **/
@Component
public class OprFreqRetryWhen {
    /**
     * 操作频繁状态码
     *
     * e.g. {\"errcode\":45033,\"errmsg\":\"api concurrent out of limit, hint: [1684771447235730429443947],
     * from ip: 139.199.198.208, more info at https://open.work.weixin.qq.com/devtool/query?e=45033\"
     *
     */
    private static final  Integer OPR_FREQ_ERR_CODE = 45033 ;
    public boolean retryWhen(ForestRequest req, ForestResponse res) {
        if(res == null) {
            return true ;
        }
        WeResultDTO weResultDto ;
        try {
             weResultDto = JSONUtil.toBean(res.getContent(), WeResultDTO.class);
        }catch (Exception e) {
            // 转换异常则重试
            return true ;
        }
        // 当出现操作频繁 错误码 则需要重试
        return  OPR_FREQ_ERR_CODE.equals(weResultDto.getErrcode()) ;
    }
}
