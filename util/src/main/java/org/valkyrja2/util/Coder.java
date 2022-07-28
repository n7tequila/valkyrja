/*
 * PROJECT valkyrja2
 * util/Coder.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 基础加密组件
 *
 * @author Tequila
 * @create 2022/05/20 22:22
 **/
public abstract class Coder {

    /** 当前算法 */
    protected String algorithm;

    /** 秘钥长度 */
    protected int keyLength;

    /** 加密密码对象 */
    protected Cipher encryptCipher;

    /** 解密密码对象 */
    protected Cipher decryptCipher;

    /** 秘钥对象 */
    protected Key key;

    /**
     * 初始化
     *
     * @param algorithm 算法
     * @param key       秘钥
     * @param keyLength 秘钥长度
     * @throws InvalidKeyException 无效key异常
     * @author Tequila
     * @date 2022/05/19 12:05
     */
    protected void init(String algorithm, byte[] key, int keyLength) throws InvalidKeyException {
        this.algorithm = algorithm;
        this.keyLength = keyLength;
        try {
            this.encryptCipher = Cipher.getInstance(algorithm);
            this.decryptCipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(
                    String.format("Can not find algorithm `%s` for Cipher.", algorithm), e
            );
        }
        this.key = bytes2key(key);
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, this.key);
        this.decryptCipher.init(Cipher.DECRYPT_MODE, this.key);
    }

    /**
     * 将byte[]转换成Key对象
     *
     * @param key 秘钥
     * @return {@link Key }
     * @author Tequila
     * @date 2022/05/19 12:06
     */
    protected Key bytes2key(byte[] key) {
        return bytes2key(this.algorithm, key, this.keyLength);
    }

    /**
     * 将byte[]转换成Key对象
     *
     * @param algorithm 算法
     * @param key       key
     * @param keyLength key长度
     * @return {@link Key }
     * @author Tequila
     * @date 2022/05/19 12:07
     */
    protected Key bytes2key(String algorithm, byte[] key, int keyLength) {
        // 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
        byte[] bitKey = Arrays.copyOf(key, keyLength);
        return new SecretKeySpec(bitKey, algorithm);
    }

    /**
     * 更新秘钥
     *
     * @param key key
     * @author Tequila
     * @date 2022/05/19 11:23
     */
    public void updateKey(String key) {
        updateKey(key.getBytes());
    }

    /**
     * 更新秘钥
     *
     * @param key key
     * @author Tequila
     * @date 2022/05/19 11:23
     */
    public void updateKey(byte[] key) {
        this.key = bytes2key(key);
    }
}
