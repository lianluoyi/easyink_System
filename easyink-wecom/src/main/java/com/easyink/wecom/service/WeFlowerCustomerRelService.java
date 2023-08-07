package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.customer.resp.GetByUserResp;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 具有外部联系人功能企业员工也客户的关系Service接口
 *
 * @author admin
 * @date 2020-09-19
 */
public interface WeFlowerCustomerRelService extends IService<WeFlowerCustomerRel> {
    /**
     * 删除服务跟进人
     *
     * @param userId         企业成员id
     * @param externalUserid 用户id
     * @param type           删除类型
     */
    Boolean deleteFollowUser(String userId, String externalUserid, String type, String corpId);

    /**
     * 成员添加客户统计
     *
     * @param weFlowerCustomerRel
     * @return Map
     */
    Map<String, Object> getUserAddCustomerStat(WeFlowerCustomerRel weFlowerCustomerRel);

    /**
     * 批量添加或修改客户数据
     *
     * @param weFlowerCustomerRels
     * @return
     */
    int batchUpdateOrInsert(List<WeFlowerCustomerRel> weFlowerCustomerRels);

    /**
     * 获取客户关系
     *
     * @param userId         员工ID
     * @param externalUserid 客户ID
     * @return
     */
    WeFlowerCustomerRel getOne(String userId, String externalUserid, String corpId);

    /**
     * 获取客户最近添加的员工关系
     *
     * @param externalUserid 客户ID
     * @param corpId 企业ID
     * @return 客户-员工关系信息
     */
    WeFlowerCustomerRel getLastUser(String externalUserid, String corpId);

    /**
     * 员工-客户关系：对齐与服务器数据，同步远端数据
     *
     * @param resp   {@link GetByUserResp}
     * @param userId 用户id
     * @param corpId 企业id
     */
    void alignData(GetByUserResp resp, String userId, String corpId);

    /**
     * 分批批量插入
     *
     * @param list {@link List<WeFlowerCustomerRel>}
     */
    void batchInsert(List<WeFlowerCustomerRel> list);

    /**
     * 保存或者修改
     *
     * @param entity {@link WeFlowerCustomerRel}
     * @return update count
     */
    @Override
    boolean saveOrUpdate(WeFlowerCustomerRel entity);

    /**
     * 插入/或更新
     *
     * @param remoteRel
     */
    void insert(WeFlowerCustomerRel remoteRel);

    /**
     * 接替员工的客户资料 （备注信息，扩展字段，标签）
     *
     * @param corpId         公司id
     * @param handoverUserId 原跟进人id
     * @param takeoverUserId 接替人id
     * @param externalUserId 客户userid
     */
    @Transactional(rollbackFor = Exception.class)
    void transferCustomerRel(String corpId, String handoverUserId, String takeoverUserId, String externalUserId);

    /**
     * 客户关系接替
     *
     * @param corpId         企业id
     * @param handoverRelId  原跟进人客户关系id
     * @param takeoverRelId  接替人客户关系id
     * @param takeoverUserId 接替人的用户id
     */
    void transferRel(String corpId, Long handoverRelId, Long takeoverRelId, String takeoverUserId);

    /**
     * 批量更新客户关系
     *
     * @param relList {@link List<WeFlowerCustomerRel>}
     */
    void batchUpdateStatus(List<WeFlowerCustomerRel> relList);

    /**
     * 查询客户所属员工id列表
     *
     * @param customerId 客户id
     * @param corpId 企业id
     * @return
     */
    List<String> listUpUserIdListByCustomerId(String customerId, String corpId);

    /**
     * 更新已流失重新添加回来的客户状态
     *
     * @param corpId 企业ID
     * @param userId 员工ID
     * @param external_userid 外部联系人ID
     * @return 结果
     */
    Integer updateLossExternalUser(String corpId, String userId, String external_userid);

    /**
     * 根据开始，结束时间，获取截止时间下的有效客户数
     *
     * @param corpId    企业ID
     * @param beginTime 开始时间，格式为YYYY-MM-DD 00:00:00
     * @param endTime   结束时间，格式为YYYY-MM-DD 23:59:59
     * @param userIds
     * @return 有效客户数
     */
    Integer getCurrentNewCustomerCnt(String corpId, String beginTime, String endTime, List<String> userIds);
}
