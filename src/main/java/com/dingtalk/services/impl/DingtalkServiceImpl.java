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
        log.info("【请求体参数】：{}", JSON.toJSONString(requestBody));
        // 获取到请求头里的签名、时间戳
        String postSign = request.getHeader("Sign");
        long postTimestamp = Long.parseLong(request.getHeader("Timestamp"));
        log.info("post sign is: {}", postSign);
        log.info("post timestamp is: {}", postTimestamp);

        // 获取当前时间戳
        long currentTimeStamp = System.currentTimeMillis();
        log.info("current timestamp is: {}", currentTimeStamp);
        // 生成签名
        String sign = getSign(postTimestamp);

        long oneHourTimestamp = 3600000;
        // 这里用来校验接收到的消息是否过期（超过1个小时）或者请求的签名不对
        if (currentTimeStamp - postTimestamp >= oneHourTimestamp || !StringUtils.equals(sign, postSign)) {
            log.error("Time expired or sign wrong.");
            return;
        }
        // 发送消息
        sendMsg(requestBody);
    }

    @Override
    public void sendMsg(JSONObject requestBody) {
        try {
            // 这里我创建了一个实体类来接收转换所需的数据，如果你不想，也可以直接解析这个JSONObject
            ReceiveMsg receiveMsg = JSON.parseObject(JSON.toJSONString(requestBody), ReceiveMsg.class);
            // 获取到sessionWebhook
            String sessionWebhook = receiveMsg.getSessionWebhook();
            // 获取到senderStaffId
            String userId = receiveMsg.getSenderStaffId();
            // 获取到接收到的内容
            String receiveContent = receiveMsg.getText().getContent();
            // 定义要发送的内容
            String sendContent = "请说点什么吧！";
            if (StringUtils.isNotBlank(receiveContent)) {
                sendContent = aiContent(receiveContent);
            }
            // 根据sessionWebhook构建一个客户端
            DingTalkClient client = new DefaultDingTalkClient(sessionWebhook);
            // 构建请求参数
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            // 类型使用"text"
            request.setMsgtype("text");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            // 设置文本类型的消息内容
            text.setContent(sendContent);
            request.setText(text);
            request.setFeedCard(receiveContent);
            // 选择你要@的成员，这里设置isAtAll为false，即不@全员
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setIsAtAll(false);
            at.setAtUserIds(Collections.singletonList(userId));
            request.setAt(at);
            // 执行请求，这样就可以@成员，并发送消息了
            client.execute(request);
        } catch (ApiException e) {
            log.error("Failed to send msg", e);
        }
    }

    /**
     * AI机器人回答
     *
     * @param content
     * @return {@link String}
     */
    private String aiContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        content = content.replace("你", "");
        content = content.replace("我", "");
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
