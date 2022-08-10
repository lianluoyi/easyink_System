package com.easywecom.common.enums.moment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名： 朋友圈任务创建状态
 *
 * @author 佚名
 * @date 2022/1/10 16:40
 */
@AllArgsConstructor
@Getter
public enum MomentStatusEnum {

    /**
     * 定时任务未创建
     */
    NOT_START(0,"未创建"),
    /**
     * 开始创建任务
     */
    START(1, "开始创建任务"),
    /**
     * 2表示正在创建任务中
     */
    PROCESS(2, "正在创建任务中"),
    /**
     * 3表示创建任务已完成
     */
    FINISH(3, "创建任务已完成"),
    /**
     * 已删除
     */
    DEL(4,"已删除"),
    ;
    /**
     * 状态码
     */
    public final Integer type;
    /**
     * 含义
     */
    private final String desc;
}
