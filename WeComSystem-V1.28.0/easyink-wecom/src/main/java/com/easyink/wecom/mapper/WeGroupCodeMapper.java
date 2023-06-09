package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeGroupCode;
import com.easyink.wecom.domain.dto.FindWeGroupCodeDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户群活码Mapper接口
 *
 * @author admin
 * @date 2020-10-07
 */
@Repository
public interface WeGroupCodeMapper extends BaseMapper<WeGroupCode> {
    /**
     * 获得一天内要过期的实际群码
     * @param corpId 企业id
     * @return {@link }
     */
    List<WeGroupCode> listOfExpireGroupCode(@Param("corpId") String corpId);
    /**
     * 查询客户群活码列表
     *
     * @param weGroupCode 客户群活码
     * @return 客户群活码集合
     */
    List<WeGroupCode> selectWeGroupCodeList(FindWeGroupCodeDTO weGroupCode);

    /**
     * 新增客户群活码
     *
     * @param weGroupCode 客户群活码
     * @return 结果
     */
    int insertWeGroupCode(WeGroupCode weGroupCode);

    /**
     * 修改客户群活码
     *
     * @param weGroupCode 客户群活码
     * @return 结果
     */
    int updateWeGroupCode(WeGroupCode weGroupCode);

    /**
     * 根据群活码id获取对应所有群聊信息
     *
     * @param groupCodeId 群活码id
     * @return 结果
     */
    List<WeGroup> selectWeGroupListByGroupCodeId(Long groupCodeId);

    /**
     * 获取群活码的总扫码次数
     *
     * @param groupCodeId 群活码id
     * @return 总扫码次数
     */
    int selectScanTimesByGroupCodeId(Long groupCodeId);

    /**
     * 通过员工活码获取群活码，用于新客自动拉群。
     *
     * @param state 员工活码state
     * @param corpId 企业Id
     * @return 群活码URL
     */
    String selectGroupCodeUrlByEmplCodeState(@Param("state") String state, @Param("corpId") String corpId);

    /**
     * 根据id和corpId获取群url
     *
     * @param id     主键ID
     * @param corpId 企业Id
     * @return
     */
    String getCodeUrlByIdAndCorpId(@Param("id") Long id, @Param("corpId") String corpId);
}
