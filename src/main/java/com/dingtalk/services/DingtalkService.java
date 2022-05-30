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

    /**
     * 发送消息
     *
     * @param requestBody
     * @author 泽兔
     * @date 2022/5/30 14:33
     */
    void sendMsg(JSONObject requestBody);
}
