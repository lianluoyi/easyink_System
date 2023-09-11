package com.easyink.common.utils;

import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.CustomerTrajectoryEnums;

import java.util.List;

/**
 * 信息动态记录标签工具类
 *
 * @author : zhaorui
 * @createTime : 2023/5/24 17:11
 */
public class TagRecordUtil {

    /**
     * 空字符串
     */
    private final static String EMPTY_STRING = "";

    /**
     * 组装员工活码记录文案
     *
     * @param sceneName 活动场景
     * @param userName 员工姓名
     * @return 组装后文案
     */
    public String buildCodeContent(String sceneName, String userName) {
        if (StringUtils.isEmpty(sceneName)){
            sceneName = EMPTY_STRING;
        }
        if (StringUtils.isEmpty(userName)){
            userName = EMPTY_STRING;
        }
        return CustomerTrajectoryEnums.TagType.CODE_TAG.getDesc()
                .replace(GenConstants.ACTIVITY_SCENE, sceneName)
                .replace(GenConstants.USER_NAME, userName);
    }

    /**
     * 组装员工活码记录文案
     *
     * @param sceneName 活动场景
     * @param userName 员工姓名
     * @return 组装后文案
     */
    public String buildAssistantContent(String sceneName, String userName) {
        if (StringUtils.isEmpty(sceneName)){
            sceneName = EMPTY_STRING;
        }
        if (StringUtils.isEmpty(userName)){
            userName = EMPTY_STRING;
        }
        return CustomerTrajectoryEnums.TagType.CUSTOMER_ASSISTANT_TAG.getDesc()
                .replace(GenConstants.ACTIVITY_SCENE, sceneName)
                .replace(GenConstants.USER_NAME, userName);
    }

    /**
     * 组装新客进群打标签记录文案
     *
     * @param codeName 活码名称
     * @param userName 员工姓名
     * @return 组装后文案
     */
    public String buildIntoGroupContent(String codeName, String userName) {
        if (StringUtils.isEmpty(codeName)){
            codeName = EMPTY_STRING;
        }
        if (StringUtils.isEmpty(userName)){
            userName = EMPTY_STRING;
        }
        return CustomerTrajectoryEnums.TagType.NEW_CUSTOMER_GROUP.getDesc()
                .replace(GenConstants.CODE_NAME, codeName)
                .replace(GenConstants.USER_NAME, userName);
    }

    /**
     * 组装查看雷达记录文案
     *
     * @param radarTitle 雷达标题
     * @return 组装后文案
     */
    public String buildRadarContent(String radarTitle) {
        if (StringUtils.isEmpty(radarTitle)){
            radarTitle = EMPTY_STRING;
        }
        return CustomerTrajectoryEnums.TagType.VIEW_RADAR.getDesc()
                .replace(GenConstants.RADAR_TITLE, radarTitle);
    }

    /**
     * 组装表单记录文案
     *
     * @param formName 表单名称
     * @param type 修改类型
     * @return 组装后文案
     */
    public String buildFormContent(String formName ,String type) {
        if (StringUtils.isEmpty(formName)){
            formName = EMPTY_STRING;
        }
        if (CustomerTrajectoryEnums.TagType.SUBMIT_FORM.getType().equals(type)){
            return CustomerTrajectoryEnums.TagType.SUBMIT_FORM.getDesc().replace(GenConstants.FORM_NAME,formName);
        }else {
            return CustomerTrajectoryEnums.TagType.CLICK_FORM.getDesc().replace(GenConstants.FORM_NAME,formName);
        }
    }

    /**
     * 组装自动打标签记录文案
     *
     * @param ruleName 规则名称
     * @return 组装后文案
     */
    public String buildAutoTagContent(String ruleName) {
        if (StringUtils.isEmpty(ruleName)){
            ruleName = EMPTY_STRING;
        }
        return CustomerTrajectoryEnums.TagType.AUTO_TAG.getDesc()
                .replace(GenConstants.RULE_NAME, ruleName);
    }

    /**
     * 组装客户流失记录文案
     *
     * @param userName 员工姓名
     * @return 组装后文案
     */
    public String buildLossTagContent(String userName) {
        if (StringUtils.isEmpty(userName)){
            userName = EMPTY_STRING;
        }
        return CustomerTrajectoryEnums.TagType.LOSS_TAG.getDesc()
                .replace(GenConstants.USER_NAME, userName);
    }

    /**
     * 组装修改标签记录文案
     *
     * @param userName 员工姓名
     * @param type 修改类型
     * @return 组装后文案
     */
    public String buildEditTagContent(String userName,String type) {
        if (StringUtils.isEmpty(userName)){
            userName = EMPTY_STRING;
        }
        if (CustomerTrajectoryEnums.TagType.EDIT_TAG.getType().equals(type)) {
            return CustomerTrajectoryEnums.TagType.EDIT_TAG.getDesc().replace(GenConstants.OPERATOR, userName);
        } else if (CustomerTrajectoryEnums.TagType.BATCH_REMOVE_TAG.getType().equals(type)) {
            return CustomerTrajectoryEnums.TagType.BATCH_REMOVE_TAG.getDesc().replace(GenConstants.OPERATOR, userName);
        } else if (CustomerTrajectoryEnums.TagType.BATCH_TAG_TASK.getType().equals(type)) {
            return CustomerTrajectoryEnums.TagType.BATCH_TAG_TASK.getDesc().replace(GenConstants.CREATE_BY, userName);
        }
        return CustomerTrajectoryEnums.TagType.BATCH_ADD_TAG.getDesc().replace(GenConstants.OPERATOR,userName);
    }



}
