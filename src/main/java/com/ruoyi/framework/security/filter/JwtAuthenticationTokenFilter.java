package com.ruoyi.framework.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.security.LoginUser;
import com.ruoyi.framework.security.LoginWebUser;
import com.ruoyi.framework.security.service.TokenService;
import com.ruoyi.framework.security.service.TokenWebService;

/**
 * token过滤器 验证token有效性
 * 
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenWebService tokenWebService;
    
    @Value("${token.header}")
    private String tokenHeader;
    @Value("${token.headerPortal}")
    private String tokenHeadPortal;
    @Value("${token.headerAdmin}")
    private String tokenHeadAdmin;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException{
		 String authHeader = request.getHeader(this.tokenHeader);
	     if (authHeader != null) {
	    	 if(authHeader.startsWith(Constants.TOKEN_PREFIX+this.tokenHeadAdmin)||authHeader.startsWith(this.tokenHeadAdmin)){
	    		 LoginUser loginUser = tokenService.getLoginUser(request);
	    		 if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
	    	       {
	    	           tokenService.verifyToken(loginUser);
	    	           UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
	    	           authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	    	           SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	    	       }
	    	 }else if(authHeader.startsWith(Constants.TOKEN_PREFIX+this.tokenHeadPortal)||authHeader.startsWith(this.tokenHeadPortal)){
	    		 LoginWebUser loginWebUser = tokenWebService.getLoginUser(request);
	    		 if (StringUtils.isNotNull(loginWebUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
	    	       {
	    	           tokenWebService.verifyToken(loginWebUser);
	    	           UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginWebUser, null, loginWebUser.getAuthorities());
	    	           authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	    	           SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	    	       }
	    	 }
	     }
	     chain.doFilter(request, response);
	}
}
