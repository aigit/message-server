package com.qlk.message.server.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.qlk.baymax.common.log.CommonLoggerFactory;

/**
 * Http请求工具类
 * <P>File name : HttpRequestUtils.java </P>
 * <P>Author : zhouyanxin </P>
 * <P>Date : 2015年6月28日 </P>
 */
public final class HttpRequestUtils {

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(HttpRequestUtils.class);

    /**
     * 请求超时时间
     */
    private static final int REQUEST_TIMEOUT = 3000;

    /**
     * 传输超时时间
     */
    private static final int TRANSFER_TIMEOUT = 3000;

    /**
     * 默认字符集
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * GET请求
     * 对应StringEntity
     * HttpRequestUtils.doGet()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2015年6月28日 </P>
     * @param linkUrl
     * @param param
     * @return
     */
    public static int doGet(String linkUrl) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(linkUrl);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(TRANSFER_TIMEOUT).build();// 设置请求和传输超时时间
        request.setConfig(requestConfig);
        try {
            HttpResponse response = httpClient.execute(request);
            if (response != null && response.getStatusLine() != null) {
                return response.getStatusLine().getStatusCode();
            }
            return 0;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return 0;
        } finally {
            request.abort();
            request.releaseConnection();
        }
    }

    /**
     * POST请求
     * 对应StringEntity
     * HttpRequestUtils.doPost()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2015年6月28日 </P>
     * @param linkUrl
     * @param param
     * @return
     */
    public static String doPost(String linkUrl, String param) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(linkUrl);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(TRANSFER_TIMEOUT).build();// 设置请求和传输超时时间
        request.setConfig(requestConfig);
        try {
            StringEntity entity = new StringEntity(param, DEFAULT_CHARSET);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET).trim();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        } finally {
            request.abort();
            request.releaseConnection();
        }
    }

    /**
     * POST请求
     * 对应
     * HttpRequestUtils.doPost()<BR>
     * <P>Author : zhouyanxin </P>
     * <P>Date : 2015年6月28日 </P>
     * @param linkUrl
     * @param params
     * @return
     */
    public static String doPost(String linkUrl, Map<String, String> params) {

        // 构建请求参数
        List<BasicNameValuePair> formParams = new ArrayList<>();
        for (Iterator<Entry<String, String>> iter = params.entrySet().iterator(); iter.hasNext();) {
            Entry<String, String> entry = iter.next();
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        // 执行请求
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(linkUrl);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(TRANSFER_TIMEOUT).build();// 设置请求和传输超时时间
        request.setConfig(requestConfig);
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET).trim();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        } finally {
            request.abort();
            request.releaseConnection();
        }
    }
}
