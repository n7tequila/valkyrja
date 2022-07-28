/*
 * PROJECT valkyrja2
 * core/ApiConfig.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.sdk;

import org.valkyrja2.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Properties;


/**
 * api 配置信息
 *
 * @author Tequila
 * @create 2022/07/27 15:55
 **/
public class ApiConfig {

    /** api host */
    private String apiHost;

    /** app id */
    private String appId;

    /** api key */
    private String apiKey;

    /** api secret */
    private String apiSecret;

    /** api aes key */
    private String aesKey;

    public ApiConfig() {
    }

    public ApiConfig(String apiHost, String apiKey, String apiSecret) {
        this.apiHost = apiHost;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public ApiConfig(String apiHost, String appId, String apiKey, String apiSecret) {
        this.apiHost = apiHost;
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public ApiConfig(String apiHost, String appId, String apiKey, String apiSecret, String aesKey) {
        this.apiHost = apiHost;
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.aesKey = aesKey;
    }

    /**
     * 从项目路径中获取配置文件
     *
     * @param propFile 属性文件
     * @author Tequila
     * @date 2022/07/27 16:19
     */
    public void load(String propFile) {
        load(null, propFile);
    }

    /**
     * 从项目路径中获取配置文件
     *
     * @param section  前缀
     * @param propFile 属性文件
     * @author Tequila
     * @date 2022/07/27 16:19
     */
    public void load(String section, String propFile) {
        URL url = ApiConfig.class.getClassLoader().getResource(propFile);
        if (url == null) {
            throw ApiRuntimeException.of(ApiConst.MSG_PROP_FILE_NOT_FOUND, new FileNotFoundException(propFile));
        }

        File f = new File(url.getPath());
        if (!f.exists()) {
            throw ApiRuntimeException.of(ApiConst.MSG_PROP_FILE_NOT_FOUND, new FileNotFoundException(propFile));
        }

        load(section, f);
    }

    /**
     * 从文件载入配置
     *
     * @param file 属性文件
     * @author Tequila
     * @date 2022/07/27 16:20
     */
    public void load(File file) {
        load(null, file);
    }

    /**
     * 从文件载入配置
     *
     * @param section 前缀
     * @param file    属性文件
     * @author Tequila
     * @date 2022/05/04 17:36
     */
    public void load(String section, File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Properties prop = new Properties();
            prop.load(fis);

            this.apiHost = prop.getProperty(getPropKey(section, ApiConst.PROP_API_HOST));
            this.appId = prop.getProperty(getPropKey(section, ApiConst.PROP_APP_ID));
            this.apiKey = prop.getProperty(getPropKey(section, ApiConst.PROP_API_KEY));
            this.apiSecret = prop.getProperty(getPropKey(section, ApiConst.PROP_API_SECRET));
            this.aesKey = prop.getProperty(getPropKey(section, ApiConst.PROP_AES_KEY));
        } catch (Exception e) {
            throw new ApiRuntimeException(ApiConst.MSG_PROP_FILE_LOAD_ERROR, e);
        }
    }


    /**
     * 拼接properties文件键值
     *
     * @param section 前缀
     * @param key     属性key
     * @return {@link String }
     * @author Tequila
     * @date 2022/07/27 16:21
     */
    protected String getPropKey(String section, String key) {
        if (StringUtils.isNotBlank(section)) {
            return String.format("%s.%s", section, key);
        } else {
            return key;
        }
    }

    /**
     * 获得api路径
     *
     * @param api api
     * @return {@link String }
     * @author Tequila
     * @date 2022/07/27 16:55
     */
    public String getApiPath(String api) {
        return apiHost + api;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
}
