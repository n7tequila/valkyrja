/*
 * PROJECT valkyrja2
 * core/AbstractApiClient.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.sdk;

import com.fasterxml.jackson.core.JacksonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.valkyrja2.mvc.sdk.ApiConst.*;

/**
 * api client对象
 *
 * @author Tequila
 * @create 2022/07/27 15:49
 **/
public abstract class AbstractApiClient {

    private ApiConfig apiConfig;

    protected AbstractApiClient() {

    }

    protected AbstractApiClient(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    protected AbstractApiClient(String apiHost, String apiKey, String apiSecret) {
        this.apiConfig = new ApiConfig(apiHost, apiKey, apiSecret);
    }

    protected AbstractApiClient(String apiHost, String appId, String apiKey, String apiSecret) {
        this.apiConfig = new ApiConfig(apiHost, appId, apiKey, apiSecret);
    }

    protected AbstractApiClient(String apiHost, String appId, String apiKey, String apiSecret, String aesKey) {
        this.apiConfig = new ApiConfig(apiHost, appId, apiKey, apiSecret, aesKey);
    }


    /**
     * 调用api
     *
     * @param api        api
     * @param funcName   函数名字
     * @param params     参数个数
     * @param returnType 返回类型
     * @return {@link T }
     * @author Tequila
     * @date 2022/07/27 16:59
     */
    protected <T extends ApiBaseResponse<?>> T callAPI(String api, String funcName, Map<String, String> params, Class<T> returnType) {
        String apiUrl = apiConfig.getApiPath(api);
        logInfo(funcName, S_INPUT, params);
        HttpResult httpResult;
        try {
            httpResult = HttpUtils.doPostReturnHttpResult(apiUrl, null, params);
        } catch (IOException e) {
            logError(funcName, S_ERROR, params, e);
            throw new ApiRuntimeException(msgCallApiError(apiUrl), e);
        }
        return handleHttpResult(httpResult, apiUrl, funcName, returnType);
    }

    /**
     * 调用API
     *
     * @param api        api
     * @param funcName   函数名字
     * @param reqJson    要求json
     * @param returnType 返回类型
     * @return {@link T }
     * @author Tequila
     * @date 2022/07/27 17:24
     */
    protected <T extends ApiBaseResponse<?>> T callJsonAPI(String api, String funcName, Map<String, String> params, String reqJson, Class<T> returnType) {
        String apiUrl = apiConfig.getApiPath(api);
        logInfo(funcName, S_INPUT, reqJson);
        HttpResult httpResult;
        try {
            httpResult = HttpUtils.doPostJsonReturnHttpResult(apiUrl, null, reqJson);
        } catch (IOException e) {
            logError(funcName, S_ERROR, reqJson, e);
            throw new ApiRuntimeException(msgCallApiError(apiUrl), e);
        }
        return handleHttpResult(httpResult, apiUrl, funcName, returnType);
    }

    /**
     * 调用带有文件的API
     *
     * @param api        api
     * @param funcName   函数名字
     * @param params     参数个数
     * @param files      文件
     * @param returnType 返回类型
     * @return {@link T }
     * @author Tequila
     * @date 2022/07/27 17:26
     */
    protected <T extends ApiBaseResponse<?>> T callFileAPI(String api, String funcName, Map<String, String> params, Map<String, byte[]> files, Class<T> returnType) {
        String apiUrl = apiConfig.getApiPath(api);
        HttpResult httpResult;
        try {
            List<TripleEntry<String, Object, String>> parts = new ArrayList<>();

            params.forEach((k, v) -> parts.add(new TripleEntry<>(k, v)));
            files.forEach((k, v) -> parts.add(new TripleEntry<>(k, v)));

            logInfo(funcName, S_OUTPUT, params);
            httpResult = HttpUtils.doPostMultipartReturnHttpResult(apiUrl, null, parts);
        } catch (IOException e) {
            logError(funcName, S_ERROR, params, e);
            throw new ApiRuntimeException(msgCallApiError(apiUrl), e);
        }

        return handleHttpResult(httpResult, apiUrl, funcName, returnType);
    }


    /**
     * 处理http返回结果
     *
     * @param httpResult http结果
     * @param apiUrl     api url
     * @param funcName   函数名字
     * @param returnType 返回类型
     * @return {@link T }
     * @author Tequila
     * @date 2022/07/27 17:28
     */
    protected <T extends ApiBaseResponse<?>> T handleHttpResult(HttpResult httpResult, String apiUrl, String funcName, Class<T> returnType) {
        String json = httpResult.getContentString();
        if (httpResult.getHttpStatus().is2xxSuccessful()) {
            logInfo(funcName, S_OUTPUT, json);
        } else {
            logError(funcName, S_ERROR, httpResult.toString(), null);
        }
        try {
            return json2response(json, returnType);
        } catch (ApiResponseParseException e) {
            throw new ApiRuntimeException(msgCallApiError(apiUrl), new UnknownServiceException(httpResult.toString()));
        }
    }

    /**
     * 将json转换成返回对象
     *
     * @param json       json
     * @param returnType 返回类型
     * @return {@link T }
     * @throws ApiResponseParseException api响应解析异常
     * @author Tequila
     * @date 2022/07/27 17:09
     */
    @SuppressWarnings("unchecked")
    protected <T extends ApiBaseResponse<?>> T json2response(String json, Class<T> returnType) throws ApiResponseParseException {
        T response;
        try {
            response = Jackson2Utils.json2obj(json, returnType);
        } catch (Exception e1) {
            getLogger().warn("Can not parse response json to {}", returnType, e1);
            try {
                Map<String, Object> map = Jackson2Utils.json2obj(json, HashMap.class, String.class, Object.class);
                response = BeanUtils.map2bean(map, returnType);
            } catch (InstantiationException | IllegalAccessException | IOException | InvocationTargetException e2) {
                e1.addSuppressed(e2);
                throw new ApiResponseParseException(e1);
            }
        }

        return response;
    }

    /**
     * obj2json
     *
     * @param output       输出
     * @param ignoreFields 忽略字段
     * @return {@link String }
     * @author Tequila
     * @date 2022/07/27 17:29
     */
    private static String obj2json(Object output, String...ignoreFields) {
        if (output instanceof String) {
            if (((String) output).length() > 1024) {
                return ((String) output).substring(0, 1024);
            } else {
                return (String) output;
            }
        }
        String s;
        try {
            s = Jackson2Utils.obj2jsonExcludeFields(output, ignoreFields);
        } catch (JacksonException e) {
            try {
                s = Jackson2Utils.obj2json(output);
            } catch (JacksonException ex) {
                s = "";
            }
        }

        return s;
    }

    public void logDebug(String funcName, String comment, Object output, String...ignoreFields) {
        if (getLogger().isDebugEnabled()) {
            String msg = String.format(LOG_FORMAT, funcName, comment, obj2json(output, ignoreFields));
            getLogger().debug(msg);
        }
    }

    public void logInfo(String funcName, String comment, Object output, String...ignoreFields) {
        if (getLogger().isInfoEnabled()) {
            String msg = String.format(LOG_FORMAT, funcName, comment, obj2json(output, ignoreFields));
            getLogger().info(msg);
        }
    }

    public void logError(String funcName, String comment, Object output, Throwable e) {
        if (getLogger().isErrorEnabled()) {
            String s = obj2json(output);
            String msg = String.format(LOG_FORMAT, funcName, comment, s);
            getLogger().error(msg, e);
        }
    }

    /**
     * 犯错调用api
     *
     * @param api api
     * @return {@link String }
     * @author Tequila
     * @date 2022/07/27 17:02
     */
    public static String msgCallApiError(String api) {
        return String.format(MSG_CALL_API_ERROR, api);
    }

    public ApiConfig getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    /**
     * 获取日志记录器
     *
     * @return {@link Logger }
     * @author Tequila
     * @date 2022/07/27 16:58
     */
    public abstract Logger getLogger();
}
