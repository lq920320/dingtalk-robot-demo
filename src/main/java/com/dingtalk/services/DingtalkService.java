package com.dingtalk.services;


import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;


/**
 * 钉钉机器人接口
 *
 * @author zetu
 * @date 2022/5/22
 */
public interface DingtalkService {
    /**
     * 处理发送的消息请求，钉钉机器人回调接口
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

    /**
     * webhook机器人
     *
     * @param request
     * @param requestBody
     * @author 泽兔
     * @date 2022/7/1 11:04
     */
    void webhook(HttpServletRequest request, JSONObject requestBody);

}
