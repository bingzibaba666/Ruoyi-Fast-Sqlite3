package com.ruoyi.framework.aspectj;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ruoyi.common.exception.RequestLimitException;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.framework.annotation.RequestLimit;


/**
 * 
 * <pre>
 * =============================
 * = @author: aiyunhui         =
 * = @idea:   STS IDEA         =
 * = @date:   2018-07-11 11:09 =
 * =============================
 */
@Aspect
@Component
public class RequestLimitContract {
	private static final Logger logger = LoggerFactory.getLogger("requestLimitLogger");
    private Map<String, Integer> requestMap = new HashMap<String, Integer>();
    
    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint , RequestLimit limit) throws RequestLimitException {
        try {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String ip = IpUtils.getIpAddr(request);
            String url = request.getRequestURL().toString();
            final String key = "req_limit_".concat(url).concat(ip);
            int reqCount = MapUtils.getIntValue(requestMap, key, 0);
            if ( reqCount == 0 ) {
                requestMap.put(key, 1);
            } else {
                requestMap.put(key, requestMap.get(key) + 1);
            }
            int count = MapUtils.getIntValue(requestMap, key, 0);
            if (count > 0) {
                //创建一个定时器
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        requestMap.remove(key);
                    }
                };
                //这个定时器设定在time规定的时间之后会执行上面的remove方法，也就是说在这个时间后它可以重新访问
                timer.schedule(timerTask, limit.time());
            }
            if (count > limit.count()) {
                logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
                throw new RequestLimitException();
            }
        }catch (RequestLimitException e){
            throw e;
        }catch (Exception e){
            logger.error("发生异常",e);
        }
    }
}
