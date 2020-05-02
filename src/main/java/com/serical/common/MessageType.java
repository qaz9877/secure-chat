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
    TEXT_MESSAGE(3001, "文本消息"),
    SYSTEM_MESSAGE(4001, "服务端发到客户端的系统消息"),
    ;

    private int code;
    private String remark;

    MessageType(int code, String remark) {
        this.code = code;
        this.remark = remark;
    }
}
