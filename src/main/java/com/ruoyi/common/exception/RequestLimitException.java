package com.ruoyi.common.exception;

/**
 * 限制请求IP次数异常类
 * <pre>
 * =============================
 * = @author: aiyunhui         =
 * = @idea:   STS IDEA         =
 * = @date:   2018-07-11 11:07 =
 * =============================
 */
public class RequestLimitException extends RuntimeException {
	private static final long serialVersionUID = 1364225358754654702L;

    public RequestLimitException(){
        super("请求频繁");
    }

    public RequestLimitException(String message){
        super(message);
    }
}
