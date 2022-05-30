package com.dingtalk.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.services.DingtalkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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
        if (currentTimeStamp - postTimestamp >= oneHourTimestamp || StringUtils.equals(sign, postSign)) {
            log.error("Time expired or sign wrong.");
            return;
        }
        // query 参数获取
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("【请求体参数】：{}", JSON.toJSONString(requestBody));
        log.info("【query参数】：{}", JSON.toJSONString(parameterMap));


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
