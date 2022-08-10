package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeMyApplicationUseScopeEntity;
import com.easywecom.wecom.domain.dto.SetApplicationUseScopeDTO;

import java.util.List;

/**
 * 类名： 我的应用使用范围接口
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
public interface WeMyApplicationUseScopeService extends IService<WeMyApplicationUseScopeEntity> {


    /**
     * 设置我的应用使用范围
     *
     * @param corpId       企业ID
     * @param appid        应用ID
     * @param useScopeList 使用范围
     */
    void setMyApplicationUseScope(String corpId, Integer appid, List<SetApplicationUseScopeDTO.UseScope> useScopeList);

    /**
     * 获取我的应用使用员工集合
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @return {@link  List<String>}
     */
    List<String> getUseScopeUserList(String corpId, Integer appid);

    /**
     * 获取我的应用使用
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @return {@link  List<SetApplicationUseScopeDTO.UseScope>}
     */
    List<SetApplicationUseScopeDTO.UseScope> getUseScope(String corpId, Integer appid);



}

