package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeEmpleCodeChannel;
import com.easyink.wecom.domain.dto.emplecode.AddCustomChannelDTO;
import com.easyink.wecom.domain.vo.WeEmpleCodeChannelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 获客助手-自定义渠道Mapper
 *
 * @author lichaoyu
 * @date 2023/8/22 15:08
 */
@Repository
@Mapper
public interface WeEmpleCodeChannelMapper extends BaseMapper<WeEmpleCodeChannel> {

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
     * @param channelId  自定义渠道id
     * @param updateBy   更新人
     * @param updateTime 更新时间
     * @return 结果
     */
    Integer delChannel(@Param("channelId") String channelId, @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);


    /**
     * 获取获客链接下对应的所有渠道id(包含已删除）
     *
     * @return 渠道id列表
     */
    List<Long> getAllAssistantChannel(@Param("empleCodeId") String empleCodeId);

    /**
     * 根据获客链接id获取所有的渠道id列表（包含已删除的渠道）
     *
     * @param empleCodeIdList 获客链接ID列表
     * @return 渠道id列表
     */
    List<Long> getChannelIdByEmpleIds(@Param("empleCodeIdList") List<Long> empleCodeIdList);

    /**
     * 获取获客链接默认渠道id
     *
     * @param channelUrl 获客链接默认渠道URL
     * @return 默认渠道id
     */
    String getDefaultChannelIdByUrl(@Param("channelUrl") String channelUrl);

    /**
     * 根据id获取渠道信息，包含已删除的渠道
     *
     * @param channelIdList 渠道ID列表
     * @return {@link WeEmpleCodeChannel}
     */
    List<WeEmpleCodeChannel> listChannelByIds(@Param("channelIdList") List<String> channelIdList);

    /**
     * 根据渠道id获取渠道信息（包含已删除的）
     *
     * @param channelId 渠道id
     * @return {@link WeEmpleCodeChannel}
     */
    WeEmpleCodeChannel getChannelById(@Param("channelId") String channelId);
}
