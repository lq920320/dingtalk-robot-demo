package com.dingtalk.model;


import lombok.Data;

/**
 * 接收到的消息
 *
 * @author zetu
 * @date 2022/5/31
 */
@Data
public class ReceiveMsg {
    /**
     * 消息发送地址
     */
    private String sessionWebhook;

    /**
     * 发送人
     */
    private String senderStaffId;

    /**
     * 发送人昵称
     */
    private String senderNick;

    /**
     * 消息内容
     */
    private MessageContent text;

    /**
     * 机器人code
     */
    private String robotCode;
}
