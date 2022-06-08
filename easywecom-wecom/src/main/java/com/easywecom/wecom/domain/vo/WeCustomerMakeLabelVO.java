package com.easywecom.wecom.domain.vo;

import com.easywecom.wecom.domain.WeTag;
import lombok.Data;

import java.util.List;

@Data
public class WeCustomerMakeLabelVO {
    private String userid;

    private String externalUserid;

    private List<WeTag> addTag;

    private List<WeTag> removeTag;

}
