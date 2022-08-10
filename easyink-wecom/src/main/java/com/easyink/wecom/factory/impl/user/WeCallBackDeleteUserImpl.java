package com.easyink.wecom.factory.impl.user;

import com.easyink.common.constant.RedisKeyConstants;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @description 删除成员事件
 * @date 2021/1/20 22:44
 **/
@Slf4j
@Component("delete_user")
public class WeCallBackDeleteUserImpl extends WeEventStrategy {
    @Autowired
    private WeUserService weUserService;
    @Autowired
    private RedisCache redisCache;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getUserId(), message.getToUserName(), message.getCreateTime().toString())) {
            log.error("[delete_user]删除员工回调事件,返回数据缺失,message:{}", message);
            return;
        }
        String corpId = message.getToUserName();
        weUserService.deleteUserNoToWeCom(message.getUserId(), corpId);
        // 由于收到回调的时候马上去获取待分配离职员工会获取不到数据,需要等一会才有数据
        // 所以这里等待30S后再去调接口
        redisCache.setCacheObject(RedisKeyConstants.DELETE_USER_KEY + corpId, corpId, 30, TimeUnit.SECONDS);
    }
}
