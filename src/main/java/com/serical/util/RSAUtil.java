package com.serical.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA工具类
 *
 * @author serical 2020-04-23 23:53:41
 */
@Slf4j
public class RSAUtil {

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    static {
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            final KeyPair keyPair = generator.genKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用公钥加密数据
     *
     * @param data      数据
     * @param publicKey 公钥
     */
    public static String encrypt(String data, PublicKey publicKey) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        // return HexUtil.encodeHexStr(cipher.doFinal(data.getBytes()));
    }

    /**
     * 使用公钥加密数据
     *
     * @param data      数据
     * @param publicKey 公钥
     */
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 使用私钥解密数据
     *
     * @param data       数据
     * @param privateKey 私钥
     */
    public static String decrypt(String data, PrivateKey privateKey) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        // return new String(cipher.doFinal(HexUtil.decodeHex(data)));
    }

    /**
     * 使用私钥解密数据
     *
     * @param data       数据
     * @param privateKey 私钥
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * base64编码的公钥字符串->PublicKey对象
     *
     * @param publicKeyStr base64编码的公钥字符串
     * @return PublicKey对象
     */
    public static PublicKey getPublicKey(String publicKeyStr) throws GeneralSecurityException {
        final byte[] bytes = Base64.getDecoder().decode(publicKeyStr);
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        final KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(keySpec);
    }

    public static void main(String[] args) throws GeneralSecurityException {
        String data = "你好 rsa";
        final String encrypt = encrypt(data, publicKey);
        log.info("encrypt: {}", encrypt);

        final String decrypt = decrypt(encrypt, privateKey);
        log.info("decrypt: {}", decrypt);
    }
}
