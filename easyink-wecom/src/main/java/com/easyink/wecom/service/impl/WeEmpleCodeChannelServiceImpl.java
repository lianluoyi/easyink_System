package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeEmpleCodeChannel;
import com.easyink.wecom.domain.dto.emplecode.AddCustomChannelDTO;
import com.easyink.wecom.domain.dto.emplecode.EditCustomerChannelDTO;
import com.easyink.wecom.domain.vo.WeEmpleCodeChannelVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeEmpleCodeChannelMapper;
import com.easyink.wecom.service.WeEmpleCodeChannelService;
import com.easyink.wecom.service.WeEmpleCodeService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 获客助手-自定义渠道Service实现类
 *
 * @author lichaoyu
 * @date 2023/8/22 15:07
 */
@Service
public class WeEmpleCodeChannelServiceImpl extends ServiceImpl<WeEmpleCodeChannelMapper, WeEmpleCodeChannel> implements WeEmpleCodeChannelService {

    private final WeEmpleCodeService weEmpleCodeService;

    public WeEmpleCodeChannelServiceImpl(WeEmpleCodeService weEmpleCodeService) {
        this.weEmpleCodeService = weEmpleCodeService;
    }

    /**
     * 查询自定义渠道
     *
     * @param addCustomChannelDTO {@link AddCustomChannelDTO}
     * @return 结果
     */
    @Override
    public List<WeEmpleCodeChannelVO> listChannel(AddCustomChannelDTO addCustomChannelDTO) {
        if (StringUtils.isBlank(addCustomChannelDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeEmpleCode assistant = weEmpleCodeService.getById(addCustomChannelDTO.getEmpleCodeId());
        if (assistant == null) {
            return null;
        }
        // 是否需要过滤默认渠道
        if (addCustomChannelDTO.getIsFilterDefaultUrl() != null) {
            if (addCustomChannelDTO.getIsFilterDefaultUrl()) {
                addCustomChannelDTO.setDefaultUrl(assistant.getQrCode());
            }
        }
        return this.baseMapper.listChannel(addCustomChannelDTO);
    }

    /**
     * 编辑自定义渠道
     *
     * @param editCustomerChannelDTO {@link EditCustomerChannelDTO}
     * @return 结果
     */
    @Override
    public Integer editChannel(EditCustomerChannelDTO editCustomerChannelDTO) {
        if (StringUtils.isBlank(editCustomerChannelDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeEmpleCodeChannel weEmpleCodeChannel = this.baseMapper.selectById(editCustomerChannelDTO.getChannelId());
        weEmpleCodeChannel.setName(editCustomerChannelDTO.getName());
        weEmpleCodeChannel.setUpdateBy(LoginTokenService.getUsername());
        weEmpleCodeChannel.setUpdateTime(new Date());
        return this.baseMapper.updateById(weEmpleCodeChannel);
    }

    /**
     * 删除自定义渠道
     *
     * @param channelId 自定义渠道id
     * @return 结果
     */
    @Override
    public Integer delChannel(String channelId) {
        if(channelId == null) {
            throw new CustomException(ResultTip.TIP_DELETE_EMPLE_CHANNEL_NO_FIND);
        }
        return this.baseMapper.delChannel(channelId, LoginTokenService.getUsername(), new Date());
    }


    @Override
    public List<Long> getAssistantChannelNoDel(String empleCodeId) {
        if (StringUtils.isBlank(empleCodeId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        return this.baseMapper.getAllAssistantChannel(empleCodeId);
    }

    /**
     * 创建自定义渠道
     *
     * @param addCustomChannelDTO {@link AddCustomChannelDTO}
     * @return 结果
     */
    @Override
    public Integer addChannel(AddCustomChannelDTO addCustomChannelDTO) {
        if (StringUtils.isBlank(addCustomChannelDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 获取获客链接信息
        WeEmpleCode assistantInfo = weEmpleCodeService.getById(addCustomChannelDTO.getEmpleCodeId());
        if (assistantInfo == null) {
            return null;
        }
        // 查找是否存在未被删除的渠道名称
        WeEmpleCodeChannel alreadyHave = this.getOne(new LambdaQueryWrapper<WeEmpleCodeChannel>()
                .eq(WeEmpleCodeChannel::getEmpleCodeId, addCustomChannelDTO.getEmpleCodeId())
                .eq(WeEmpleCodeChannel::getName, addCustomChannelDTO.getName())
                .eq(WeEmpleCodeChannel::getDelFlag, WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE));
        if (alreadyHave != null) {
            throw new CustomException(ResultTip.TIP_EMPLE_CHANNEL_REPEAT);
        }
        WeEmpleCodeChannel weEmpleCodeChannel = new WeEmpleCodeChannel(addCustomChannelDTO.getName(), addCustomChannelDTO.getEmpleCodeId());
        weEmpleCodeChannel.handleChannelUrl(assistantInfo.getQrCode());
        weEmpleCodeChannel.setCreateBy(LoginTokenService.getUsername());
        weEmpleCodeChannel.setCreateTime(new Date());
        return this.baseMapper.insert(weEmpleCodeChannel);
    }
}
