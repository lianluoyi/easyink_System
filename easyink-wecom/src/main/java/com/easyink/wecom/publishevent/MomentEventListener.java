package com.easyink.wecom.publishevent;

import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.entity.moment.WeMomentUserCustomerRelEntity;
import com.easyink.wecom.domain.model.customer.UserIdAndExternalUserIdModel;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.moment.WeMomentUserCustomerRelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 朋友圈监听器
 * @author tigger
 * 2024/6/12 16:58
 **/
@Slf4j
@Component
@AllArgsConstructor
public class MomentEventListener {

    private final WeMomentUserCustomerRelService weMomentUserCustomerRelService;
    private final WeCustomerService weCustomerService;


    @Async(value = "momentSaveCustomerAndUserRefTaskExecutor")
    @TransactionalEventListener
    public void saveMomentCustomerRef(SaveMomentCustomerRefEvent event) {
        if (event == null || event.getMomentId() == null || CollectionUtils.isEmpty(event.getCandidateUserIdList())) {
            log.info("保存朋友圈员工客户关系参数缺失 event： {}", event);
            return;
        }
        log.info("保存员工客户关系, taskId: {}", event.getMomentId());
        List<UserIdAndExternalUserIdModel> userIdAndExternalUserIdModels = weCustomerService.selectUserIdFromRef(
                String.join(",", event.getCandidateUserIdList()),
                event.getTags(),
                event.getCorpId());
        List<WeMomentUserCustomerRelEntity> saveBatchList = userIdAndExternalUserIdModels.stream().map(it -> new WeMomentUserCustomerRelEntity(event.getMomentId(), it.getExternalUserid(), it.getUserId())).collect(Collectors.toList());
        BatchInsertUtil.doInsert(saveBatchList, weMomentUserCustomerRelService::saveBatch);
        log.info("保存员工客户关系, taskId: {}, size: {}", event.getMomentId(), saveBatchList.size());
    }


}
