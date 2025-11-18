package com.easyink.wecom.domain.enums.form.push;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送表单记录状态枚举
 *
 * @author easyink
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum FormRecordStatusEnum {
    ;

    /**
     * 推送状态
     */
    @Getter
    public enum PushStatusEnum {

        /**
         * 推送状态
         */
        NOT_PUSHED(0, "未推送"),
        PUSHED_SUCCESS(1, "已推送"),
        PUSHED_FAILED(2, "推送失败"),
        ;

        private final Integer code;
        private final String description;

        PushStatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根据code获取枚举
         */
        public static PushStatusEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (PushStatusEnum pushStatus : PushStatusEnum.values()) {
                if (pushStatus.getCode().equals(code)) {
                    return pushStatus;
                }
            }
            return null;
        }
    }

    /**
     * 推送状态
     */
    @Getter
    public enum TimeoutPushStatusEnum {

        /**
         * 推送状态
         */
        NOT_PUSHED(0, "未推送"),
        PUSHED_FINALLY(1, "推送完成"),
        ;

        private final Integer code;
        private final String description;

        TimeoutPushStatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根据code获取枚举
         */
        public static TimeoutPushStatusEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (TimeoutPushStatusEnum pushStatus : TimeoutPushStatusEnum.values()) {
                if (pushStatus.getCode().equals(code)) {
                    return pushStatus;
                }
            }
            return null;
        }
    }

    /**
     * 推送配置状态枚举
     */
    @Getter
    public enum PushSettingStatusEnum {

        /**
         * 启用状态
         */
        DISABLED(0, "停用"),
        ENABLED(1, "启用"),
        ;

        private final Integer code;
        private final String description;

        PushSettingStatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根据code获取枚举
         */
        public static PushSettingStatusEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (PushSettingStatusEnum settingStatus : PushSettingStatusEnum.values()) {
                if (settingStatus.getCode().equals(code)) {
                    return settingStatus;
                }
            }
            return null;
        }
    }

    /**
     * 提交状态
     */
    @Getter
    public enum SubmitStatusEnum {

        NOT_SUBMITTED(0, "未提交"),
        SUBMITTED(1, "已提交");

        private final Integer code;
        private final String description;

        SubmitStatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根据code获取枚举
         */
        public static SubmitStatusEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (SubmitStatusEnum submitStatus : SubmitStatusEnum.values()) {
                if (submitStatus.getCode().equals(code)) {
                    return submitStatus;
                }
            }
            return null;
        }
    }
    /**
     * 推送来源类型
     */
    @Getter
    public enum PushSceneEnum {

        /**
         * 推送类型
         */
        FORM_SUBMIT(1, "表单提交推送"),
        TIMEOUT_SUBMIT(2, "超时未提交推送"),
        ;

        private final Integer code;
        private final String description;

        PushSceneEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根据code获取枚举
         */
        public static PushSceneEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (PushSceneEnum source : PushSceneEnum.values()) {
                if (source.getCode().equals(code)) {
                    return source;
                }
            }
            return null;
        }
    }

    /**
     * 推送日志状态枚举
     */
    @Getter
    public enum PushLogStatusEnum {

        PUSHING(0, "推送中"),
        SUCCESS(1, "成功"),
        FAILED(2, "失败");

        private final Integer code;
        private final String description;

        PushLogStatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根据code获取枚举
         */
        public static PushLogStatusEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (PushLogStatusEnum pushLogStatus : PushLogStatusEnum.values()) {
                if (pushLogStatus.getCode().equals(code)) {
                    return pushLogStatus;
                }
            }
            return null;
        }
    }

}
