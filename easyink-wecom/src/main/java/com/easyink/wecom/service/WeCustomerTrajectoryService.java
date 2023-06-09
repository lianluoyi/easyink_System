package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerTrajectory;
import com.easyink.wecom.domain.WeGroupMember;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.customer.EditCustomerDTO;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRule;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecordTagRel;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormOperRecord;
import com.easyink.wecom.domain.entity.radar.WeRadar;
import com.easyink.wecom.domain.vo.autotag.TagRuleListVO;
import io.swagger.annotations.ApiModel;

import java.util.List;
import java.util.Map;

@ApiModel("活动轨迹相关Service")
public interface WeCustomerTrajectoryService extends IService<WeCustomerTrajectory> {

    void waitHandleMsg(String url);

    /**
     * 记录修改客户资料和跟进人备注的操作
     *
     * @param corpId         企业id
     * @param userId         跟进人userID
     * @param externalUserId 客户id
     * @param updateBy       更新人
     * @param dto            {@link EditCustomerDTO }
     */
    void recordEditCustomerOperation(String corpId, String userId, String externalUserId, String updateBy, EditCustomerDTO dto);

    /**
     * 记录修改客户扩展字段的操作
     *
     * @param corpId           企业id
     * @param userId           跟进人userID
     * @param externalUserId   客户id
     * @param updateBy         更新人
     * @param extendProperties {@link  List<BaseExtendPropertyRel>}
     */
    void recordEditExtendPropOperation(String corpId, String userId, String externalUserId, String updateBy, List<BaseExtendPropertyRel> extendProperties);

    /**
     * 记录修改客户标签的操作
     *
     * @param corpId         企业id
     * @param userId         跟进人userID
     * @param externalUserId 客户id
     * @param updateBy       更新人
     */
    void recordEditTagOperation(String corpId, String userId, String externalUserId, String updateBy);


    /**
     * 记录编辑客户操作
     *
     * @param dto {@link EditCustomerDTO } 更新客户信息时传
     */
    void recordEditOperation(EditCustomerDTO dto);

    /**
     * 记录活动轨迹 (加入/退出群聊)
     *
     * @param list    {@link List<WeGroupMember>  } 增量减量的客户群列表
     * @param subType 活动轨迹子类型
     */
    void saveActivityRecord(List<WeGroupMember> list, String subType);

    /**
     * 记录活动轨迹 (添加成员)
     *
     * @param corpId         企业id
     * @param userId         成员id
     * @param externalUserId 客户id
     * @param subType        活动轨迹子类型
     */
    void saveActivityRecord(String corpId, String userId, String externalUserId, String subType);

    /**
     * 轨迹信息
     *
     * @param corpId         企业id
     * @param externalUserid 客户id
     * @param trajectoryType 轨迹类型
     * @param userId         员工id
     * @return {@link List<WeCustomerTrajectory>}
     */
    List<WeCustomerTrajectory> listOfTrajectory(String corpId, String externalUserid, Integer trajectoryType, String userId);

    /**
     * 记录活动轨迹（点击雷达链接)
     *
     * @param radar    雷达{@link WeRadar}
     * @param user     员工 {@link WeUser}
     * @param customer 客户 {@link WeCustomer}
     */
    void recordRadarClickOperation(WeRadar radar, WeUser user, WeCustomer customer);

    /**
     * 记录活动轨迹（提交表单链接)
     *
     * @param weForm           表单{@link WeForm}
     * @param weFormOperRecord 表单操作记录{@link WeFormOperRecord}
     * @param user             员工 {@link WeUser}
     * @param customer         客户 {@link WeCustomer}
     */
    void recordFormCommitOperation(WeForm weForm, WeFormOperRecord weFormOperRecord, WeUser user, WeCustomer customer);

    /**
     * 记录活动轨迹（点击表单链接)
     *
     * @param weForm           表单{@link WeForm}
     * @param weFormOperRecord 表单操作记录{@link WeFormOperRecord}
     * @param user             员工 {@link WeUser}
     * @param customer         客户 {@link WeCustomer}
     */
    void recordFormClickOperation(WeForm weForm, WeFormOperRecord weFormOperRecord, WeUser user, WeCustomer customer);

    /**
     * 记录新客打标签活动轨迹
     *
     * @param corpId 公司id
     * @param userId 员工id
     * @param customerId 客户id
     * @param ruleNameList 规则名列表
     */
    void recordAutoCustomerTag(String corpId,String userId,String customerId,List<TagRuleListVO> ruleNameList);

    /**
     * 记录新客进群打标签活动轨迹
     *
     * @param corpId 公司id
     * @param customerId 客户id
     * @param weUser 员工信息
     * @param weAutoTagRules 自动打标签规则列表
     */
    void recordAutoGroupTag(String corpId, String customerId, WeUser weUser, List<WeAutoTagRule> weAutoTagRules,String chatId);

    /**
     * 记录关键词打标签活动轨迹
     *
     * @param corpId 公司id
     * @param userId 员工id
     * @param customerId 客户id
     * @param ruleIdList 规则id列表
     */
    void recordAutoKeyWordTag(String corpId, String userId, String customerId, List<Long> ruleIdList);

    /**
     * 保存信息动态
     *
     * @param corpId 公司id
     * @param userId 员工id
     * @param customerId 客户id
     * @param content 操作详情
     * @param detail 标签详情
     */
    void saveCustomerTrajectory(String corpId,String userId,String customerId,String content,String detail);
}
