package com.easywecom.common.constant.redeemcode;

/**
 * ClassName： Constants
 *
 * @author wx
 * @date 2022/7/12 9:41
 */
public class RedeemCodeConstants {

    /**
     * 兑换码锁
     */
    public static String REDEEM_CODE_KEY = "redeemCode";

    public static long CODE_WAIT_TIME = 5L;

    public static long CODE_LEASE_TIME = 5L;

    /**
     * 获取兑换码活动锁，用来处理分配兑换码所引发的并发操作
     *
     * @param corpId
     * @param activityId
     * @return
     */
    public static String getRedeemCodeKey(String corpId, String activityId) {
        return REDEEM_CODE_KEY + ":" + corpId + ":" + activityId;
    }

    /**
     * 兑换码
     */
    public static final String REDEEM_CODE = "#兑换码#";

    /**
     * 告警员工信息模板
     */
    public static final String REDEEM_CODE_ALARM_MESSAGE_INFO = "【兑换码活动库存告警提醒】\n" +
            "\n" +
            "兑换码活动名称：【活动名称】\n" +
            "\n" +
            "兑换码库存数：【库存个数】\n" +
            "\n" +
            "请及时前往该活动中添加库存";

    public static final String REDEEM_CODE_ACTIVITY_NAME = "【活动名称】";

    public static final String REDEEM_CODE_REAMIN_INVENTORY = "【库存个数】";

    /**
     * 兑换码员工告警
     */
    public static final Integer REDEEM_CODE_USER_ALARM = 1;

    /**
     * 兑换码活动限制参与
     */
    public static final Integer REDEEM_CODE_ACTIVITY_LIMITED = 1;

    /**
     * 兑换码已领取
     */
    public static final int REDEEM_CODE_RECEIVED = 1;

    /**
     * 兑换码未领取
     */

    public static final int REDEEM_CODE_NOT_RECEIVED = 0;

    /**
     * 兑换码默认空时间
     */
    public static final String REDEEM_CODE_EMPTY_TIME = "0000-00-00";

}
