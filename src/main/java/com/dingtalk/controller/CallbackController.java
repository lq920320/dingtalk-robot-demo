package com.dingtalk.controller;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.services.DingtalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    @PostMapping("callback")
    public void callback(HttpServletRequest request, @RequestBody JSONObject requestBody) {
        dingtalkService.callback(request, requestBody);
    }
}
