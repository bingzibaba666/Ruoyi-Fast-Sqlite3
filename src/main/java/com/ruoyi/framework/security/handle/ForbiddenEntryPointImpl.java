package com.ruoyi.framework.security.handle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.domain.AjaxResult;

/**
 * 认证失败处理类 返回未授权
 * 
 * @author ruoyi
 */
@Component
public class ForbiddenEntryPointImpl implements AccessDeniedHandler {
	

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException arg2)
			throws IOException, ServletException {
		 int code = HttpStatus.FORBIDDEN;
	        String msg = StringUtils.format("请求访问：{}，访问受限，授权过期", request.getRequestURI());
	        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
		
	}
}
