package com.myworld.cauth.secure.security;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class TokenKeyGenerator {

    //加载myworld.jks文件
    private static InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("myworld.jks");
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    static {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, "myworldpass".toCharArray());
            privateKey = (PrivateKey) keyStore.getKey("myworld", "myworldpass".toCharArray());
            publicKey = keyStore.getCertificate("myworld").getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TokenKeyGenerator() {
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }

}
