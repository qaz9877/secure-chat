package com.serical.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
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

    private static String encrypt(String data, PublicKey publicKey) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        // return HexUtil.encodeHexStr(cipher.doFinal(data.getBytes()));
    }

    private static String decrypt(String data, PrivateKey privateKey) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        // return new String(cipher.doFinal(HexUtil.decodeHex(data)));
    }

    public static void main(String[] args) throws GeneralSecurityException {
        String data = "你好 rsa";
        final String encrypt = encrypt(data, publicKey);
        log.info("encrypt: {}", encrypt);

        final String decrypt = decrypt(encrypt, privateKey);
        log.info("decrypt: {}", decrypt);
    }
}
