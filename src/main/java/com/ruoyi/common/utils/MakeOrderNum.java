package com.ruoyi.common.utils;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: MakeOrderNum
 * @CreateTime 2015年9月13日 下午4:51:02
 * @author : mayi
 * @Description: 订单号生成工具，生成非重复订单号，理论上限1毫秒1000个，可扩展
 *
 */
public class MakeOrderNum {
	public static final String RECHARGE_PREFIX = "R";
	
	/**
	 * 锁对象，可以为任意对象
	 */
	private static Object lockObj = "lockerOrder";
	/**
	 * 订单号生成计数器
	 */
	private static long orderNumCount = 0L;
	/**
	 * 每毫秒生成订单号数量最大值
	 */
	private int maxPerMSECSize=1000;
	/**
	 * 生成非重复订单号，理论上限1毫秒1000个，可扩展
	 */
	public String makeOrderNum() {
		// 最终生成的订单号
		String finOrderNum = "";
		try {
			synchronized (lockObj) {
				// 取系统当前时间作为订单号变量前半部分，精确到毫秒
				long nowLong = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmSS").format(new Date()));
				// 计数器到最大值归零，可扩展更大，目前1毫秒处理峰值1000个，1秒100万
				if (orderNumCount >= maxPerMSECSize) {
					orderNumCount = 0L;
				}
				//组装订单号
				String countStr=maxPerMSECSize +orderNumCount+"";
				finOrderNum=nowLong+countStr.substring(1);
				orderNumCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			finOrderNum = "";
		}
		return finOrderNum;
	}
	/**
	 * 生成支付订单编号(在订单号前面添加标识R)
	 * @return
	 */
	public String makeRechargeOrderNo() {
		String orderNo = makeOrderNum();
		//如果第一次没能取到值，将会继续取值(最多取值五次)
		int getCount = 1;
		while(StringUtils.isBlank(orderNo) && getCount <= 5) {
			orderNo = makeOrderNum();
			++getCount;
		}
		return RECHARGE_PREFIX + orderNo;
	}
}
