package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeGroupCodeActual;
import com.easyink.wecom.domain.vo.WeGroupCodeActualExistVO;

import java.util.List;

/**
 * 实际群码Service接口
 *
 * @author admin
 * @date 2020-10-07
 */
public interface WeGroupCodeActualService extends IService<WeGroupCodeActual> {
    /**
     * 查询实际群码
     *
     * @param id 实际群码ID
     * @return 实际群码
     */
    WeGroupCodeActual selectWeGroupCodeActualById(Long id);

    /**
     * 根据群聊id获取对应群二维码
     *
     * @param chatId 群聊id
     * @return 结果
     */
    WeGroupCodeActual selectActualCodeByChatId(String chatId);

    /**
     * 查询实际群码列表
     *
     * @param weGroupCodeActual 实际群码
     * @return 实际群码集合
     */
    List<WeGroupCodeActual> selectWeGroupCodeActualList(WeGroupCodeActual weGroupCodeActual);

    /**
     * 新增实际群码
     *
     * @param weGroupCodeActual 实际群码
     * @return 结果
     */
    int insertWeGroupCodeActual(WeGroupCodeActual weGroupCodeActual);

    /**
     * 修改实际群码
     *
     * @param weGroupCodeActual 实际群码
     * @return 结果
     */
    int updateWeGroupCodeActual(WeGroupCodeActual weGroupCodeActual);

    /**
     * 批量删除实际群码
     *
     * @param ids 需要删除的实际群码ID
     * @return 结果
     */
    int deleteWeGroupCodeActualByIds(Long[] ids);

    /**
     * 检测实际码chatId是否唯一
     *
     * @param actualList 实际码
     * @param groupId    群活码id
     * @return 结果
     */
    WeGroupCodeActualExistVO checkChatIdUnique(List<WeGroupCodeActual> actualList, Long groupId);

    /**
     * 检测chatId是否唯一(全表)
     *
     * @param chatId 群聊id
     * @param id     实际群码id
     * @return 结果
     */
    int checkChatIdOnly(String chatId, Long id);

    /**
     * 通过群id增加实际群活码扫码入群人数
     *
     * @param chatId          群id
     * @param memberChangeCnt 人数
     */
    void updateScanTimesByChatId(String chatId, Integer memberChangeCnt);

    /**
     * 根据群活码ID查询群详情
     *
     * @param groupCodeId 群活码ID
     * @return List<WeGroupCodeActual>
     */
    List<WeGroupCodeActual> selectByGroupCodeId(Long groupCodeId);


    List<WeGroupCodeActual> addBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, String corpId);

    /**
     * 新增待开发应用企业微信活码
     *
     * @param weGroupCodeActualList
     * @param id
     * @return
     */
    List<WeGroupCodeActual> addThirdWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long id);

    /**
     * 新增自建应用企业微信活码
     *
     * @param weGroupCodeActualList
     * @param groupCodeId
     * @param remoteCall
     * @param corpId
     * @return
     */
    List<WeGroupCodeActual> addInnerWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, Boolean remoteCall, String corpId);

    /**
     * 批量修改
     *
     * @param weGroupCodeActualList
     * @param groupCodeId
     * @param corpId
     * @return
     */
    int editBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, String corpId);

    /**
     * 修改待开发应用企业微信活码
     *
     * @param weGroupCodeActualList
     * @return
     */
    int editThirdWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList);

    /**
     * 修改自建应用企业微信活码
     *
     * @param weGroupCodeActualList
     * @param groupCodeId
     * @param remoteCall
     * @param corpId
     * @return
     */
    int editInnerWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, Boolean remoteCall, String corpId);

    /**
     * 批量删除
     *
     * @param removeIds
     * @param corpId
     * @return
     */
    int removeBatch(List<Long> removeIds, String corpId);

    /**
     * 删除待开发应用企业微信活码
     *
     * @param removeIds 企业微信实际活码id
     * @return
     */
    int removeThirdWeGroupCodeActualByIds(List<Long> removeIds);

    /**
     * 删除自建应用企业微信活码
     *
     * @param removeIds    企业微信实际活码id
     * @param corpId
     * @return
     */
    int removeInnerWeGroupCodeActualByIds(List<Long> removeIds, String corpId);
}
