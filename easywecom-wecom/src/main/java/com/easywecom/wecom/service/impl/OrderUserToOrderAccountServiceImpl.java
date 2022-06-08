package com.easywecom.wecom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.AppIdEnum;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.StaffActivateEnum;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.OrderClient;
import com.easywecom.wecom.domain.OrderGroupToOrderCustomerEntity;
import com.easywecom.wecom.domain.OrderUserToOrderAccountEntity;
import com.easywecom.wecom.domain.WeMyApplication;
import com.easywecom.wecom.domain.dto.BindDetailDTO;
import com.easywecom.wecom.domain.dto.UnBindOrderDTO;
import com.easywecom.wecom.domain.entity.appconfig.YiGeOrderAppConfig;
import com.easywecom.wecom.domain.order.*;
import com.easywecom.wecom.domain.vo.BindDetailVO;
import com.easywecom.wecom.domain.vo.BindOrderTotalVO;
import com.easywecom.wecom.mapper.OrderUserToOrderAccountMapper;
import com.easywecom.wecom.service.OrderGroupToOrderCustomerService;
import com.easywecom.wecom.service.OrderUserToOrderAccountService;
import com.easywecom.wecom.service.WeMyApplicationService;
import com.easywecom.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 类名： 绑定员工工单账号接口
 *
 * @author 佚名
 * @date 2021/12/14 10:55
 */
@Service
@Slf4j
public class OrderUserToOrderAccountServiceImpl extends ServiceImpl<OrderUserToOrderAccountMapper, OrderUserToOrderAccountEntity> implements OrderUserToOrderAccountService {
    private final OrderClient orderClient;
    private final WeMyApplicationService weMyApplicationService;
    private final WeUserService weUserService;
    private final OrderGroupToOrderCustomerService orderGroupToOrderCustomerService;

    public OrderUserToOrderAccountServiceImpl(OrderClient orderClient, WeMyApplicationService weMyApplicationService, WeUserService weUserService, OrderGroupToOrderCustomerService orderGroupToOrderCustomerService) {
        this.orderClient = orderClient;
        this.weMyApplicationService = weMyApplicationService;
        this.weUserService = weUserService;
        this.orderGroupToOrderCustomerService = orderGroupToOrderCustomerService;
    }


