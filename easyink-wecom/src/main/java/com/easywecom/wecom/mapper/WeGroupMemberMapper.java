package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeGroupMember;
import com.easywecom.wecom.domain.dto.FindWeGroupMemberDTO;
import com.easywecom.wecom.domain.dto.WeGroupMemberDTO;
import com.easywecom.wecom.domain.vo.FindWeGroupMemberCountVO;
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
