package com.easyink.wecom.strategy.appstrategy;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.enums.ExpressNameEnum;
import com.easyink.wecom.client.OrderClient;
import com.easyink.wecom.domain.OrderGroupToOrderCustomerEntity;
import com.easyink.wecom.domain.OrderUserToOrderAccountEntity;
import com.easyink.wecom.domain.entity.appconfig.YiGeOrderAppConfig;
import com.easyink.wecom.domain.order.OrderVerifyVO;
import com.easyink.wecom.service.OrderGroupToOrderCustomerService;
import com.easyink.wecom.service.OrderUserToOrderAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类名： YiGeOrderAppConfigStrategy
 *
 * @author 佚名
 * @date 2021/12/13 17:16
 */
@Component
@Slf4j
public class YiGeOrderAppConfigStrategy implements AppConfigStrategy {
    private final OrderClient orderClient;
    private final OrderUserToOrderAccountService userToOrderAccountService;
    private final OrderGroupToOrderCustomerService groupToOrderCustomerService;

    @Autowired
    public YiGeOrderAppConfigStrategy(OrderClient orderClient, OrderUserToOrderAccountService userToOrderAccountService, OrderGroupToOrderCustomerService groupToOrderCustomerService) {
        this.orderClient = orderClient;
        this.userToOrderAccountService = userToOrderAccountService;
        this.groupToOrderCustomerService = groupToOrderCustomerService;
    }


    /**
     * 配置处理网点
     *
     * @param config 配置（jsonStr）
     * @param corpId
     */
    @Override
    public String configHandler(String config, String corpId) {
        com.easyink.common.utils.StringUtils.checkCorpId(corpId);
        if (StringUtils.isBlank(config)) {
            return config;
        }
        //转java对象
        YiGeOrderAppConfig yiGeOrderAppConfig = JSON.parseObject(config, YiGeOrderAppConfig.class);
        if (StringUtils.isNotBlank(yiGeOrderAppConfig.getNetworkId())) {
            //校验网点
            OrderVerifyVO orderVerifyVO = userToOrderAccountService.verifyNetworkId(yiGeOrderAppConfig.getNetworkId());
            //存储网点名称 存储快递公司类型
            yiGeOrderAppConfig.setNetworkName(orderVerifyVO.getNetworkName());
            yiGeOrderAppConfig.setType(ExpressNameEnum.getEnum(orderVerifyVO.getLogistics()).getCode());
        } else if (StrUtil.EMPTY.equals(yiGeOrderAppConfig.getNetworkId())) {
            //为空字符串说明是解绑清空名称
            yiGeOrderAppConfig.setNetworkName(StrUtil.EMPTY);
            yiGeOrderAppConfig.setNetworkId(StrUtil.EMPTY);
            //0表示未选择快递
            yiGeOrderAppConfig.setType(0);
            // 清空绑定关系
            userToOrderAccountService.remove(new LambdaQueryWrapper<OrderUserToOrderAccountEntity>().eq(OrderUserToOrderAccountEntity::getCorpId, corpId));
            groupToOrderCustomerService.remove(new LambdaQueryWrapper<OrderGroupToOrderCustomerEntity>().eq(OrderGroupToOrderCustomerEntity::getCorpId, corpId));
        }
        return JSON.toJSONString(yiGeOrderAppConfig);
    }
}
