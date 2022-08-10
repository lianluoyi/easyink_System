package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeCustomerAddGroup;
import com.easywecom.wecom.domain.WeGroup;
import com.easywecom.wecom.domain.dto.FindWeGroupDTO;
import com.easywecom.wecom.domain.vo.sop.GroupSopVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 * @Description:
 * @Date: create in 2020/9/21 0021 23:58
 */
@Repository
public interface WeGroupMapper extends BaseMapper<WeGroup> {
    /**
     *
     * 查询客户群列表
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
    List<String> listOfOwnerId(@Param("corpId") String corpId, @Param("array") String[] departments);

    /**
     * 查询客户群列表
     *
     * @param findWeGroupDTO 搜索条件
     * @return List<WeGroup>
     */
    List<WeGroup> list(FindWeGroupDTO findWeGroupDTO);

    /**
     * 根据员工id和客户id获取客户添加群聊的集合
     * @param userId    员工id
     * @param externalUserid    客户i
     * @param corpId    企业id
     * @return 客户添加的群列表
     */
    List<WeCustomerAddGroup> findWeGroupByCustomer(@Param("userId") String userId, @Param("externalUserid") String externalUserid, @Param("corpId") String corpId);

    /**
     * 根据userId获取群聊列表数据
     *
     * @param userId 员工id
     * @param corpId 企业id
     * @return  根据员工id获取群聊列表
     */
    List<WeGroup> selectWeGroupListByUserid(@Param("userId") String userId, @Param("corpId") String corpId);

    /**
     * 群聊信息
     *
     * @param chatName 群聊名称
     * @param chatIds  群id
     * @return {@link List<GroupSopVO>}
     */
    List<GroupSopVO> listOfChat(@Param("chatName") String chatName, @Param("list") List<String> chatIds);
}
