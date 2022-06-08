package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeGroupCodeActual;
import com.easywecom.wecom.domain.query.groupcode.GroupCodeDetailQuery;
import com.easywecom.wecom.domain.vo.WeGroupCodeActualExistVO;
import com.easywecom.wecom.domain.vo.groupcode.GroupCodeDetailVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 实际群码Mapper接口
 *
 * @author admin
 * @date 2020-10-07
 */
@Repository
public interface WeGroupCodeActualMapper extends BaseMapper<WeGroupCodeActual> {
    /**
     * 查询实际群码
     *
     * @param id 实际群码ID
     * @return 实际群码
     */
    WeGroupCodeActual selectWeGroupCodeActualById(Long id);

    /**
     * 根据群聊id获取群实际码
     *
     * @param chatId 群聊id
     * @return 结果
     */
    WeGroupCodeActual selectWeGroupCodeActualByChatId(String chatId);

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
     * 删除实际群码
     *
     * @param id 实际群码ID
     * @return 结果
     */
    int deleteWeGroupCodeActualById(Long id);

    /**
     * 批量删除实际群码
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeGroupCodeActualByIds(Long[] ids);

    /**
     * 通过群活码id查询实际码列表
     *
     * @param groupCodeId 群活码id
     * @return 结果
     */
    List<WeGroupCodeActual> selectActualList(Long groupCodeId);

    /**
     * 通过群活码id删除实际码
     *
     * @param groupCodeIds 群活码id列表
     * @return 结果
     */
    int deleteActualListByGroupCodeIds(Long[] groupCodeIds);

    /**
     * 检测chatId是否唯一（不包含该活码下的群）
     *
     * @param actualList 群聊id
     * @param groupId    群活码id
     * @return 结果
     */
    WeGroupCodeActualExistVO checkChatIdUnique(@Param("actualList") List<WeGroupCodeActual> actualList, @Param("groupId") Long groupId);

    /**
     * 检测chatId是否唯一(全表)
     *
     * @param chatId 群聊id
     * @param id     群码id
     * @return 结果
     */
    int checkChatIdOnly(@Param("chatId") String chatId, @Param("id") Long id);


    /**
     * 通过群id增加实际群活码扫码入群人数
     *
     * @param chatId          群id
     * @param memberChangeCnt 人数
     */
    void updateScanTimesByChatId(@Param("chatId") String chatId, @Param("memberChangeCnt") Integer memberChangeCnt);

    /**
     * 根据群活码ID查询实际群信息
     *
     * @param groupCodeId 群活码ID
     * @return List<WeGroupCodeActual>
     */
    List<WeGroupCodeActual> selectByGroupCodeId(@Param("groupCodeId") Long groupCodeId);

    /**
     * 查询客户群活码下的群二维码活码列表
     *
     * @param id 客户群活码id
     * @return
     */
    List<GroupCodeDetailVO> selectGroupActualListWithGroupQr(GroupCodeDetailQuery groupCodeDetailQuery);

    /**
     * 扫码次数+1
     *
     * @param id
     */
    void addScanCodeTimes(@Param("id") Long id);

    /**
     * 更新活码使用状态
     *
     * @param id
     * @param weGroupCodeDisable
     */
    void updateStatus(@Param("id") Long id, @Param("status") Integer weGroupCodeDisable);
}
