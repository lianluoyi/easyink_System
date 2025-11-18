package com.easyink.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 类名: 客户轨迹枚举
 *
 * @author : silver_chariot
 * @date : 2021/11/19 18:12
 */
public class CustomerTrajectoryEnums {
    /**
     * 类名: 待办事项状态枚举
     *
     * @author : silver_chariot
     * @date : 2021/11/19 14:56
     */
    @AllArgsConstructor
    public enum TodoTaskStatusEnum {
        /**
         * 正常
         */
        NORMAL("0"),
        /**
         * 已通知未完成
         */
        INFORMED("1"),
        /**
         * 已删除
         */
        DEL("2"),
        /**
         * 已完成
         */
        FINISHED("3"),
        ;

        @Getter
        private final String code;
    }

    /**
     * 老客标签建群任务群发类型
     *
     * @author admin
     * @Date 2021/3/24 9:56
     */
    @Getter
    public enum TaskSendType {
        /**
         * 企业群发
         */
        CROP(0, "企业群发"),

        /**
         * 个人群发
         */
        SINGLE(1, "个人群发");

        private final String name;

        private final Integer type;

        TaskSendType(Integer type, String name) {
            this.name = name;
            this.type = type;
        }
    }

    /**
     * 客户轨迹枚举
     */
    @Getter
    public enum Type {


        INFO(1, "信息动态"),
        SOCIAL(2, "社交动态"),
        ACTIVITY(3, "活动轨迹"),
        TO_DO(4, "待办动态");

        private String name;

        private Integer desc;


        Type(Integer desc, String name) {
            this.name = name;
            this.desc = desc;
        }


        public String getName() {
            return name;
        }

        public Integer getDesc() {
            return desc;
        }

    }

    public enum SceneType {
        /**
         * 编辑资料
         */
        EDIT_INFO(Type.INFO.getDesc(), "信息动态-补充资料", 1),
        /**
         * 信息动态,打标签
         */
        MAKE_TAGS(Type.INFO.getDesc(), "信息动态-设置标签", 2);


        private String name;

        private Integer type;


        private Integer key;


        SceneType(Integer type, String name, Integer key) {
            this.name = name;
            this.type = type;
            this.key = key;
        }


        public String getName() {
            return name;
        }

        public Integer getType() {
            return type;
        }

        public Integer getKey() {
            return key;
        }

    }

    /**
     * 客户轨迹：子类型枚举
     */
    @AllArgsConstructor
    public enum SubType {

        /**
         * 修改备注
         */
        EDIT_REMARK("edit_remark", "${operator}修改了备注为："),
        /**
         * 修改标签
         */
        EDIT_TAG("edit_tag", "${operator}修改了客户标签为:"),
        /**
         * 编辑多选框
         */
        EDIT_MULTI("edit_multi", "${operator}修改了${propertyName}为："),
        /**
         * 编辑单选框
         */
        EDIT_CHOICE("edit_choice", "${operator}修改了${propertyName}为："),
        EDIT_LOCATION("edit_location", "${operator}修改了${propertyName}为："),
        /***
         * 编辑图片
         */
        EDIT_PIC("edit_pic", "${operator}修改了${propertyName}为："),
        /**
         * 编辑文件
         */
        EDIT_FILE("edit_file", "${operator}修改了${propertyName}为："),
        /**
         * 添加好友
         */
        ADD_USER("add_user", "${customer}添加了员工${picUrl}${userName}为好友"),
        /**
         * 删除好友
         */
        DEL_USER("del_user", "${customer}删除了员工${picUrl}${userName}"),
        /**
         * 进入群聊
         */
        JOIN_GROUP("join_group", "${customer}加入了群聊【${groupName}】"),
        /**
         * 退出群聊
         */
        QUIT_GROUP("quit_group", "${customer}退出了群聊【${groupName}】"),
        CLICK_RADAR("click_radar", "${customer}打开了雷达链接“${radarTitle}”"),
        CLICK_FORM("click_form","${customer}访问了表单【${formName}】"),
        COMMIT_FORM("commit_form","${customer}提交了表单【${formName}】"),
        UNKNOWN("un_known", ""),
        ;
        /**
         * 子类型标识
         */
        @Getter
        private final String type;
        /**
         * 对应的客户轨迹记录描述
         */
        @Getter
        private final String desc;

        /**
         * 根据类型获取子类型枚举
         *
         * @param type 子类型标识
         * @return 对应的枚举
         */
        public static SubType getByType(String type) {
            return Arrays.stream(values()).filter(a -> a.type.equals(type)).findFirst().orElse(UNKNOWN);
        }

        /**
         * 判断是否是进群/退群操作
         *
         * @param subType 子类型
         */
        public static boolean isGroupOperation(String subType) {
            if (subType == null) {
                return false;
            }
            return subType.equals(JOIN_GROUP.getType())
                    || subType.equals(QUIT_GROUP.getType());
        }

        /**
         * 判断是否是 添加/删除跟进人操作
         *
         * @param subType 子类型
         * @return true or false
         */
        public static boolean isUserOperation(String subType) {
            if (subType == null) {
                return false;
            }
            return subType.equals(ADD_USER.getType())
                    || subType.equals(DEL_USER.getType());
        }


    }

    @AllArgsConstructor
    public enum TagType{

        /**
         * 通过员工活码打标签
         */
        CODE_TAG("code_tag","客户扫描活码\"${activityScene}\"添加员工${userName}，被打上标签："),
        /**
         * 新客进群打标签
         */
        NEW_CUSTOMER_GROUP("newCustomer_group","客户扫描活码\"${codeName}\"添加员工${userName}，被打上标签："),
        /**
         * 查看雷达打标签
         */
        VIEW_RADAR("view_radar","客户查看了雷达链接\"${radarTitle}\"，被打上标签："),
        /**
         * 点击表单打标签
         */
        CLICK_FORM("click_form","客户点击了表单\"${formName}\"，被打上标签："),
        /**
         * 提交表单打标签
         */
        SUBMIT_FORM("submit_form","客户提交了表单\"${formName}\"，被打上标签："),
        /**
         * 自动打标签
         */
        AUTO_TAG("auto_tag","客户触发了自动标签规则\"${ruleName}\"，被打上标签："),
        /**
         * 流失打标签
         */
        LOSS_TAG("loss_tag","客户删除员工${userName}，被打上标签："),
        /**
         * 侧边栏、客户详情页修改标签
         */
        EDIT_TAG("edit_tag","${operator}修改了客户标签为："),
        /**
         * 批量移除标签
         */
        BATCH_REMOVE_TAG("batch_remove_tag","${operator}移除了标签："),
        /**
         * 批量增加标签
         */
        BATCH_ADD_TAG("batch_add_tag","${operator}给客户打上标签："),
        /**
         * 批量打标签任务
         */
        BATCH_TAG_TASK("batch_tag_task", "${createBy}给客户打上标签："),
        /**
         * 通过获客链接打标签
         */
        CUSTOMER_ASSISTANT_TAG("customer_assistant_tag", "客户点击获客链接\"${activityScene}\"添加员工${userName}，被打上标签：")
        ;
        /**
         * 子类型标识
         */
        @Getter
        private final String type;
        /**
         * 对应的客户轨迹记录描述
         */
        @Getter
        private final String desc;


    }
}
