package com.dingtalk.services.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.clients.HuangliOkHttpClient;
import com.dingtalk.model.ReceiveMsg;
import com.dingtalk.model.client.HuangliResult;
import com.dingtalk.services.DingtalkService;
import com.taobao.api.ApiException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;

/**
 * 钉钉机器人接口实现类
 *
 * @author zetu
 * @date 2022/5/22
 */
@Service
@Slf4j
public class DingtalkServiceImpl implements DingtalkService {

    @Resource
    private HuangliOkHttpClient huangliOkHttpClient;

    private final String DATE_PATTERN = "yyyy-MM-dd";

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
        if (receiveContent.contains("黄历")) {
            sendContent = huangliContent(receiveContent);
        }
        try {
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
     * 黄历信息
     *
     * @param content
     * @return {@link String}
     */
    private String huangliContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        Date today = new Date();
        String date = DateFormatUtils.format(today, DATE_PATTERN);
        if (content.contains("明天")) {
            date = DateFormatUtils.format(DateUtils.addDays(today, 1), DATE_PATTERN);
        } else if (content.contains("-")) {
            date = content.replace("黄历", "").trim();
        }
        // 获取星期几
        String weekDay = fetchWeekDay(date);
        HuangliResult huangli = huangliOkHttpClient.getHuangli(date);
        // 构建黄历返回内容
        StringBuilder sendContent = new StringBuilder("\n");
        sendContent.append("日期：").append(huangli.getGregorianDateTime()).append("\n");
        sendContent.append("农历：").append(huangli.getTianGanDiZhiYear())
                .append(huangli.getLYear()).append("年")
                .append(huangli.getLMonth()).append(huangli.getLDay()).append("\n");
        sendContent.append("星期：").append(weekDay).append("\n");
        sendContent.append("节气：")
                .append(StringUtils.isBlank(huangli.getSolarTermName()) ? "-" : huangli.getSolarTermName())
                .append("\n");
        sendContent.append("农历节日：")
                .append(StringUtils.isBlank(huangli.getLJie()) ? "-" : huangli.getLJie())
                .append("\n");
        sendContent.append("公历节日：")
                .append(StringUtils.isBlank(huangli.getGJie()) ? "-" : huangli.getGJie())
                .append("\n");
        sendContent.append("宜：").append(huangli.getYi()).append("\n");
        sendContent.append("忌：").append(huangli.getJi()).append("\n");
        sendContent.append("神位：").append(huangli.getShenWei()).append("\n");
        sendContent.append("岁煞：").append(huangli.getSuiSha()).append("\n");
        sendContent.append("冲煞：").append(huangli.getChong()).append("\n");
        sendContent.append("天干地支月：").append(huangli.getTianGanDiZhiMonth()).append("\n");
        sendContent.append("天干地支日：").append(huangli.getTianGanDiZhiDay()).append("\n");
        sendContent.append("彭祖百忌：").append(huangli.getPengZu()).append("\n");
        sendContent.append("五行甲子：").append(huangli.getWuxingJiazi()).append("\n");
        return sendContent.toString();
    }

    private String fetchWeekDay(String date) {
        Date targetDate = null;
        try {
            targetDate = DateUtils.parseDate(date, DATE_PATTERN);
        } catch (ParseException e) {
            log.error("转换日期错误", e);
        }
        if (Objects.isNull(targetDate)) {
            return "";
        }
        Calendar calendar = DateUtils.toCalendar(targetDate);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        return switch (weekday) {
            case 1 -> "星期日";
            case 2 -> "星期一";
            case 3 -> "星期二";
            case 4 -> "星期三";
            case 5 -> "星期四";
            case 6 -> "星期五";
            case 7 -> "星期六";
            default -> "";
        };
    }

    @Override
    public void webhook(HttpServletRequest request, JSONObject requestBody) {
        // query 参数获取
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("【query参数】：{}", JSON.toJSONString(parameterMap));
        // 获取到请求头里的签名、时间戳
        String requestMethod = request.getMethod();
        String postSign = request.getHeader("Sign");
        String codeEvent = request.getHeader("Codeup-Event");
        log.info("request method is: {}", requestMethod);
        log.info("request sign is: {}", postSign);
        log.info("request codeUpEvent is: {}", codeEvent);
        log.info("The request body is: {}", JSON.toJSONString(requestBody));
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
