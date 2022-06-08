package com.easywecom.wecom.domain.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.common.core.domain.wecom.WeUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 通讯录用户列表
 * @author admin
 * @create: 2020-10-17 00:19
 **/
@Data
public class WeUserListDTO extends WeResultDTO {

    private List<WeUserDTO> userlist;


    public List<WeUser> getWeUsers() {
        List<WeUser> weUsers = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userlist)) {
            userlist.stream().forEach(k -> {
                WeUser weUser = new WeUser();
                BeanUtils.copyPropertiesASM(k, weUser);
                weUser.setIsActivate(k.getStatus());
                weUser.setAvatarMediaid(k.getAvatar());
                weUsers.add(weUser);
            });
        }
        return weUsers;
    }

}
