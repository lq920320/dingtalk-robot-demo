package com.dingtalk.model;

import lombok.Data;

/**
 * 接收到的消息内容
 *
 * @author zetu
 * @date 2022/5/31
 */
@Data
public class MessageContent {
    /**
     * 消息内容
     */
    private String content;
}
