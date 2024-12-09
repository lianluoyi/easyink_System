package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.WeCustomerAddGroup;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeGroupMember;
import com.easyink.wecom.domain.dto.FindWeGroupDTO;
import com.easyink.wecom.domain.dto.customer.CustomerGroupDetail;
import com.easyink.wecom.domain.dto.customer.CustomerGroupList;
import com.easyink.wecom.domain.vo.sop.GroupSopVO;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 类名： 客户群Service类
 *
 * @author 佚名
 * @date 2021/10/18 10:20
 */
public interface WeGroupService extends IService<WeGroup> {
    /**
     * 群聊信息
     *
     * @param chatName 群聊名称 可以为null
     * @param chatIds  群id
     * @return {@link List<GroupSopVO>}
     */
    List<GroupSopVO> listOfChat(String chatName, List<String> chatIds);

    /**
     * 获取群聊列表
     *
     * @param paramWeGroup 客户群实体类
     * @return 客户群列表
     */
    List<WeGroup> selectWeGroupList(WeGroup paramWeGroup);

    /**
     * 获取数据权限下群主id
     *
     * @param corpId      公司id
     * @param departments 权限下部门
     * @return 员工id
     */
    List<String> listOfOwnerId(String corpId, String[] departments);

    /**
     * 获取群聊列表
     *
     * @param weGroupDTO 客户群实体类
     * @return List<WeGroup> list
     */
    List<WeGroup> list(FindWeGroupDTO weGroupDTO);

    /**
     * 根据企微获取客户群集合
     *
     * @param corpId 企业id，不能为空
     * @param params 含有客户群跟进状态过滤。0 - 所有列表(即不过滤) 1 - 离职待继承 2 - 离职继承中 3 - 离职继承完成
     * @return 客户群的集合类列表
     */
    List<CustomerGroupList.GroupChat> getGroupChats(@NotBlank(message = "企业id不能为空") String corpId, CustomerGroupList.Params params);

    /**
     * 同步群聊
     *
     * @param corpId 企业id
     */
    void syncWeGroup(String corpId);

    /**
     * 创建群聊
     *
     * @param chatId 群id
     * @param corpId 公司id
     */
    void createWeGroup(String chatId, String corpId);

    /**
     * 修改群聊
     *
     * @param corpId 公司id
     * @param chatId 群聊id
     * @return 变动的群成员列表信息,未过滤
     */
    List<WeGroupMember> updateWeGroup(String corpId, String chatId);

    /**
     * 根据群id删除群聊
     *
     * @param chatId 群id
     * @param corpId 企业id
     */
    void deleteWeGroup(String chatId, String corpId);

    /**
     * 获取用户添加的群
     *
     * @param userId         员工id
     * @param externalUserid 客户id
     * @param corpId         公司id
     * @return 客户添加群聊的列表
     */
    List<WeCustomerAddGroup> findWeGroupByCustomer(@NotBlank(message = "userId不能为空") String userId, @NotBlank(message = "externalUserid不能为空") String externalUserid, @NotBlank(message = "corpId不能为空") String corpId);

    /**
     * 根据群聊id查询群聊详情
     *
     * @param chatId 群聊id
     * @param corpId 企业id
     * @return 客户群详情
     */
    CustomerGroupDetail selectWeGroupDetail(String chatId, String corpId);

    /**
     * 根据userId获取群聊列表，会话存档模块内部调用
     *
     * @param userId 群成员id
     * @param corpId 企业id
     * @return 客户群列表
     */
    List<WeGroup> selectWeGroupListByUserid(String userId, String corpId);

    /**
     * 查询群数据（结果数据不带群）
     *
     * @param corpId         [必填]企业ID
     * @param tagIds         [选填]标签列表（多个已逗号隔开）
     * @param includeTagMode 包含标签模式 {@link  TagFilterModeEnum}
     * @param ownerIds       [选填]群主userId（多个已逗号隔开）
     * @param beginTime      [选填]开始时间
     * @param endTime        [选填]结束时间
     * @return List<WeGroup>
     */
    List<WeGroup> listNoRelTag(String corpId, String tagIds, Integer includeTagMode, String ownerIds, String beginTime, String endTime);

    /**
     * 导出客户群信息
     *
     * @param weGroupDTO
     * @return
     */
    AjaxResult export(FindWeGroupDTO weGroupDTO);

    /**
     * 定时任务处理客户群聊统计
     *
     * @param corpId    企业id
     */
    void processGroupChatData(String corpId);
}
