package com.easywecom.common.constant;


/**
 * 群活码常量
 *
 * @author tigger
 * 2022/2/10 10:34
 **/
public class GroupCodeConstants {

    /**
     * 群二维码人数上限
     */
    public final static int ACTUAL_GROUP_NUM_LIMIT = 200;
    /**
     * 群二维码人数默认值
     */
    public final static int DEFAULT_GROUP_NUM = 100;

    /**
     * 企业微信群活码人数上限
     */
    public final static int CORP_ACTUAL_GROUP_NUM_LIMIT = 1000;
    /**
     * 企业微信活码人数默认值
     */
    public final static int DEFAULT_CORP_GROUP_NUM = CORP_ACTUAL_GROUP_NUM_LIMIT;

    /**
     * com.easywecom.wecom.domain.WeGroupCodeCorpActual#scene
     */
    public final static int MINIPROGRAM_SCENE = 1;
    public final static int GROUP_SCENE = 2;


    /**
     * 调用企业微信接口生成企业微信群活码最多关联5个群聊
     */
    public static final int CORP_ACTUAL_CODE_REF_GROUP_LIMIT = 5;
}
