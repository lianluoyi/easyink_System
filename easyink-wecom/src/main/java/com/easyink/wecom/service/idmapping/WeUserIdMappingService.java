package com.easyink.wecom.service.idmapping;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.WeUserIdMapping;

import java.util.List;
import java.util.Set;

/**
 * 类名: userid明文密文映射业务接口
 *
 * @author : silver_chariot
 * @date : 2023/5/23 13:46
 **/
public interface WeUserIdMappingService extends IService<WeUserIdMapping> {


    /**
     * 获取并保存 明文密文的userid映射关系
     * 1.28 版本更新
     * 由于企微 会话存档的信息 拉取后只有明文的userid
     * 但是后面的代开发应用的userid 都是密文的
     * 而 代开发应用只支持 明文-> 密文的转换
     * 所以 在拉取消息的时候 , 对拉取到的userid 进行转换
     * 然后保存到 we_user_id_mapping 映射表中
     * 后续获取会话存档信息的时候 从映射表中先对userid进行转换 ,然后再查询会话记录
     *
     * @param corpId    企业id
     * @param userIdSet 明文的userid 集合set
     */
    void buildAndSaveUserIdMapping(String corpId, Set<String> userIdSet);

    /**
     * 密文userId 转明文 userId
     *
     * @param corpId     公司id
     * @param openUserId 密文userid
     * @return 明文userId
     */
    String getUserIdByOpenUserId(String corpId, String openUserId);

    /**
     * 根据明文获取密文
     *
     * @param corpId 企业id
     * @param userId 明文userId
     * @return 密文userId
     */
    String getOpenUserIdByUserId(String corpId, String userId);

    /**
     * 为代开发应用进行转换 密文- 》 明文
     *
     * @param corpId  企业id
     * @param userIds userid列表
     * @return 明文userid列表
     */
    List<String> transferUserIdsForDK(String corpId, List<String> userIds);
}
