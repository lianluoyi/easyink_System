package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easywecom.wecom.domain.WeCustomerTrajectory;
import com.easywecom.wecom.domain.WeGroupMember;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.dto.customer.EditCustomerDTO;

import java.util.List;

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
     * @param editTags       {@link List<WeTag>}
     */
    void recordEditTagOperation(String corpId, String userId, String externalUserId, String updateBy, List<WeTag> editTags);


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


}
