package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeGroupCode;
import com.easywecom.wecom.domain.WeGroupCodeActual;
import com.easywecom.wecom.domain.dto.FindWeGroupCodeDTO;
import com.easywecom.wecom.domain.query.groupcode.GroupCodeDetailQuery;
import com.easywecom.wecom.domain.vo.groupcode.GroupCodeActivityFirstVO;
import com.easywecom.wecom.domain.vo.groupcode.GroupCodeDetailVO;

import java.util.List;

/**
 * 客户群活码Service接口
 *
 * @author admin
 * @date 2020-10-07
 */
public interface WeGroupCodeService extends IService<WeGroupCode> {

    /**
     * 保存客户群活码
     *
     * @param weGroupCode 客户群活码
     */
    void add(WeGroupCode weGroupCode);

    /**
     * 根据群活码id查询实际码列表
     *
     * @param groupCodeId 群活码id
     * @return 结果
     */
    List<WeGroupCodeActual> selectActualList(Long groupCodeId);

    /**
     * 查询客户群活码列表
     *
     * @param weGroupCode 客户群活码
     * @return 客户群活码集合
     */
    List<WeGroupCode> selectWeGroupCodeList(FindWeGroupCodeDTO weGroupCode);

    /**
     * 查询含有过期客户群的活码
     *
     * @param corpId 企业id
     * @return 客户群活码集合
     */
    List<WeGroupCode> selectExpireCode(String corpId);

    /**
     * 修改客户群活码
     *
     * @param weGroupCode 客户群活码
     * @return 结果
     */
    int updateWeGroupCode(WeGroupCode weGroupCode);

    /**
     * 批量删除客户群活码
     *
     * @param ids 需要删除的客户群活码ID
     * @return 结果
     */
    int remove(Long[] ids);

    /**
     * 检测活码名称是否被占用
     *
     * @param weGroupCode 活码对象
     * @return 结果
     */
    boolean isNameOccupied(WeGroupCode weGroupCode);

    /**
     * 通过员工活码获取群活码，用于新客自动拉群。
     *
     * @param state  员工活码state
     * @param corpId 企业Id
     * @return 群活码URL
     */
    String selectGroupCodeUrlByEmplCodeState(String state, String corpId);

    /**
     * 根据id和corpId获取群url
     *
     * @param id     主键ID
     * @param corpId 企业ID
     * @return String code_url
     */
    String getCodeUrlByIdAndCorpId(Long id, String corpId);

    /**
     * 更新客户群活码
     *
     * @param weGroupCode
     */
    int edit(WeGroupCode weGroupCode);

    /**
     * 查询客户群码详情 -> 实际是查实际码的列表
     *
     * @param groupCodeDetailQuery
     * @param createType
     * @return
     */
    List<GroupCodeDetailVO> getGroupCodeDetail(GroupCodeDetailQuery groupCodeDetailQuery, Integer createType);

    /**
     * 获取可用的企业微信实际码
     *
     * @param id 客户群id
     * @param groupCode
     */
    GroupCodeActivityFirstVO doGetActual(Long id, WeGroupCode groupCode);

}
