package com.easywecom.common.enums;

import com.easywecom.common.utils.StringUtils;
import lombok.Getter;

import java.util.Arrays;

/**
 * 类名： CustomerTransferStatusEnum 客户接替状态
 *
 * @author 佚名
 * @date 2021/8/30 19:21
 */
@Getter
public enum CustomerTransferStatusEnum {

    // 接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录
    FAIL("接替失败", 0, ""),
    SUCCEED("接替完毕", 1, ""),
    WAIT("等待接替", 2, ""),
    REFUSE("客户拒绝", 3, "customer_refused"),
    CUSTOMER_EXCEED_LIMIT("接替成员客户达到上限", 4, "customer_limit_exceed"),
    NO_RECORD("无接替记录", 5, "");

    /**
     * 描述
     */
    private final String describeType;

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 接替失败原因
     */
    private final String failReason;

    CustomerTransferStatusEnum(String describeType, Integer type, String failReason) {
        this.describeType = describeType;
        this.type = type;
        this.failReason = failReason;
    }

    /**
     * 获取可在在职继承客户列表中显示的接替状态数组
     *
     * @return 可在在职继承列表客户中显示的接替状态数组
     */
    public static Integer[] getTransferAvailTypes() {
        return new Integer[]{
                WAIT.getType(), REFUSE.getType(), CUSTOMER_EXCEED_LIMIT.getType()
        };
    }

    /**
     * 获取详细的失败原因
     *
     * @param failReason 企微返回的失败原因
     * @return 失败原因（中文）
     */
    public static CustomerTransferStatusEnum getByFailReason(String failReason) {
        if (StringUtils.isBlank(failReason)) {
            return null;
        }
        return Arrays.stream(values()).filter(a -> a.getFailReason().equals(failReason)).findFirst().orElse(null);
    }

    /**
     * 更具状态码获取描述
     *
     * @param status 状态码
     * @return 描述
     */
    public static String getDescByStatus(Integer status) {
        if (status == null) {
            return StringUtils.EMPTY;
        }
        CustomerTransferStatusEnum customerTransferStatusEnum = Arrays.stream(values()).filter(a -> a.getType().equals(status)).findFirst().orElse(null);
        if (customerTransferStatusEnum != null) {
            return customerTransferStatusEnum.getDescribeType();
        }
        return StringUtils.EMPTY;
    }


}
