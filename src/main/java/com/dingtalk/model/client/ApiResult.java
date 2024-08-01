package com.dingtalk.model.client;

import lombok.Data;

/**
 * api结果
 *
 * @author liuqian
 * created of 2024/7/26 10:22 for com.dingtalk.model.client
 */
@Data
public class ApiResult<T> {
    private int code;

    private String msg;

    private Long time;

    private T data;
}
