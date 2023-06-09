package com.easyink.wecom.service.idmapping;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.WeExternalUseridMapping;

import java.util.Set;

/**
 * 类名: 客户id映射业务接口
 *
 * @author : silver_chariot
 * @date : 2023/5/29 18:41
 **/
public interface WeExternalUserIdMappingService extends IService<WeExternalUseridMapping> {

    /**
     * 获取并保存 明文密文的external_userid映射关系
     * 1.28 版本更新
     * 由于企微 会话存档的信息 拉取后只有明文的external_userid
     * 但是后面的代开发应用的external_userid都是密文的
     * 而 代开发应用只支持 明文-> 密文的转换
     * 所以 在拉取消息的时候 , 对拉取到的userid 进行转换
     * 然后保存到 we_external_user_mapping_user 映射表中
     * 后续获取会话存档信息的时候 从映射表中先对userid进行转换 ,然后再查询会话记录
     *
     * @param corpId            企业id
     * @param externalUserIdSet 明文的userid 集合set
     */
    void buildAndSave(String corpId, Set<String> externalUserIdSet);

    /**
     * 密文userId 转明文 userId
     *
     * @param corpId             公司id
     * @param openExternalUserId 密文userid
     * @return 明文external userId
     */
    String getUserIdByOpenUserId(String corpId, String openExternalUserId);

    /**
     * 根据明文的客户id 获取密文的 客户id
     *
     * @param corpId         企业id
     * @param externalUserId 明文客户id
     * @return 密文客户id
     */
    String getOpenExternalUserIdByExternalUserId(String corpId, String externalUserId);
}
