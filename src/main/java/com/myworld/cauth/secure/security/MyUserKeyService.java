package com.myworld.cauth.secure.security;


import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;


@Service
public class MyUserKeyService {
    private static Logger log = LogManager.getRootLogger();
    //密钥 (需要前端和后端保持一致)
    private static final String CKEY = "myworldmypasskey";
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * username解密并去掉salt
     */
    public static String getRealUsername(String value) {
        value = value.replace(" ", "+");
        log.info("【原始的用户名是: {}】", value);
        value = aesDecrypt(value);
        String one = String.valueOf(value.charAt(0));
        String two = value.substring(2, 4);
        String three = value.substring(6, 9);
        String four = value.substring(12, 16);
        String five = String.valueOf(value.charAt(20));
        log.info("【解密后的用户名是: {}】", one + two + three + four + five);
        return one + two + three + four + five;
    }

    /**
     * password解密并去掉salt
     */
    public static String getRealPassword(String value) {
        value = value.replace(" ", "+");
        log.info("【原始的密码是: {}】", value);
        value = aesDecrypt(value);
        char one = value.charAt(0);
        char two = value.charAt(2);
        char three = value.charAt(5);
        char four = value.charAt(9);
        char five = value.charAt(14);
        char six = value.charAt(20);
        String theRest = value.substring(27);
        return String.valueOf(one) + String.valueOf(two) + String.valueOf(three) + String.valueOf(four) + String.valueOf(five) + String.valueOf(six) + theRest;
    }


    public static String encodeUserIdDetail(String value) {
        log.info("真实的idDetail是：{}", value);
        String thePrefix = value.substring(0, 6);
        String theBody = value.substring(6);

        String one = theBody.substring(0, 3);
        String two = theBody.substring(3, 6);
        String three = theBody.substring(6, 9);
        String four = theBody.substring(9, 12);
        String five = theBody.substring(12, 15);
        String six = theBody.substring(15);
        String[] theBodys = new String[]{one, two, three, four, five, six};
        List<String> bodyList = Arrays.asList(theBodys);

        String theBodyNew = "";
        Random random = new Random();
        for (int i=0; i < 6; i++) {
            String randNum = String.valueOf(random.nextInt(10));
            theBodyNew = theBodyNew + bodyList.get(i) + randNum;
        }
        String userIdDetailSalt = thePrefix + theBodyNew;
        log.info("加盐和拼接后的idDetail是：{}", userIdDetailSalt);
        String userIdDetailCoded = aesEncrypt(userIdDetailSalt);
        log.info("加密后的userIdDetailCoded 是：{}", userIdDetailCoded);
        return userIdDetailCoded;
    }

    public static String decodeUserIdDetail(String value) {
        value = value.replace(" ", "+");
        value = aesDecrypt(value);
        log.info("【解密出来的idDetail是: {}】", value);
        String thePrefix = value.substring(0, 6);
        String theBody = value.substring(6);

        String one = theBody.substring(0, 3);
        String two = theBody.substring(4, 7);
        String three = theBody.substring(8, 11);
        String four = theBody.substring(12, 15);
        String five = theBody.substring(16, 19);
        String six = theBody.substring(20, 22);
        String userIdDetailReal = thePrefix + one + two + three + four + five + six;
        log.info("去掉盐后的userIdDetail是：{}", userIdDetailReal);
        return userIdDetailReal;
    }



    /**
     * aes解密
     */
    public static String aesDecrypt(String encrypt) {
        try {
            return aesDecrypt(encrypt, CKEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }
    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        return StringUtils.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code);
    }
    /**
     * AES解密
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }




    /**
     * aes加密
     */
    public static String aesEncrypt(String content) {
        try {
            return aesEncrypt(content, CKEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }
    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes){
        return Base64.encodeBase64String(bytes);
    }
    /**
     * AES加密
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

}
