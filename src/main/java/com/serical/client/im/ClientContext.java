package com.serical.client.im;

import com.serical.common.ImUser;

import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端上下文
 *
 * @author serical 2020-05-01 14:19:17
 */
public class ClientContext {

    /**
     * 客户端当前用户信息
     */
    private static ImUser currentUser;

    /**
     * 对方用户公钥
     */
    private static ConcurrentHashMap<String, PublicKey> userPublicKey = new ConcurrentHashMap<>();


    public static ImUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(ImUser currentUser) {
        ClientContext.currentUser = currentUser;
    }

    public static ConcurrentHashMap<String, PublicKey> getUserPublicKey() {
        return userPublicKey;
    }

    public static void setUserPublicKey(ConcurrentHashMap<String, PublicKey> userPublicKey) {
        ClientContext.userPublicKey = userPublicKey;
    }
}
