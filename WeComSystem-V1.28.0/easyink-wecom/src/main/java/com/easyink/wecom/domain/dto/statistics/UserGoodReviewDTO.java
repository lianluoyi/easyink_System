package com.easyink.wecom.domain.dto.statistics;

import com.easyink.common.core.domain.RootEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 员工好评DTO
 *
 * @author wx
 * 2023/2/15 11:46
 **/
@Data
@NoArgsConstructor
public class UserGoodReviewDTO extends RootEntity {
    /**
     * userId
     */
    private String userId;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 评价人数
     */
    private Integer num;

    /**
     * 评价时间
     */
    private Date createTime;

}
