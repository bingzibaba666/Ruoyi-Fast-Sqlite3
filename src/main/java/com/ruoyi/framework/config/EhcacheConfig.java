package com.ruoyi.framework.config;

import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ruoyi.framework.cache.EhcacheClient;

/**
 * 权限配置加载
 * 
 * @author ruoyi
 */
@Configuration
public class EhcacheConfig
{

    @Bean
    public EhCacheFactoryBean cacheBean(){
    	EhCacheFactoryBean cache = new EhCacheFactoryBean();
    	cache.setBeanName("BaseDataCache");
    	cache.setCacheManager(new EhCacheManagerFactoryBean().getObject());
    	return cache;
    }
    
    @Bean
    public EhcacheClient cacheClient(){
    	EhcacheClient ehcacheClient = new EhcacheClient();
    	ehcacheClient.setCache(cacheBean().getObject());
    	return ehcacheClient;
    }
    
}
