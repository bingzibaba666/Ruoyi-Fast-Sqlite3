package com.ruoyi.project.dq.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 会员管理
 * @author lurong
 *
 */
@Mapper
public interface AppUserMapper {

	Map<String,Object> get(Long appUserId);	

	Map<String,Object> getByUsername(String username);
	
	List<Map<String,Object>> list(Map<String,Object> map);
	
	int count(Map<String,Object> map);
	
	int remove(Long app_user_id);
	
	int batchRemove(Long[] appUserIds);
	
    
}
