package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 新客标签场景表(WeAutoTagCustomerScene)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:30
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagCustomerScene {
    /**
     * 主键id
     */
    private Long id;
    // 企业id
    private String corpId;
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 指定循环节点 周: 1-7 月: 1-月末
     */
    private Integer loopPoint;
    /**
     * 循环指定开始时间
     */
    private String loopBeginTime;
    /**
     * 循环指定结束时间
     */
    private String loopEndTime;
    /**
     * 场景类型 1:天 2:周 3:月
     */
    private Integer sceneType;

    public WeAutoTagCustomerScene(Long id, Integer loopPoint, String loopBeginTime, String loopEndTime, Integer sceneType) {
        this.id = id;
        this.loopPoint = loopPoint;
        this.loopBeginTime = loopBeginTime;
        this.loopEndTime = loopEndTime;
        this.sceneType = sceneType;
    }
}

