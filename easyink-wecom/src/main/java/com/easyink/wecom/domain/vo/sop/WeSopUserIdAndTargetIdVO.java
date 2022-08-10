package com.easyink.wecom.domain.vo.sop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 类名：WeSopUserIdAndTargetIdVO  查询sop任务下符合条件的数据
 *
 * @author Society my sister Li
 * @date 2021-12-03 11:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeSopUserIdAndTargetIdVO {

    /**
     * 使用者
     */
    private String userId;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 目标名称
     */
    private String targetName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 生日（客户SOP时使用）
     */
    private Date birthday;


    public WeSopUserIdAndTargetIdVO(String userId, String targetId, String targetName, Date createTime) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetName = targetName;
        this.createTime = createTime;
    }
}
