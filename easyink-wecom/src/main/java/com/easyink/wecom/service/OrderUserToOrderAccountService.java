package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.OrderUserToOrderAccountEntity;
import com.easyink.wecom.domain.dto.BindDetailDTO;
import com.easyink.wecom.domain.dto.UnBindOrderDTO;
import com.easyink.wecom.domain.order.*;
import com.easyink.wecom.domain.vo.BindDetailVO;
import com.easyink.wecom.domain.vo.BindOrderTotalVO;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 类名： 企业员工与工单帐号绑定关系表接口
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
public interface OrderUserToOrderAccountService extends IService<OrderUserToOrderAccountEntity> {
    /**
     * 校验网点id
     *
     * @param networkId 网点id
     * @return {@link OrderBaseResp<OrderVerifyVO>}
     */
    OrderVerifyVO verifyNetworkId(@Validated @NotBlank String networkId);

    /**
     * 绑定员工工单账号
     *
     * @param orderAccountEntity 实体
     */
    void bindUser(OrderUserToOrderAccountEntity orderAccountEntity);

    /**
     * 解绑工单账号
     *
     * @param orderAccountEntity 实体
     */
    void unbindUser(UnBindOrderDTO orderAccountEntity);

    /**
     * 获取绑定员工数量
     *
     * @param corpId 企业id
     * @return 数量
     */
    BindOrderTotalVO getBindTotal(String corpId);

    /**
     * 获取绑定详情
     *
     * @param bindDetailDTO 查询条件
     * @return {@link List<BindDetailVO>}
     */
    List<BindDetailVO> listOfBindDetail(BindDetailDTO bindDetailDTO);

    /**
     * 获取网点账号列表接口
     *
     * @param corpId 企业id
     * @return OrderUser
     */
    List<OrderUser> networkUser(String corpId);

    /**
     * 获取网点客户列表接口
     *
     * @param corpId 企业id
     * @return {@link OrderCustomer}
     */
    List<OrderCustomer> networkCustomer(String corpId);

    /**
     * 获取网点id （绑定后调用）
     * 未配置壹鸽应用抛出异常 8004
     *
     * @param corpId 企业id
     * @return networkId
     */
    String getNetworkId(String corpId);

    /**
     * 获取绑定详情
     *
     * @param orderBindInfoDTO
     * @return
     */
    OrderBindInfoVO getBindInfo(OrderBindInfoDTO orderBindInfoDTO);
}