    @Override
    public OrderVerifyVO verifyNetworkId(@NotBlank String networkId) {
        final Integer successCode = 0;
        try {
            OrderBaseResp<OrderVerifyVO> resp = orderClient.verifyNetwork(networkId);
            if (!successCode.equals(resp.getCode())) {
                //失败
                throw new CustomException(ResultTip.TIP_NETWORK_ID_ERROR, ResultTip.TIP_NETWORK_ID_ERROR.getTipMsg(), networkId);
            }
            return resp.getResult();
        } catch (ForestRuntimeException e) {
            log.error("绑定工单网点异常 ex:{}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(ResultTip.TIP_NETWORK_ID_ERROR, ResultTip.TIP_NETWORK_ID_ERROR.getTipMsg(),networkId);
        }
    }

    @Override
    public void bindUser(OrderUserToOrderAccountEntity orderAccountEntity) {
        StringUtils.checkCorpId(orderAccountEntity.getCorpId());
        orderAccountEntity.setNetworkId(getNetworkId(orderAccountEntity.getCorpId()));
        //校验是否重复绑定员工
        OrderUserToOrderAccountEntity existOrderAccount = this.getOne(new LambdaQueryWrapper<OrderUserToOrderAccountEntity>()
                .eq(OrderUserToOrderAccountEntity::getOrderUserId, orderAccountEntity.getOrderUserId())
                .eq(OrderUserToOrderAccountEntity::getCorpId, orderAccountEntity.getCorpId())
                .eq(OrderUserToOrderAccountEntity::getNetworkId, orderAccountEntity.getNetworkId()).last(GenConstants.LIMIT_1));
        if (existOrderAccount != null) {
            throw new CustomException(ResultTip.TIP_ORDER_USER_ID_BIND_ERROR);
        }
        this.saveOrUpdate(orderAccountEntity);
    }

    @Override
    public void unbindUser(UnBindOrderDTO orderAccountEntity) {
        this.remove(new LambdaQueryWrapper<OrderUserToOrderAccountEntity>()
                .eq(OrderUserToOrderAccountEntity::getUserId, orderAccountEntity.getUserId())
                .eq(OrderUserToOrderAccountEntity::getCorpId, orderAccountEntity.getCorpId())
                .last(GenConstants.LIMIT_1));
    }

    @Override
    public BindOrderTotalVO getBindTotal(String corpId) {
        BindOrderTotalVO bindOrderTotalVO = new BindOrderTotalVO();
        bindOrderTotalVO.setBindNum(this.count(new LambdaQueryWrapper<OrderUserToOrderAccountEntity>().eq(OrderUserToOrderAccountEntity::getCorpId, corpId)));
        bindOrderTotalVO.setUserNum(weUserService.count(new LambdaQueryWrapper<WeUser>()
                .eq(WeUser::getCorpId, corpId)
                .eq(WeUser::getIsActivate, StaffActivateEnum.ACTIVE.getCode())));
        return bindOrderTotalVO;
    }

    @Override
    public List<BindDetailVO> listOfBindDetail(BindDetailDTO bindDetailDTO) {
        StringUtils.checkCorpId(bindDetailDTO.getCorpId());
        return baseMapper.listOfBindDetail(bindDetailDTO);
    }

    @Override
    public List<OrderUser> networkUser(String corpId) {
        return orderClient.networkUser(getNetworkId(corpId)).getResult();
    }

    @Override
    public List<OrderCustomer> networkCustomer(String corpId) {
        return orderClient.networkCustomer(getNetworkId(corpId)).getResult();
    }


    /**
     * 获取网点id （绑定后调用）
     * 未配置壹鸽应用抛出异常 8004
     *
     * @param corpId 企业id
     */
    @Override
    public String getNetworkId(String corpId) {
        // 网点配置查询未删除的配置
        WeMyApplication weMyApplication = weMyApplicationService.getOne(new LambdaQueryWrapper<WeMyApplication>()
                .eq(WeMyApplication::getAppid, AppIdEnum.YIGE_ORDER.getCode())
                .eq(WeMyApplication::getCorpId, corpId)
                .eq(WeMyApplication::getEnable, 1).last(GenConstants.LIMIT_1));
        //校验网点配置
        if (weMyApplication != null && StringUtils.isNotBlank(weMyApplication.getConfig())) {
            YiGeOrderAppConfig appConfig = JSONObject.parseObject(weMyApplication.getConfig(), YiGeOrderAppConfig.class);
            if (StringUtils.isBlank(appConfig.getNetworkId())) {
                throw new CustomException(ResultTip.TIP_NETWORK_ID_BIND_ERROR);
            }
            return appConfig.getNetworkId();
        } else {
            throw new CustomException(ResultTip.TIP_YIGE_APP_CONFIG_ERROR);
        }
    }

    @Override
    public OrderBindInfoVO getBindInfo(OrderBindInfoDTO orderBindInfoDTO) {
        OrderBindInfoVO orderBindInfoVO = new OrderBindInfoVO();
        if (StringUtils.isBlank(orderBindInfoDTO.getUserId())) {
            return orderBindInfoVO;
        }
        String networkId = getNetworkId(orderBindInfoDTO.getCorpId());
        //员工是否绑定
        OrderUserToOrderAccountEntity user = this.getOne(new LambdaQueryWrapper<OrderUserToOrderAccountEntity>()
                .eq(OrderUserToOrderAccountEntity::getUserId, orderBindInfoDTO.getUserId())
                .eq(OrderUserToOrderAccountEntity::getCorpId, orderBindInfoDTO.getCorpId())
                .eq(OrderUserToOrderAccountEntity::getNetworkId, networkId)
                .last(GenConstants.LIMIT_1));
        if (user == null) {
            return orderBindInfoVO;
        }
        orderBindInfoVO.setOrderUserId(user.getOrderUserId());
        orderBindInfoVO.setOrderUserName(user.getOrderUserName());
        //群是否绑定
        if (StringUtils.isNotBlank(orderBindInfoDTO.getChatId())) {
            OrderGroupToOrderCustomerEntity group = orderGroupToOrderCustomerService.getOne(new LambdaQueryWrapper<OrderGroupToOrderCustomerEntity>()
                    .eq(OrderGroupToOrderCustomerEntity::getChatId, orderBindInfoDTO.getChatId())
                    .eq(OrderGroupToOrderCustomerEntity::getCorpId, orderBindInfoDTO.getCorpId())
                    .eq(OrderGroupToOrderCustomerEntity::getNetworkId, networkId)
                    .last(GenConstants.LIMIT_1));
            if (group == null) {
                return orderBindInfoVO;
            }
            orderBindInfoVO.setOrderCustomerId(group.getOrderCustomerId());
            orderBindInfoVO.setOrderCustomerName(group.getOrderCustomerName());
        }
        return orderBindInfoVO;
    }
}