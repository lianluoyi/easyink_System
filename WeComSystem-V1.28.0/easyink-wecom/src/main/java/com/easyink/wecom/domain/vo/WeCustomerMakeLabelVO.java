package com.easyink.wecom.domain.vo;

import com.easyink.wecom.domain.WeTag;
import lombok.Data;

import java.util.List;

@Data
public class WeCustomerMakeLabelVO {
    private String userid;

    private String externalUserid;

    private List<WeTag> addTag;

    private List<WeTag> removeTag;

}
