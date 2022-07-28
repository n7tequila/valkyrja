/*
 * PROJECT valkyrja2
 * util/AESCoder.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.UUID;


/**
 * AES安全编码组件
 *
 *
 * @version 1.0
 * @since 1.0
 */
public class AESCoder extends Coder {

    /** 加密算法 */
    private static final String ALGORITHM = "AES";

    /** 秘钥长度 */
    private static final int KEY_LENGTH = 16;


    /**
     * 构造函数
     *
     * @author Tequila
     * @date 2022/05/19 11:19
     */
    public AESCoder() {
        super();
    }

    /**
     * 构造函数
     *
     * @param key key
     * @author Tequila
     * @date 2022/05/19 11:19
     */
    public AESCoder(String key) throws InvalidKeyException {
        this(key.getBytes());
    }

    /**
     * 构造函数
     *
     * @param key key
     * @author Tequila
     * @date 2022/05/19 11:19
     */
    public AESCoder(byte[] key) throws InvalidKeyException {
        init(ALGORITHM, key, KEY_LENGTH);
    }

    /**
     * 解密
     *
     * @param data 数据
     * @return {@link byte[] }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/19 11:52
     */
    public byte[] decrypt(byte[] data) throws GeneralSecurityException {
        return decryptCipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data 数据
     * @return {@link byte[] }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/19 12:17
     */
    public byte[] decrypt(String data) throws GeneralSecurityException {
        return decryptCipher.doFinal(data.getBytes(Consts.UTF_8));
    }

    /**
     * 解密
     *
     * @param data 数据
     * @param key  key
     * @return {@link byte[] }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/19 12:14
     */
    public static byte[] decrypt(byte[] data, String key) throws GeneralSecurityException {
        AESCoder coder = new AESCoder(key);

        return coder.decrypt(data);
    }

    /**
     * 解密字符串
     *
     * @param data 数据
     * @param key  key
     * @return {@link String }
     * @throws Exception 异常
     * @author Tequila
     * @date 2022/05/19 12:14
     */
    public static String decryptString(byte[] data, String key) throws GeneralSecurityException {
        return new String(decrypt(data, key), Consts.UTF_8);
    }

    /**
     * 解密base64
     *
     * @param data 数据
     * @param key  key
     * @return {@link byte[] }
     * @throws GeneralSecurityException 异常
     * @author Tequila
     * @date 2022/05/19 12:15
     */
    public static byte[] decryptBase64(String data, String key) throws GeneralSecurityException {
        return decrypt(Base64.decodeBase64(data), key);
    }

    /**
     * 解密base64字符串
     *
     * @param data 数据
     * @param key  key
     * @return {@link String }
     * @throws GeneralSecurityException 异常
     * @author Tequila
     * @date 2022/05/19 12:16
     */
    public static String decryptBase64String(String data, String key) throws GeneralSecurityException {
        return new String(decryptBase64(data, key), Consts.UTF_8);
    }

    /**
     * 加密
     *
     * @param plain 明文
     * @return {@link byte[] }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/19 12:22
     */
    public byte[] encrypt(byte[] plain) throws GeneralSecurityException {
        return encryptCipher.doFinal(plain);
    }

    /**
     * 加密
     *
     * @param plainText 明文文本
     * @return {@link byte[] }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/20 21:58
     */
    public byte[] encrypt(String plainText) throws GeneralSecurityException {
        return encrypt(plainText.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 加密，返回base64字符串
     *
     * @param plain 明文
     * @return {@link String }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/20 22:02
     */
    public String encryptBase64String(byte[] plain) throws GeneralSecurityException {
        byte[] bs = encrypt(plain);
        return Base64.encodeBase64String(bs);
    }

    /**
     * 加密，返回base64字符串
     *
     * @param plainText 明文文本
     * @return {@link String }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/20 22:02
     */
    public String encryptBase64String(String plainText) throws GeneralSecurityException {
        return encryptBase64String(plainText.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 加密
     *
     * @param plain 明文
     * @param key   key
     * @return {@link byte[] }
     * @throws GeneralSecurityException 异常
     * @author Tequila
     * @date 2022/05/19 12:26
     */
    public static byte[] encrypt(byte[] plain, String key) throws GeneralSecurityException {
        AESCoder coder = new AESCoder(key);

        return coder.encrypt(plain);
    }

    /**
     * 加密
     *
     * @param plainText 明文字符串
     * @param key       秘钥
     * @return {@link byte[] }
     * @throws GeneralSecurityException 异常
     * @author Tequila
     * @date 2022/05/19 12:28
     */
    public static byte[] encrypt(String plainText, String key) throws GeneralSecurityException {
        return encrypt(plainText.getBytes(StandardCharsets.UTF_8), key);
    }

    /**
     * 加密并返回base64
     *
     * @param plain 明文
     * @param key 秘钥
     * @return {@link String }
     * @throws GeneralSecurityException 异常
     * @author Tequila
     * @date 2022/05/19 12:28
     */
    public static String encryptBase64(byte[] plain, String key) throws GeneralSecurityException {
        return Base64.encodeBase64String(encrypt(plain, key));
    }

    /**
     * 加密并返回base64
     *
     * @param data 数据
     * @param key  key
     * @return {@link String }
     * @throws GeneralSecurityException 一般安全例外
     * @author Tequila
     * @date 2022/05/19 12:29
     */
    public static String encryptBase64(String data, String key) throws GeneralSecurityException {
        return encryptBase64(data.getBytes(StandardCharsets.UTF_8), key);
    }

    /**
     * 初始化秘钥
     *
     * @param seed 种子
     * @return {@link String }
     * @author Tequila
     * @date 2022/05/19 12:31
     */
    public static String initKey(String seed) {
        try {
            SecureRandom secureRandom;
            if (StringUtils.isNotBlank(seed)) {
                secureRandom = new SecureRandom(seed.getBytes());
            } else {
                secureRandom = new SecureRandom();
            }

            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
            kg.init(secureRandom);

            SecretKey secretKey = kg.generateKey();

            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (Exception e) {
            throw new IllegalStateException("Can not execute init(seed) function.", e);
        }
    }

    /**
     * 生成密钥
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/05/20 22:17
     */
    public static String initKey() {
        return initKey(UUID.randomUUID().toString());
    }

    /**
     * 初始化key
     *
     * @param bit 位，可以是128, 192, 256
     * @return {@link String }
     * @author Tequila
     * @date 2022/05/18 23:58
     */
    public static String initKey(int bit) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
            kg.init(bit);

            SecretKey secretKey = kg.generateKey();

            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (Exception e) {
            throw new IllegalStateException("Can not execute init(seed) function.", e);
        }
    }
}
