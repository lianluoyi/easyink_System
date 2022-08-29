package com.easyink.wecom.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ClassName： WeCorpUpdateId
 *
 * @author wx
 * @date 2022/8/23 14:43
 */
@Data
@TableName("we_corp_update_id")
@AllArgsConstructor
public class WeCorpUpdateId{
    /**
     * '未升级的corp_id'
     */
    @TableField("corp_id")
    private String corpId;
    /**
     * '升级后的corp_id'
     */
    @TableField("open_corpid")
    private String openCorpId;
    /**
     * '升级userid个数'
     */
    @TableField("userid_num")
    private Integer userIdNum;
    /**
     * '升级external_userid个数'
     */
    @TableField("external_userid_num")
    private Integer externalUseridNum;
    /**
     * '升级日志'
     */
    @TableField("update_log")
    private String updateLog;
    /**
     * 升级开始时间
     */
    @TableField("create_time")
    private String createTime;
    /**
     * '升级结束时间'
     */
    @TableField("end_time")
    private String endTime;
}