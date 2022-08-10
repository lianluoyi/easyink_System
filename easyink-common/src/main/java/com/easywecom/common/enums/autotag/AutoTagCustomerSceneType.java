package com.easywecom.common.enums.autotag;

import java.sql.Time;
import java.time.LocalDate;

/**
 * 新客标签规则场景类型枚举
 *
 * @author tigger
 * 2022/3/3 16:01
 **/
public enum AutoTagCustomerSceneType {
    /**
     * 天
     */
    DAY(1) {
        @Override
        public boolean match(Integer loopPoint, Time loopBeginTime, Time loopEndTime, Time now) {

            if (inTime(loopBeginTime, loopEndTime, now)) {
                return true;
            }
            return false;
        }
    },
    /**
     * 周
     */
    WEEK(2) {
        @Override
        public boolean match(Integer loopPoint, Time loopBeginTime, Time loopEndTime, Time now) {
            if (LocalDate.now().getDayOfWeek().getValue() == loopPoint && inTime(loopBeginTime, loopEndTime, now)) {
                return true;
            }
            return false;
        }
    },
    /**
     * 月
     */
    MONTH(3) {
        @Override
        public boolean match(Integer loopPoint, Time loopBeginTime, Time loopEndTime, Time now) {
            if (LocalDate.now().getDayOfMonth() == loopPoint && inTime(loopBeginTime, loopEndTime, now)) {
                return true;
            }
            return false;
        }
    },
    ;

    public boolean inTime(Time loopBeginTime, Time loopEndTime, Time now) {
        return now.compareTo(loopBeginTime) >= 0 && now.compareTo(loopEndTime) <= 0;
    }


    private Integer type;

    AutoTagCustomerSceneType(Integer type) {
        this.type = type;
    }

    public abstract boolean match(Integer loopPoint, Time loopBeginTime, Time loopEndTime, Time now);

    public static AutoTagCustomerSceneType getByType(Integer type) {
        for (AutoTagCustomerSceneType value : AutoTagCustomerSceneType.values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
