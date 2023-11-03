package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeGroupMember;
import com.easyink.wecom.domain.dto.FindWeGroupMemberDTO;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.wecom.domain.vo.FindWeGroupMemberCountVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 * @Description:
 * @Date: create in 2020/9/21 0021 23:58
 */
@Repository
public interface WeGroupMemberMapper extends BaseMapper<WeGroupMember> {
    List<WeGroupMemberDTO> selectWeGroupMemberListByChatId(String chatId);

    /**
     * 根据群聊id列表查询群成员信息
     *
     * @param chatIdList 群聊ID列表
     * @return 群聊和群聊的成员信息列表
     */
    List<WeGroupMemberDTO> selectWeGroupMemberListByChatIdList(@Param("chatIdList") List<String> chatIdList);


    /**
     * 根据chat_id、join_scene、type查询群成员数量
     *
     * @param chatIdList 群ID列表
     * @param joinScene  入群方式
     * @param type       群成员类型
     * @return int
     */
    int countWeGroupMemberListByChatIdAndJoinSceneAndType(@Param("chatIdList") List<String> chatIdList, @Param("joinScene") Integer joinScene, @Param("type") Integer type);

    /**
     * 统计客户群的员工数和客户数
     *
     * @param weGroupMember 搜索条件
     * @return FindWeGroupMemberCountVO
     */
    FindWeGroupMemberCountVO selectWeGroupMemberCount(FindWeGroupMemberDTO weGroupMember);

    /**
     * 查询客户群的成员信息
     *
     * @param weGroupMember 搜索条件
     * @return List<WeGroupMember>
     */
    List<WeGroupMember> selectWeGroupMember(FindWeGroupMemberDTO weGroupMember);

    /**
     * 批量插入
     *
     * @param list {@link List<WeGroupMember>}
     * @return affected rows
     */
    Integer batchInsert(@Param("list") List<WeGroupMember> list);
}
