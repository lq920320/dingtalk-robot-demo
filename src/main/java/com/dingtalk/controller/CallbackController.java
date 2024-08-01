package com.dingtalk.controller;


import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.services.DingtalkService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 消息回调接口
 *
 * @author zetu
 * @date 2022/5/22
 */
@RestController
@RequestMapping("api/msg")
public class CallbackController {

    @Autowired
    private DingtalkService dingtalkService;

    @RequestMapping("callback")
    public void callback(HttpServletRequest request, @RequestBody JSONObject requestBody) {
        dingtalkService.callback(request, requestBody);
    }

    @RequestMapping("code/webhook")
    public void webhook(HttpServletRequest request, @RequestBody JSONObject requestBody) {
        dingtalkService.webhook(request, requestBody);
    }
}
