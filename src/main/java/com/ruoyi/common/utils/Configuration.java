package com.ruoyi.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Properties;

/**
 * 微信公众号、商户平台基础配置
 * <pre>
 * =============================
 * = @author: aiyunhui         =
 * = @idea:   STS IDEA         =
 * = @date:   2018-06-07 17:25 =
 * =============================
 */
public class Configuration {
	private static Properties defaultProperty;
	
	static {
        init();
    }
	
	static void init() {
        //初始化默认配置
        defaultProperty = new Properties();
        defaultProperty.setProperty("jweixin.debug", "true");
        defaultProperty.setProperty("jweixin.token", "jweixin");
        defaultProperty.setProperty("jweixin.http.connectionTimeout", "20000");
        defaultProperty.setProperty("jweixin.http.readTimeout", "120000");
        defaultProperty.setProperty("jweixin.http.retryCount", "3");
        //读取自定义配置
        String jwxProps = "jweixin.properties";
        boolean loaded = loadProperties(defaultProperty, "." + File.separatorChar + jwxProps)
                || loadProperties(defaultProperty, Configuration.class.getResourceAsStream("/WEB-INF/" + jwxProps))
                || loadProperties(defaultProperty, Configuration.class.getClassLoader().getResourceAsStream(jwxProps));
        if (!loaded) {
            System.out.println("jweixin:没有加载到jweixin.properties属性文件!");
        }
    }
	
	/**
     * 加载属性文件
     *
     * @param props 属性文件实例
     * @param path 属性文件路径
     * @return 是否加载成功
     */
    private static boolean loadProperties(Properties props, String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                props.load(new FileInputStream(file));
                return true;
            }
        } catch (IOException ignore) {
            //异常忽略
            ignore.printStackTrace();
        }
        return false;
    }
    
    /**
     * 加载属性文件
     *
     * @param props 属性文件实例
     * @param is 属性文件流
     * @return 是否加载成功
     */
    private static boolean loadProperties(Properties props, InputStream is) {
        try {
            if (is != null) {
                props.load(is);
                return true;
            }
        } catch (IOException ignore) {
            //异常忽略
            ignore.printStackTrace();
        }
        return false;
    }
    
    /**
     * 获取开发者验证TOKEN
     *
     * @return 开发者模式TOKEN
     */
    public static String getToken() {
    	return getProperty("jweixin.token");
    }
    
    /**
     * 获取开发者第三方用户唯一凭证
     *
     * @return 第三方用户唯一凭证
     */
    public static String getOAuthAppId() {
        return getProperty("jweixin.oauth.appid");
    }

    /**
     * 获取开发者第三方用户唯一凭证
     *
     * @param appid 默认第三方用户唯一凭证
     * @return 第三方用户唯一凭证
     */
    public static String getOAuthAppId(String appid) {
        return getProperty("jweixin.oauth.appid", appid);
    }

    /**
     * 获取开发者第三方用户唯一凭证密钥
     *
     * @return 第三方用户唯一凭证密钥
     */
    public static String getOAuthSecret() {
        return getProperty("jweixin.oauth.appsecret");
    }

    /**
     * 获取开发者第三方用户唯一凭证密钥
     *
     * @param secret 默认第三方用户唯一凭证密钥
     * @return 第三方用户唯一凭证密钥
     */
    public static String getOAuthSecret(String secret) {
        return getProperty("jweixin.oauth.appsecret", secret);
    }

    /**
     * 获取商户号
     *
     * @return 商家支付平台的商户号
     */
    public static String getPayPartnerMchId() {
        return getProperty("jweixin.pay.partner.mch_id");
    }
    
    /**
     * 获取商户API kEY
     *
     * @return 商家支付平台的API kEY
     */
    public static String getPayPartnerApiKey() {
        return getProperty("jweixin.pay.partner.api_key");
    }
    
    /**
     * 商户支付签名方式
     *
     * @return 支付签名方式
     */
    public static String getPayPartnerSignType() {
    	return getProperty("jweixin.pay.partner.sign_type");
    }
    
    /**
     * 商户支付成功后的回调URL地址
     *
     * @return 支付成功异步通知的URL地址
     */
    public static String getPayNotifyUrl() {
    	return getProperty("jweixin.pay.notify_url");
    }
    
    /**
     * 商户支付证书
     *
     * @return 商户交易证书地址
     */
    public static String getPayCertPath() {
    	return getProperty("jweixin.pay.cert.path");
    }
    
    /**
     * 获取 连接超时时间
     *
     * @return 连接超时时间
     */
    public static int getHttpConnectionTimeout() {
        return getIntProperty("jweixin.http.connectionTimeout");
    }

    /**
     * 获取 连接超时时间
     *
     * @param connectionTimeout 默认连接超时时间
     * @return 连接超时时间
     */
    public static int getHttpConnectionTimeout(int connectionTimeout) {
        return getIntProperty("jweixin.http.connectionTimeout", connectionTimeout);
    }

    /**
     * 获取 请求超时时间
     *
     * @return 请求超时时间
     */
    public static int getHttpReadTimeout() {
        return getIntProperty("jweixin.http.readTimeout");
    }
    
    /**
     * 获取 请求超时时间
     *
     * @return 请求超时时间
     */
    public static String getHttpEncoding() {
    	return getProperty("jweixin.http.encoding");
    }

    /**
     * 获取 请求超时时间
     *
     * @param readTimeout 默认请求超时时间
     * @return 请求超时时间
     */
    public static int getHttpReadTimeout(int readTimeout) {
        return getIntProperty("jweixin.http.readTimeout", readTimeout);
    }
    
    /**
     * 获取 是否为调试模式
     *
     * @return 是否为调试模式
     */
    public static boolean isDebug() {
        return getBoolean("jweixin.debug");
    }

    public static boolean getBoolean(String name) {
        String value = getProperty(name);
        return Boolean.valueOf(value);
    }

    public static int getIntProperty(String name) {
        String value = getProperty(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public static int getIntProperty(String name, int fallbackValue) {
        String value = getProperty(name, String.valueOf(fallbackValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    /**
     * 获取属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    public static String getProperty(String name) {
        return getProperty(name, null);
    }

    /**
     * 获取属性值
     *
     * @param name 属性名
     * @param fallbackValue 默认返回值
     * @return 属性值
     */
    public static String getProperty(String name, String fallbackValue) {
        String value;
        try {
            //从全局系统获取
            value = System.getProperty(name, null);
            if (null == value) {
                //先从系统配置文件获取
                value = defaultProperty.getProperty(name, fallbackValue);
            }
        } catch (AccessControlException ace) {
            // Unsigned applet cannot access System properties
            value = fallbackValue;
        }
        return value;
    }
}
