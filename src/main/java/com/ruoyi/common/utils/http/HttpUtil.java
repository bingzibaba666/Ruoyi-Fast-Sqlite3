package com.ruoyi.common.utils.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.utils.Configuration;
import com.ruoyi.common.utils.StringUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HTTP请求工具类
 * <pre>
 * =============================
 * = @author: aiyunhui         =
 * = @idea:   STS IDEA         =
 * = @date:   2018-06-07 19:33 =
 * =============================
 */
public class HttpUtil {  
	
	public static String post(String requestUrl,String params) {
		return request(requestUrl, params, true);
    }
	public static String get(String requestUrl) {
		return request(requestUrl, "", true);
	}
	
	private static String request(String uri, String params,boolean isPost) {
		String reqMethod = "GET";
		if(isPost) {
			reqMethod = "POST";
		}
		if(Configuration.isDebug()) {
		}
		String result = "";
        URL url;
		try {
			url = new URL(uri);
			// 打开和URL之间的连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 设置通用的请求属性
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod(reqMethod);
			// 得到请求的输出流对象
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(params);
			out.flush();
			out.close();
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String getLine;
			while ((getLine = in.readLine()) != null) {
				result += getLine;
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return result;
	}
	
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	public static String postData(String urlStr,String urlStr2,String urlStr3){ 
		log.error("请求url=============={}", urlStr);
		OkHttpClient client = new OkHttpClient().newBuilder()
					.connectTimeout(100, TimeUnit.SECONDS)
					.readTimeout(120, TimeUnit.SECONDS).build();

		Request request = new Request.Builder()
		  .url(urlStr)
		  .get()
		  .addHeader("cache-control", "no-cache")
		  .build();

		try {
			Response response = client.newCall(request).execute();
			String resp = response.body().string();
			log.error("响应结果=============={}", resp);
			return resp;
		} catch (IOException e) {
			log.error("响应异常==============");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static String postDataWithHeader(String urlStr,String header){  
		log.error("请求url=============={}", urlStr);
		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS)
				.readTimeout(120, TimeUnit.SECONDS).build();

		Request request = new Request.Builder()
		  .url(urlStr)
		  .get()
		  .addHeader("cache-control", "no-cache")
		  .addHeader("userKey", header)
		  .build();

		try {
			Response response = client.newCall(request).execute();
			String resp = response.body().string();
			log.error("响应结果=============={}", resp);
			return resp;
		} catch (IOException e) {
			log.error("响应异常==============");
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		String url = "http://api.map.baidu.com/location/ip?ak=32f38c9491f2da9eb61106aaab1e9739&ip=61.49.248.47";
		String result = get(url);
		System.err.println(result);
		if(!StringUtils.isNotBlank(result)) {
			Map<String,Object> resultMap = (Map)JSON.parse(result);
			Map<String,Object> map = (Map<String, Object>) resultMap.get("data");
			System.err.println(map.get("code") + "    " + map.get("nick"));
		}
	}

}