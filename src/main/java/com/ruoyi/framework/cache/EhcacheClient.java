package com.ruoyi.framework.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Ehcache缓存方式
 *
 */
public class EhcacheClient {

	private Ehcache cache;

	public Ehcache getCache() {
		return cache;
	}

	public void setCache(Ehcache cache) {
		this.cache = cache;
	}

	/**
	 * Get方法, 转换结果类型并屏蔽异常, 仅返回Null.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		try {
			Element e=cache.get(key);
			return (T) e.getValue();
		} catch (RuntimeException e) {
			
			return (T)null;
		}
	}
	
	public List<String> keys(String key){
		List<String> keyList=new ArrayList<String>();
		List<String> keys=cache.getKeys();
		for(int i=0;i<keys.size();i++){
			if(keys.get(i).startsWith(key)){
				keyList.add(keys.get(i));
			}
		}
		return keyList;
	}

	/**
	 * GetBulk方法, 转换结果类型并屏蔽异常.
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getBulk(Collection<String> keys)
	{
		Map<String, T> map=new HashMap<String, T>();
		for(String key:keys)
		{
			T object= (T)this.get(key);
			if(object!=null)
				map.put(key, object);
		}
		return map;
	}

	/**
	 * 异步Set方法, 不考虑执行结果.
	 */
	public void set(String key, int expiredTime, Object value) {
		Element e=null;
		if(expiredTime==0){
			e=new Element(key,value);
		}else{
			e=new Element(key, value, false, null,expiredTime);
		}
		cache.put(e);
	}
	
	
	/**
	 * 安全的Set方法, 保证在updateTimeout秒内返回执行结果, 否则返回false并取消操作.
	 */
	public boolean safeSet(String key, int expiration, Object value) {
		try {
			this.set(key, expiration, value);
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}

	/**
	 * 异步 Delete方法, 不考虑执行结果.
	 */
	public void delete(String key) {
		cache.remove(key);		
	}

	/**
	 * 安全的Delete方法, 保证在updateTimeout秒内返回执行结果, 否则返回false并取消操作.
	 */
	public boolean safeDelete(String key) {
		
		try {
			this.delete(key);
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}
}
