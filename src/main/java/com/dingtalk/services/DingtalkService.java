package com.dingtalk.services;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO description
 *
 * @author zetu
 * @date 2022/5/22
 */
public interface DingtalkService {
    /**
     * 处理发送的消息请求
     *
     * @param request
     * @param requestBody
     * @author 泽兔
     * @date 2022/5/22 21:40
     */
    void callback(HttpServletRequest request, JSONObject requestBody);
}
