package com.easywecom.common.enums;

import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 类名：WeOperationsCenterSop 运营中心SOP定义
 *
 * @author Society my sister Li
 * @date 2021-11-30 15:53
 */
public enum WeOperationsCenterSop {

    ;

    //SOP请求类型
    public enum SopTypeEnum {
        //0：定时sop
        TIME_TASK(0, "【群SOP】\nSOP名称：{0}\n发送对象：「{1}」等{2}个客户群\n提醒内容：{3}\n{4}"),
        //1：循环sop
        CYCLE(1,"【群SOP】\nSOP名称：{0}\n发送对象：「{1}」等{2}个客户群\n提醒内容：{3}\n{4}"),
        //2：新客sop
        NEW_CUSTOMER(2,"【新客SOP】\nSOP名称：{0}\n发送对象：「{1}」等{2}个客户\n提醒内容：{3}\n{4}"),
        //3：活动sop
        ACTIVITY(3,"【活动SOP】\nSOP名称：{0}\n发送对象：「{1}」等{2}个客户\n提醒内容：{3}\n{4}"),
        //4：生日sop
        BIRTH_DAT(4,"【生日SOP】\nSOP名称：{0}\n发送对象：「{1}」等{2}个客户\n提醒内容：{3}\n{4}"),
        //5：群日历
        GROUP_CALENDAR(5, "【群日历】\n日历名称：{0}\n发送对象：「{1}」 等{2}个客户群\n提醒内容：{3}\n{4}");

        @Getter
        private Integer sopType;

        @Getter
        private String content;

        SopTypeEnum(Integer sopType, String content) {
            this.sopType = sopType;
            this.content = content;
        }

        /**
         * 是否为客户sop
         *
         * @param sopType sop类型
         * @return true/false
         */
        public static boolean isCustomerSop(Integer sopType) {
            return !TIME_TASK.getSopType().equals(sopType) && !CYCLE.getSopType().equals(sopType) && !GROUP_CALENDAR.getSopType().equals(sopType);
        }

        /**
         * 获取对应sopType的枚举值
         */
        public static SopTypeEnum getSopTypeEnumByType(Integer sopType) {
            if (sopType == null) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }

            for (SopTypeEnum sopTypeEnum : values()) {
                if (sopTypeEnum.getSopType().equals(sopType)) {
                    return sopTypeEnum;
                }
            }
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }


    }

    //群聊过滤类型
    public enum FilterTypeEnum {
        //0：指定群聊
        SPECIFY(0),
        //1：筛选群聊
        FILTER(1);
        @Getter
        private Integer filterType;

        FilterTypeEnum(Integer filterType) {
            this.filterType = filterType;
        }
    }

    //提醒类型
    public enum AlertTypeEnum {
        //0：xx小时xx分钟提醒
        TYPE_0(0, 0, 23, DateUtils.MM),
        //1：xx天xx:xx提醒
        TYPE_1(1, 1, 999, DateUtils.HH_MM),
        //2：每天xx:xx提醒
        TYPE_2(2, null, null, DateUtils.HH_MM),
        //3：每周周x的xx:xx提醒
        TYPE_3(3, 1, 7, DateUtils.HH_MM),
        //4：每月x日xx:xx提醒
        TYPE_4(4, 1, 31, DateUtils.HH_MM),
        //5：具体提醒时间 yyyy-MM-dd HH:mm:dd
        TYPE_5(5, null, null, DateUtils.YYYY_MM_DD_HH_MM),
        //6：提前xx天xx:xx提醒
        TYPE_6(6, 1, 999, DateUtils.HH_MM),
        //7: 生日第**天xx:xx提醒
        TYPE_7(7, 1, 999, DateUtils.HH_MM),
        ;
        @Getter
        private Integer alertType;
        @Getter
        private Integer alertMin;
        @Getter
        private Integer alertMax;
        @Getter
        private String dateFormat;

        AlertTypeEnum(Integer alertType, Integer alertMin, Integer alertMax, String dateFormat) {
            this.alertType = alertType;
            this.alertMin = alertMin;
            this.alertMax = alertMax;
            this.dateFormat = dateFormat;
        }

        /**
         * 校验请求参数格式
         */
        public static void checkParam(Integer alertType, Integer alertData1, String alertData2) {
            if (alertType == null || alertData1 == null || StringUtils.isBlank(alertData2)) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            //校验alertData1、alertData2
            for (AlertTypeEnum alertTypeEnum : values()) {
                if (alertTypeEnum.getAlertType().equals(alertType)) {
                    try {
                        if (!DateUtils.isMatchFormat(alertData2, alertTypeEnum.getDateFormat())) {
                            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
                        }
                    } catch (Exception e) {
                        int mm;
                        try {
                            mm = Integer.parseInt(alertData2);
                        } catch (Exception e1) {
                            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
                        }
                        //判断是否为1分钟后执行
                        if (alertData1 == 0 && mm <= 1) {
                            throw new CustomException(ResultTip.TIP_GENERAL_RESET_TIME);
                        }
                        //使用isMatchFormat方法校验时分钟为个位数的情况下会报错，所以在前面+0
                        String alertData3 = "0" + alertData2;
                        if (!DateUtils.isMatchFormat(alertData3, alertTypeEnum.getDateFormat())) {
                            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
                        }
                    }

                    if (alertTypeEnum.getAlertMin() != null) {
                        if (alertTypeEnum.getAlertMin() > alertData1) {
                            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
                        }
                    }
                    if (alertTypeEnum.getAlertMax() != null) {
                        if (alertTypeEnum.getAlertMax() < alertData1) {
                            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
                        }
                    }
                }
            }
        }

        /**
         * 获取提醒类型枚举值
         *
         * @param alertType alertType
         * @return AlertTypeEnum
         */
        public static AlertTypeEnum getAlertTypeEnumByType(Integer alertType) {
            if (alertType == null) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            for (AlertTypeEnum alertTypeEnum : values()) {
                if (alertTypeEnum.getAlertType().equals(alertType)) {
                    return alertTypeEnum;
                }
            }
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

}
