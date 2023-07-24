package com.easyink.wecom.openapi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.openapi.domain.entity.AppCallbackSetting;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 类名: API-消息订阅配置表持久层映射
 *
 * @author : silver_chariot
 * @date : 2023/7/17 11:40
 **/
@Mapper
@Repository
public interface AppCallbackSettingMapper extends BaseMapper<AppCallbackSetting> {
}
