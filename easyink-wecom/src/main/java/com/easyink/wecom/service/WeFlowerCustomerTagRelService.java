package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeFlowerCustomerTagRel;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;

import java.util.List;

/**
 * 客户标签关系Service接口
 *
 * @author admin
 * @date 2020-09-19
 */
public interface WeFlowerCustomerTagRelService extends IService<WeFlowerCustomerTagRel> {
    /**
     * 查询客户标签关系
     *
     * @param id 客户标签关系ID
     * @return 客户标签关系
     */
    WeFlowerCustomerTagRel selectWeFlowerCustomerTagRelById(Long id);

    /**
     * 查询客户标签关系列表
     *
     * @param weFlowerCustomerTagRel 客户标签关系
     * @return 客户标签关系集合
     */
    List<WeFlowerCustomerTagRel> selectWeFlowerCustomerTagRelList(WeFlowerCustomerTagRel weFlowerCustomerTagRel);

    /**
     * 新增客户标签关系
     *
     * @param weFlowerCustomerTagRel 客户标签关系
     * @return 结果
     */
    int insertWeFlowerCustomerTagRel(WeFlowerCustomerTagRel weFlowerCustomerTagRel);

    /**
     * 修改客户标签关系
     *
     * @param weFlowerCustomerTagRel 客户标签关系
     * @return 结果
     */
    int updateWeFlowerCustomerTagRel(WeFlowerCustomerTagRel weFlowerCustomerTagRel);

    /**
     * 批量删除客户标签关系
     *
     * @param ids 需要删除的客户标签关系ID
     * @return 结果
     */
    int deleteWeFlowerCustomerTagRelByIds(Long[] ids);

    /**
     * 删除客户标签关系信息
     *
     * @param id 客户标签关系ID
     * @return 结果
     */
    int deleteWeFlowerCustomerTagRelById(Long id);


    /**
     * 批量插入
     *
     * @param weFlowerCustomerTagRels
     * @return
     */
    int batchInsetWeFlowerCustomerTagRel(List<WeFlowerCustomerTagRel> weFlowerCustomerTagRels);

    /**
     * 根据外部联系人ID和成员ID 删除其客户和标签的关系
     *
     * @param externalUserid 外部联系人ID
     * @param userId         成员ID
     * @param tagList        标签ID集合
     * @return affected rows
     */
    Integer removeByCustomerIdAndUserId(String externalUserid, String userId, String corpId, List<String> tagList);

    /**
     * 根据外部联系人ID和成员ID 删除其客户和标签的关系
     *
     * @param externalUserid 外部联系人id
     * @param userId         用户id
     * @param corpId         企业id
     * @return affected rows
     */
    Integer removeByCustomerIdAndUserId(String externalUserid, String userId, String corpId);

    /**
     * 根据关系id删除标签
     *
     * @param relId 客户关系id
     * @return affected rows
     */
    Integer removeByRelId(Long relId);

    /**
     * 批量插入
     *
     * @param tagRelList 客户-标签结合 {@link List<WeFlowerCustomerTagRel>}
     */
    void batchInsert(List<WeFlowerCustomerTagRel> tagRelList);

    /**
     * 接替客户标签
     *
     * @param handoverRelId
     * @param takeoverRelId
     */
    void transferTag(Long handoverRelId, Long takeoverRelId);

    /**
     * 为客户列表设置标签
     *
     * @param corpId 企业id
     * @param list   客户列表 {@link WeCustomerVO }
     */
    void setTagForCustomers(String corpId, List<WeCustomerVO> list);

    /**
     * 从远端同步标签
     * @param localRel 本地的客户关系
     * @param tagRelList 同步到的客户-标签关系
     */
    void syncTagFromRemote(WeFlowerCustomerRel localRel, List<WeFlowerCustomerTagRel> tagRelList);
}
