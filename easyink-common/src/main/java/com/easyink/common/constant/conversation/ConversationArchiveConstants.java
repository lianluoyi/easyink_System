package com.easyink.common.constant.conversation;

import com.easyink.common.utils.StringUtils;

/**
 * 会话存档常量类
 *
 * @author lichaoyu
 * @date 2023/9/18 10:11
 */
public class ConversationArchiveConstants {

    /**
     * 发送状态为已发送
     */
    public static final String ACTION_SEND = "send";

    /**
     * 发送状态为已撤回
     */
    public static final String ACTION_RECALL = "recall";

    /**
     * 查询的结果数量开始位置
     */
    public static final int SEARCH_FROM = 0;

    /**
     * 查询的结果数量最大值（pageSize设置为10000表示全部查询）
     */
    public static final int SEARCH_SIZE = 10000;

    /**
     * 返回的msgId标识符
     */
    public static final String MSG_ID = "msgId";

    /**
     * 查询下文信息标识
     */
    public static final String PRIOR_CONTEXT = "after";

    /**
     * 查询上文信息标识
     */
    public static final String NEXT_CONTEXT = "before";

    /**
     * 默认的查询上下文信息的聊天条数
     */
    public static final int DEFAULT_CONTEXT_NUM = 10;

    /**
     * 分页查询上下文信息的聊天条数
     */
    public static final int PAGE_CONTEXT_NUM = 20;

    /**
     * 系统打招呼消息, 统计需要排序这些消息的计算
     */
    public static String SAY_HI_MSG_CONTENT = "我已经添加了你，现在我们可以开始聊天了。";
    public static String PASS_ADD_CONTACT_MSG_CONTENT = "我通过了你的联系人验证请求，现在我们可以开始聊天了";

    /**
     * 是否客户系统下发的打招呼消息
     * @param content 消息内容
     * @return
     */
    public static boolean isSystemSayHiMessage(String content) {
        if (StringUtils.isBlank(content)) {
            return true;
        }
        return StringUtils.equals(content, SAY_HI_MSG_CONTENT) ||
                StringUtils.equals(content, PASS_ADD_CONTACT_MSG_CONTENT);
    }
}
