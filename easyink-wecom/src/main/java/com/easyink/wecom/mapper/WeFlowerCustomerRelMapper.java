package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeUserBehaviorData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 具有外部联系人功能企业员工也客户的关系Mapper接口
 *
 * @author admin
 * @date 2020-09-19
 */
@Repository
public interface WeFlowerCustomerRelMapper extends BaseMapper<WeFlowerCustomerRel> {

    /**
     * 新增具有外部联系人功能企业员工也客户的关系
     *
     * @param weFlowerCustomerRel 具有外部联系人功能企业员工也客户的关系
     * @return 结果
     */
    int insertWeFlowerCustomerRel(WeFlowerCustomerRel weFlowerCustomerRel);

    /**
     * 批量删除具有外部联系人功能企业员工也客户的关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeFlowerCustomerRelByIds(Long[] ids);



    /**
     * 批量添加或修改客户关系
     * @param weFlowerCustomerRels
     */
    int myBatchUpdateOrInsert(List<WeFlowerCustomerRel> weFlowerCustomerRels);

    /**
     * 成员添加客户统计
     *
     * @param weFlowerCustomerRel
     * @return
     */
    List<Map<String, Object>> getUserAddCustomerStat(WeFlowerCustomerRel weFlowerCustomerRel);

    /**
     * 批量插入
     *
     * @param list 集合
     * @return 更新行数
     */
    Integer batchInsert(@Param("list") List<WeFlowerCustomerRel> list);

    /**
     * 批量更新或者修改
     *
     * @param entity {@link WeFlowerCustomerRel}
     * @return 修改行数
     */
    Integer saveOrUpdate(WeFlowerCustomerRel entity);

    /**
     * 转接员工客户关系
     *
     * @param corpId         公司ID
     * @param handoverRelId  原跟进人客户关系id
     * @param takeoverRelId  接替人客户关系id
     * @param takeoverUserId
     */
    void transferRel(@Param("corpId") String corpId, @Param("handoverRelId") Long handoverRelId, @Param("takeoverRelId") Long takeoverRelId, @Param("takeoverUserid") String takeoverUserId);

    /**
     * 批量更新状态
     *
     * @param relList {@link List<WeFlowerCustomerRel>}
     */
    void batchUpdateStatus(@Param("list") List<WeFlowerCustomerRel> relList);

    /**
     * 查询员工当天的客户总数和流失客户数
     *
     * @param userId    员工id
     * @param corpId    企业id
     * @param beginTime 时间范围开始时间
     * @param endTime   时间范围结束时间
     * @return
     */
    WeUserBehaviorData getTotalContactAndLossCnt(@Param("userId") String userId,
                                                 @Param("corpId") String corpId,
                                                 @Param("beginTime") String beginTime,
                                                 @Param("endTime") String endTime);

    /**
     * 更新已流失重新添加回来的客户状态
     *
     * @param corpId          企业ID
     * @param userId          员工ID
     * @param external_userid 外部联系人ID
     * @return 结果
     */
    Integer updateLossExternalUser(@Param("corpId") String corpId, @Param("userId") String userId, @Param("external_userid") String external_userid);

    /**
     * 根据unionId 查询客户
     *
     * @param corpId   企业ID
     * @param unionIds unionId 列表
     * @return 客户关系列表 {@link WeFlowerCustomerRel }
     */
    List<WeFlowerCustomerRel> getByUnionIds(@Param("corpId") String corpId, @Param("unionIds") List<String> unionIds);

    /**
     * 根据备注手机号 查询客户
     *
     * @param corpId  企业ID
     * @param mobiles 手机号列表
     * @return 客户关系列表 {@link WeFlowerCustomerRel }
     */
    List<WeFlowerCustomerRel> getByMobiles(@Param("corpId") String corpId, @Param("mobiles") List<String> mobiles);
}
