package com.easyink.common.constant;

import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * ClassName： WeCorpUpdateIdConstants
 *
 * @author wx
 * @date 2022/8/23 15:17
 */
@Data
@ApiModel("企业微信帐号ID安全性全面升级常量")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeCorpUpdateIdConstants {

    /**
     * 存储corpId的表
     *
     */
    public static final String CORP_ID_LIST =
            "    - we_corp_account\n" +
            "    - order_group_to_order_customer\n" +
            "    - order_user_to_order_account\n" +
            "    - sys_logininfor\n" +
            "    - sys_oper_log\n" +
            "    - sys_role\n" +
            "    - sys_role_dept\n" +
            "    - we_auth_corp_info_extend\n" +
            "    - we_auto_tag_customer_scene\n" +
            "    - we_auto_tag_group_scene\n" +
            "    - we_auto_tag_rule\n" +
            "    - we_auto_tag_rule_hit_customer_record\n" +
            "    - we_auto_tag_rule_hit_group_record\n" +
            "    - we_auto_tag_rule_hit_keyword_record\n" +
            "    - we_chat_contact_mapping\n" +
            "    - we_category\n" +
            "    - we_chat_side\n" +
            "    - we_community_new_group\n" +
            "    - we_customer\n" +
            "    - we_customer_extend_property\n" +
            "    - we_customer_extend_property_rel\n" +
            "    - we_customer_messageoriginal\n" +
            "    - we_customer_trajectory\n" +
            "    - we_customer_transfer_config\n" +
            "    - we_customer_transfer_record\n" +
            "    - we_department\n" +
            "    - we_emple_code\n" +
            "    - we_emple_code_analyse\n" +
            "    - we_external_user_mapping_user\n" +
            "    - we_flower_customer_rel\n" +
            "    - we_group\n" +
            "    - we_group_code\n" +
            "    - we_group_member\n" +
            "    - we_group_statistic\n" +
            "    - we_group_tag\n" +
            "    - we_group_tag_category\n" +
            "    - we_group_tag_rel\n" +
            "    - we_leave_user\n" +
            "    - we_material_config\n" +
            "    - we_material_tag\n" +
            "    - we_moment_task\n" +
            "    - we_msg_tlp\n" +
            "    - we_my_application\n" +
            "    - we_my_application_use_scope\n" +
            "    - we_open_config\n" +
            "    - we_operations_center_customer_sop_filter\n" +
            "    - we_operations_center_group_sop_filter\n" +
            "    - we_operations_center_group_sop_filter_cycle\n" +
            "    - we_operations_center_sop\n" +
            "    - we_operations_center_sop_detail\n" +
            "    - we_operations_center_sop_material\n" +
            "    - we_operations_center_sop_rules\n" +
            "    - we_operations_center_sop_scope\n" +
            "    - we_pres_tag_group\n" +
            "    - we_radar\n" +
            "    - we_redeem_code_activity\n" +
            "    - we_resigned_transfer_record\n" +
            "    - we_sensitive\n" +
            "    - we_sensitive_act\n" +
            "    - we_sensitive_act_hit\n" +
            "    - we_sensitive_audit_scope\n" +
            "    - we_tag\n" +
            "    - we_tag_group\n" +
            "    - we_user\n" +
            "    - we_user_behavior_data\n" +
            "    - we_user_role\n" +
            "    - we_words_category\n" +
            "    - we_words_detail\n" +
            "    - we_words_group\n" +
            "    - we_words_last_use\n";
    /**
     * 存储userId的表
     *
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名|条件
     * 要更新的表|修改的列名
     * 要更新的表|修改的列名|条件
     */
    public static final String USER_ID_LIST =
            "    - we_group,owner\n" +
            "    - order_user_to_order_account,user_id\n" +
            "    - we_auto_tag_rule_hit_customer_record_tag_rel,we_auto_tag_rule_hit_customer_record,rule_id,rule_id,user_id\n" +
            "    - we_auto_tag_rule_hit_customer_record,user_id\n" +
            "    - we_auto_tag_rule_hit_keyword_record,user_id\n" +
            "    - we_auto_tag_rule_hit_keyword_record_tag_rel,we_auto_tag_rule_hit_keyword_record,rule_id,rule_id,user_id\n" +
            "    - we_chat_contact_mapping,from_id\n" +
            "    - we_chat_contact_mapping,receive_id\n" +
            "    - we_customer_extend_property_rel,user_id\n" +
            "    - we_customer_trajectory,user_id\n" +
            "    - we_customer_transfer_record,handover_userid\n" +
            "    - we_customer_transfer_record,takeover_userid\n" +
            "    - we_emple_code_analyse,user_id\n" +
            "    - we_external_user_mapping_user,user_id\n" +
            "    - we_flower_customer_rel,user_id\n" +
            "    - we_flower_customer_rel,oper_userid\n" +
            "    - we_group_member,user_id\n" +
            "    - we_group_member,invitor\n" +
            "    - we_leave_user,user_id\n" +
            "    - we_moment_task_result,we_moment_task,moment_task_id,id,user_id\n" +
            "    - we_msg_tlp_scope,we_msg_tlp,msg_tlp_id,id,use_user_id\n" +
            "    - we_operations_center_group_sop_filter,owner\n" +
            "    - we_operations_center_sop_detail,user_id\n" +
            "    - we_pres_tag_group_scope,we_pres_tag_group,task_id,task_id,we_user_id\n" +
            "    - we_pres_tag_group_stat,we_pres_tag_group,task_id,task_id,user_id\n" +
            "    - we_radar_click_record,we_radar,radar_id,id,user_id\n" +
            "    - we_redeem_code_alarm_employee_rel,we_redeem_code_activity,activity_id,id,target_id\n" +
            "    - we_resigned_transfer_record,handover_userid\n" +
            "    - we_resigned_transfer_record,takeover_userid\n" +
            "    - we_sensitive,audit_user_id\n" +
            "    - we_sensitive_act_hit,operator_id\n" +
            "    - we_user,user_id\n" +
            "    - we_user_behavior_data,user_id\n" +
            "    - we_user_role,user_id\n" +
            "    - we_words_last_use,user_id\n" +
            "    - we_emple_code_use_scop,we_emple_code,emple_code_id,id,business_id,business_id_type=2\n" +
            "    - we_my_application_use_scope,val,type=1\n" +
            "    - we_operations_center_sop_scope,target_id,type=2\n" +
            "    - we_sensitive_audit_scope,audit_scope_id,scope_type=2\n" +
            "    - we_words_category,use_range,type=2\n" +
            "    - we_auto_tag_user_rel,we_auto_tag_rule,rule_id,id,target_id,type=2\n" +
            "    - we_auto_tag_rule,create_by\n" +
            "    - we_customer_message,we_customer_messageoriginal,original_id,message_original_Id,create_by\n" +
            "    - we_msg_tlp,create_by\n" +
            "    - we_group_code,create_by\n" +
            "    - we_operations_center_sop,create_by\n" +
            "    - we_radar,create_by\n" +
            "    - we_radar_channel,we_radar,radar_id,id,create_by\n" +
            "    - we_redeem_code_activity,create_by\n" +
            "    - we_moment_user_customer_rel,we_moment_task,moment_task_id,id,user_id\n";
    /**
     * 存储external_userId表
     *
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名|条件
     * 要更新的表|修改的列名
     * 要更新的表|修改的列名|条件
     *
     */
    public static final String EXTERNAL_USER_ID_LIST =
            "    - we_operations_center_sop_detail,target_id\n" +
            "    - we_redeem_code,we_redeem_code_activity,activity_id,id,receive_user_id\n" +
            "    - we_auto_tag_rule_hit_customer_record,customer_id\n" +
            "    - we_auto_tag_rule_hit_customer_record_tag_rel,we_auto_tag_rule_hit_customer_record,rule_id,rule_id,customer_id\n" +
            "    - we_auto_tag_rule_hit_group_record,customer_id\n" +
            "    - we_auto_tag_rule_hit_group_record_tag_rel,we_auto_tag_rule_hit_group_record,rule_id,rule_id,customer_id\n" +
            "    - we_auto_tag_rule_hit_keyword_record,customer_id\n" +
            "    - we_auto_tag_rule_hit_keyword_record_tag_rel,we_auto_tag_rule_hit_keyword_record,rule_id,rule_id,customer_id\n" +
            "    - we_customer,external_userid\n" +
            "    - we_customer_extend_property_rel,external_userid\n" +
            "    - we_customer_trajectory,external_userid\n" +
            "    - we_customer_transfer_record,external_userid\n" +
            "    - we_emple_code_analyse,external_userid\n" +
            "    - we_external_user_mapping_user,external_user_id\n" +
            "    - we_flower_customer_rel,external_userid\n" +
            "    - we_flower_customer_tag_rel,we_flower_customer_rel,flower_customer_rel_id,id,external_userid\n" +
            "    - we_pres_tag_group_stat,we_pres_tag_group,task_id,task_id,external_userid\n" +
            "    - we_radar_click_record,we_radar,radar_id,id,external_user_id\n" +
            "    - we_resigned_customer_transfer_record,we_resigned_transfer_record,record_id,id,external_userid\n" +
            "    - we_moment_user_customer_rel,we_moment_task,moment_task_id,id,external_userid\n";

    /**
     *  代开发应用设置迁移完成openid_type参数
     *  为1时升级userid与corpid 只能同时设置为升级模式
     */
    public static final Integer MIGRATION_USER_ID_AND_CORP_ID = 1;

    /**
     *  代开发应用设置迁移完成openid_type参数
     *  为2时升级external_userid 可以单独升级
     */
    public static final Integer MIGRATION_EXTERNAL_USER_ID = 3;

    /**
     * 空时间
     */
    public static final String EMPTY_TIME = "0000-00-00 00:00:00";

    /**
     * 数字0
     */
    public static final Integer ZERO_NUM = 0;

    /**
     * corpId以wpI开头
     */
    public static final String CORP_START_WITH = "wpI";
    /**
     * 更新企业失败
     */
    public static final String UPDATE_CORP_FAIL = "更新企业失败: ";

    /**
     * 获取corpIdList
     */
    public static final String GET_CORP_ID_LIST = "corpIdList";

    /**
     * 获取corpIdList
     */
    public static final String GET_USER_ID_LIST = "userIdList";

    /**
     * 获取corpIdList
     */
    public static final String GET_EXTERNAL_USER_ID_LIST = "externalUserIdList";

}
