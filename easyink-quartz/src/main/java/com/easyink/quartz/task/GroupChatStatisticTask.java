package com.easyink.quartz.task;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author admin
 * @description 群聊数据统计
 * @date 2021/2/24 0:42
 **/
@Slf4j
@Component("GroupChatStatisticTask")
public class GroupChatStatisticTask {

    private final WeGroupService weGroupService;

    private final WeCorpAccountService weCorpAccountService;

    @Autowired
    public GroupChatStatisticTask(WeGroupService weGroupService, WeCorpAccountService weCorpAccountService) {
        this.weGroupService = weGroupService;
        this.weCorpAccountService = weCorpAccountService;

    }

    public void getGroupChatData() {
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.forEach(weCorpAccount -> {
            if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                weGroupService.processGroupChatData(weCorpAccount.getCorpId());
            }
        });
    }
}
