package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeFlowerCustomerTagRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户标签关系Mapper接口
 *
 * @author admin
 * @date 2020-09-19
 */
@Repository
public interface WeFlowerCustomerTagRelMapper extends BaseMapper<WeFlowerCustomerTagRel> {
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
     * 删除客户标签关系
     *
     * @param id 客户标签关系ID
     * @return 结果
     */
    int deleteWeFlowerCustomerTagRelById(Long id);

    /**
     * 批量删除客户标签关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeFlowerCustomerTagRelByIds(Long[] ids);


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
     * @param tagIdList      标签ID
     * @return
     */
    Integer removeByCustomerIdAndUserId(@Param("externalUserid") String externalUserid, @Param("userId") String userId, @Param("corpId") String corpId, @Param("list") List<String> tagIdList);

    /**
     * 批量插入/更新
     *
     * @param list 集合
     */
    Integer batchInsert(@Param("list") List<WeFlowerCustomerTagRel> list);

    /**
     * 根据客户关系id删除标签
     *
     * @param relId 客户关系id
     * @return
     */
    Integer removeByRelId(@Param("relId") Long relId);

    /**
     * 接替客户标签
     *
     * @param handoverRelId 原跟进人客户关系id
     * @param takeoverRelId 接替人 客户关系id
     */
    void transferTag(@Param("handoverRelId") Long handoverRelId, @Param("takeoverRelId") Long takeoverRelId);
}
