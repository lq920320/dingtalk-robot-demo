package com.dingtalk.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.model.ReceiveMsg;
import com.dingtalk.services.DingtalkService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

/**
 * TODO description
 *
 * @author zetu
 * @date 2022/5/22
 */
@Service
@Slf4j
public class DingtalkServiceImpl implements DingtalkService {

    @Override
    public void callback(HttpServletRequest request, JSONObject requestBody) {
        String postSign = request.getHeader("Sign");
        long postTimestamp = Long.parseLong(request.getHeader("Timestamp"));

        log.info("post sign is: {}", postSign);
        log.info("post timestamp is: {}", postTimestamp);

        long currentTimeStamp = System.currentTimeMillis();
        log.info("current timestamp is: {}", currentTimeStamp);
        String sign = getSign(postTimestamp);

        long oneHourTimestamp = 3600000;
        if (currentTimeStamp - postTimestamp >= oneHourTimestamp || !StringUtils.equals(sign, postSign)) {
            log.error("Time expired or sign wrong.");
            return;
        }
        log.info("【请求体参数】：{}", JSON.toJSONString(requestBody));
        sendMsg(requestBody);
    }

    @Override
    public void sendMsg(JSONObject requestBody) {
        try {
            ReceiveMsg receiveMsg = JSON.parseObject(JSON.toJSONString(requestBody), ReceiveMsg.class);
            String sessionWebhook = receiveMsg.getSessionWebhook();
            String userId = receiveMsg.getSenderStaffId();
            String userNickName = receiveMsg.getSenderNick();
            String receiveContent = receiveMsg.getText().getContent();
            String sendContent = "请说点什么吧！";
            if (StringUtils.isNotBlank(receiveContent)) {
                sendContent = aiContent(receiveContent);
            }
            DingTalkClient client = new DefaultDingTalkClient(sessionWebhook);
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("text");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();

            text.setContent(" @" + userNickName + "  \n  " +
                    sendContent);
            request.setText(text);
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setIsAtAll(false);
            at.setAtUserIds(Collections.singletonList(userId));
            request.setAt(at);
            OapiRobotSendResponse response = client.execute(request);
            System.out.println(response.getBody());
        } catch (ApiException e) {
            log.error("Failed to send msg", e);
        }
    }

    /**
     * AI机器人回答
     *
     * @param content
     * @return {@link String}
     * @author 泽兔
     * @date 2022/5/31 07:10
     */
    private String aiContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        content = content.replace("你", "我");
        content = content.replace("吗", "");
        content = content.replace("?", "!");
        content = content.replace("？", "！");
        return content;
    }

    private String getSign(long postTimestamp) {
        String appSecret = "ivY4D7eVRfs0X8yJQ_s0cLEn7vxq4_6bAYb8XozdIFIxIYjmJyIYOGGOsl_gyBF3";
        String stringToSign = postTimestamp + "\n" + appSecret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = new String(Base64.encodeBase64(signData));
            log.info("generate sign is: {} ", sign);
            return sign;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to generate sign", e);
            return "";
        }
    }
}
