package com.ly.export.utils;

import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import lombok.Data;

/**
 * oss续传对象
 */
@Data
public class OSSAppend {

    private AppendObjectRequest request;
    private AppendObjectResult result;

    public OSSAppend(AppendObjectRequest request, AppendObjectResult result) {
        this.request = request;
        this.result = result;
    }

}
