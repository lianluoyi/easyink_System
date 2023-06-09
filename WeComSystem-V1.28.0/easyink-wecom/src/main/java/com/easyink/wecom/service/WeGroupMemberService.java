package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeGroupMember;
import com.easyink.wecom.domain.dto.FindWeGroupMemberDTO;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.wecom.domain.vo.FindWeGroupMemberCountVO;

import java.util.List;

/**
 * @author admin
 * @Description:
 * @Date: create in 2020/9/22 0022 0:02
 */
public interface WeGroupMemberService extends IService<WeGroupMember> {

    /**
     * 获取群集合列表
     *
     * @param findWeGroupMemberDTO 客户群搜索条件
     * @return List<WeGroupMember>
     */
    List<WeGroupMember> selectWeGroupMemberList(FindWeGroupMemberDTO findWeGroupMemberDTO);

    /**
     * 统计客户群客户数和员工数
     *
     * @param findWeGroupMemberDTO findWeGroupMemberDTO
     * @return FindWeGroupMemberCountVO
     */
    FindWeGroupMemberCountVO selectWeGroupMemberCount(FindWeGroupMemberDTO findWeGroupMemberDTO);

    /**
     * 根据群聊id获取群成员列表
     * @param chatId 群id
     * @return 群成员列表
     */
    List<WeGroupMemberDTO> selectWeGroupMemberListByChatId(String chatId);


    /**
     * 更新群聊列表
     *
     * @param chatId 群id
     * @param corpId 企业id
     */
    void synchWeGroupMember(String chatId, String corpId);

    /**
     * 批量插入群成员
     *
     * @param list {@link List<WeGroupMember>}
     * @return
     */
    Integer batchInsert(List<WeGroupMember> list);
}
