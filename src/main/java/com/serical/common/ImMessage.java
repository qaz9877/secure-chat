package com.serical.common;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息实体
 *
 * @author serical 2020-05-01 13:45:33
 */
@Data
@Builder
public class ImMessage implements Serializable {

    private static final long serialVersionUID = 4248011530026231764L;

    /**
     * 服务端返回消息状态, 0:成功, 其他失败
     */
    private long code;

    /**
     * 发送方
     */
    private String sender;

    /**
     * 发送方用户名
     */
    private String senderName;

    /**
     * 接收方
     */
    private String receiver;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 消息体内容
     */
    private Object message;

    /**
     * 消息创建时间
     */
    private Date createTime;

    @Tolerate
    ImMessage() {

    }
}
