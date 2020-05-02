package com.serical.common;

import io.netty.channel.ChannelFuture;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 用户信息
 *
 * @author serical 2020-05-01 13:59:30
 */
@Data
@Builder
public class ImUser implements Serializable {

    private static final long serialVersionUID = 449164126405706101L;

    /**
     * 用户uid
     */
    private String uid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * channel
     */
    private ChannelFuture channelFuture;

    /**
     * 当前用户公钥
     */
    private PublicKey publicKey;

    /**
     * 当前用私钥
     */
    private PrivateKey privateKey;
}
