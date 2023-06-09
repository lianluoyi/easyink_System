package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeSensitiveAct;

import java.util.List;

/**
 * @author admin
 * @version 1.0
 * @date 2021/1/12 17:38
 */
public interface WeSensitiveActService extends IService<WeSensitiveAct> {
    /**
     * 查询敏感行为
     *
     * @param id 敏感行为ID
     * @return 敏感行为
     */
    WeSensitiveAct selectWeSensitiveActById(Long id);

    /**
     * 查询敏感行为列表
     *
     * @param weSensitiveAct 敏感行为
     * @return 敏感行为
     */
    List<WeSensitiveAct> selectWeSensitiveActList(WeSensitiveAct weSensitiveAct);

    /**
     * 新增敏感行为
     *
     * @param weSensitiveAct 敏感行为
     * @return 结果
     */
    boolean insertWeSensitiveAct(WeSensitiveAct weSensitiveAct);

    /**
     * 初始化敏感行为
     *
     * @param corpId   企业corpId
     * @param createBy 创建人
     * @return 结果
     */
    boolean initWeSensitiveAct(String corpId, String createBy);

    /**
     * 初始化敏感行为
     *
     * @param corpId 企业corpId
     * @return 结果
     */
    boolean initWeSensitiveAct(String corpId);

    /**
     * 修改敏感行为
     *
     * @param weSensitiveAct 敏感行为
     * @return 结果
     */
    boolean updateWeSensitiveAct(WeSensitiveAct weSensitiveAct);

    /**
     * 批量删除敏感行为
     *
     * @param ids 需要删除的敏感行为ID
     * @return 结果
     */
    boolean deleteWeSensitiveActByIds(Long[] ids);
}
