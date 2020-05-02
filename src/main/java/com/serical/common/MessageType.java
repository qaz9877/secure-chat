package com.serical.common;

import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author serical 2020-05-01 13:48:39
 */
@Getter
public enum MessageType {

    REGISTER(1001, "向服务器注册"),
    ONLINE(1002, "获取在线用户"),
    CONNECT(2001, "建立连接"),
    DISCONNECT(2002, "断开连接"),
    REQUEST_PUBLIC_KEY(3001, "请求公钥"),
    RESPONSE_PUBLIC_KEY(3002, "响应公钥"),
    TEXT_MESSAGE(4001, "文本消息"),
    SYSTEM_MESSAGE(5001, "服务端发到客户端的系统消息"),
    ;

    private int code;
    private String remark;

    MessageType(int code, String remark) {
        this.code = code;
        this.remark = remark;
    }
}
