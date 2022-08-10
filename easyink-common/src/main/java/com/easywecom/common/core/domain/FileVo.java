package com.easywecom.common.core.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author admin
 * @create: 2021-07-13 15:25
 **/
@Data
@Builder
public class FileVo {
    private String fileName;
    private String url;
}
