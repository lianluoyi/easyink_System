package com.easywecom.wecom.domain.dto.transfer;

import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.CustomerStatusEnum;
import com.easywecom.common.enums.StaffActivateEnum;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.easywecom.wecom.domain.WeFlowerCustomerRel;
import com.easywecom.wecom.domain.resp.WePageBaseResp;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 获取待分配的离职成员列表请求响应实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 15:21
 */
@Data
public class GetUnassignedListResp extends WePageBaseResp<GetUnassignedListResp.UnassignedInfo> {
    // 以下属性由企微API返回

    /**
     * 是否是最后一条记录
     */
    private Boolean is_last;
    /**
     * 离职待分配成员和客户信息
     */
    private List<UnassignedInfo> info;

    // 以下属性由调用handleData后获得
    /**
     * 离职员工userId列表
     */
    private List<WeUser> updateUserList = new ArrayList<>();
    /**
     * 离职员工-客户关系列表
     */
    private List<WeFlowerCustomerRel> relList = new ArrayList<>();


    @Data
    public class UnassignedInfo {
        /**
         * 离职成员的userid
         */
        private String handover_userid;
        /**
         * 外部联系人userid
         */
        private String external_userid;
        /**
         * 成员离职时间
         */
        private Long dimission_time;
    }

    @Override
    public List<UnassignedInfo> getPageList() {
        if (CollectionUtils.isEmpty(info)) {
            return Collections.emptyList();
        }
        return info;
    }

    @Override
    public void handleData(String corpId) {
        if (CollectionUtils.isEmpty(this.info)) {
            return;
        }
        for (UnassignedInfo detail : info) {
            // 构建待更新的离职员工实体
            this.updateUserList.add(
                    WeUser.builder()
                            .corpId(corpId)
                            .userId(detail.getHandover_userid())
                            .isActivate(StaffActivateEnum.DELETE.getCode())
                            .dimissionTime(detail.getDimission_time() == null ? null : DateUtils.unix2Date(detail.getDimission_time()))
                            .build()
            );
            // 构建待接替客户关系实体列表
            this.relList.add(
                    WeFlowerCustomerRel.builder()
                            .id(SnowFlakeUtil.nextId())
                            .externalUserid(detail.getExternal_userid())
                            .userId(detail.getHandover_userid())
                            .corpId(corpId)
                            .status(CustomerStatusEnum.TO_BE_TRANSFERRED.getCode().toString())
                            .build()
            );
        }

    }
}
