package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeEmpleCodeChannel;
import com.easyink.wecom.domain.dto.emplecode.AddCustomChannelDTO;
import com.easyink.wecom.domain.dto.emplecode.EditCustomerChannelDTO;
import com.easyink.wecom.domain.vo.WeEmpleCodeChannelVO;

import java.util.List;

/**
 * 获客助手-自定义渠道Service层
 *
 * @author lichaoyu
 * @date 2023/8/22 15:05
 */
public interface WeEmpleCodeChannelService extends IService<WeEmpleCodeChannel> {

    /**
     * 创建自定义渠道
     *
     * @param addCustomChannelDTO {@link AddCustomChannelDTO}
     * @return 结果
     */
    Integer addChannel(AddCustomChannelDTO addCustomChannelDTO);

    /**
     * 编辑自定义渠道
     *
     * @param editCustomerChannelDTO {@link EditCustomerChannelDTO}
     * @return 结果
     */
    Integer editChannel(EditCustomerChannelDTO editCustomerChannelDTO);

    /**
     * 查询自定义渠道
     *
     * @param addCustomChannelDTO {@link AddCustomChannelDTO}
     * @return 结果
     */
    List<WeEmpleCodeChannelVO> listChannel(AddCustomChannelDTO addCustomChannelDTO);

    /**
     * 删除自定义渠道
     *
     * @param channelId 自定义渠道id
     * @return 结果
     */
    Integer delChannel(String channelId);

    /**
     * 获取获客链接下对应的所有的渠道id，包含已删除的
     *
     * @return 渠道id列表
     */
    List<Long> getAssistantChannelNoDel(String empleCodeId);
}
