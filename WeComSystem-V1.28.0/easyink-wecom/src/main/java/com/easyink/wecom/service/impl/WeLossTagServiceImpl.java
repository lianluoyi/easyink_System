package com.easyink.wecom.service.impl;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.BaseException;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.customerloss.CustomerAddLossTagDTO;
import com.easyink.wecom.domain.vo.customerloss.CustomerLossTagVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeLossTagMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeLossTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @author lichaoyu
 * @date 2023/3/24 11:49
 */
@Service
public class WeLossTagServiceImpl implements WeLossTagService {


    private final WeLossTagMapper weLossTagMapper;

    @Resource(name = "corpAccountService")
    private WeCorpAccountService corpAccountService;

    @Autowired
    public WeLossTagServiceImpl(@NotNull WeLossTagMapper weLossTagMapper) {
        this.weLossTagMapper = weLossTagMapper;
    }

    /**
     * 添加流失标签
     *
     * @param customerAddLossTagDTO 流失提醒和打标签类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertWeLossTag(CustomerAddLossTagDTO customerAddLossTagDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        if(StringUtils.isBlank(corpId)){
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 判断流失标签开关状态，1 开启， 0 关闭 ，流失标签开关开启时才进行添加标签的操作，对流失标签是否为空进行判断
        if (WeConstans.DEL_FOLLOW_USER_SWITCH_OPEN.equals(customerAddLossTagDTO.getCustomerLossTagSwitch())) {
            if (customerAddLossTagDTO.getLossTagIdList().isEmpty()) {
                throw new CustomException(ResultTip.TIP_FAIL_INSERT_LOSS_TAG);
            }
            weLossTagMapper.deleteWeLossTag(corpId);
            weLossTagMapper.insertWeLossTag(corpId, customerAddLossTagDTO.getLossTagIdList());
        }
        corpAccountService.startCustomerLossTagSwitch(corpId, customerAddLossTagDTO.getCustomerLossTagSwitch());
        corpAccountService.startCustomerChurnNoticeSwitch(corpId,customerAddLossTagDTO.getCustomerChurnNoticeSwitch());
    }

    /**
     * 查询流失标签
     *
     * @return 结果
     */
    @Override
    public CustomerLossTagVO selectLossWeTag(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new BaseException("查询流失标签id失败");
        }
        CustomerLossTagVO customerLossTagVO = new CustomerLossTagVO();
        customerLossTagVO.setWeTags(weLossTagMapper.selectLossWeTag(corpId));
        return customerLossTagVO;
    }
}
