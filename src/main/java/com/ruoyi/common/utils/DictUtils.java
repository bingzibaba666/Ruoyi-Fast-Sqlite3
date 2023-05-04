package com.ruoyi.common.utils;

import java.util.Collection;
import java.util.List;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.cache.EhcacheClient;
import com.ruoyi.project.system.domain.SysDictData;

/**
 * 字典工具类
 * 
 * @author ruoyi
 */
public class DictUtils
{
    /**
     * 设置字典缓存
     * 
     * @param key 参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas)
    {
        SpringUtils.getBean(EhcacheClient.class).set(getCacheKey(key),0, dictDatas);
    }

    /**
     * 获取字典缓存
     * 
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key)
    {
        Object cacheObj = SpringUtils.getBean(EhcacheClient.class).get(getCacheKey(key));
        if (StringUtils.isNotNull(cacheObj))
        {
            List<SysDictData> DictDatas = StringUtils.cast(cacheObj);
            return DictDatas;
        }
        return null;
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache()
    {
        Collection<String> keys = SpringUtils.getBean(EhcacheClient.class).keys(Constants.SYS_DICT_KEY + "*");
        for (String key : keys) {
        	 SpringUtils.getBean(EhcacheClient.class).delete(key);
		}
       
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey)
    {
        return Constants.SYS_DICT_KEY + configKey;
    }
}
