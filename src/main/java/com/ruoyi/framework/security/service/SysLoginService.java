package com.ruoyi.framework.security.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.MD5Utils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.cache.EhcacheClient;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.security.LoginUser;
import com.ruoyi.framework.security.LoginWebUser;
import com.ruoyi.framework.security.data.AdminUserDetails;
import com.ruoyi.framework.security.data.WebUserDetails;

/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysLoginService
{
	
    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenWebService tokenWebService;

    @Autowired
    @Qualifier("adminUserDetailsService")
    private UserDetailsService adminUserDetailsService;
    
    @Autowired
    @Qualifier("webUserDetailsService")
    private UserDetailsService webUserDetailsService;

    @Autowired
    private EhcacheClient ehcacheClient;

    /**
     * 后台登录验证
     * @param username 用户名
     * @param password 密码
     * @param captcha 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid)
    {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = ehcacheClient.get(verifyKey);
        ehcacheClient.delete(verifyKey);
        if (captcha == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
        // 用户验证
        Authentication authentication = null;
        try
        {
            UserDetails userDetails = adminUserDetailsService.loadUserByUsername(username);
            if (!SecurityUtils.matchesPassword(password,userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        AdminUserDetails adminUserDetails = (AdminUserDetails) authentication.getPrincipal();
        LoginUser loginUser=new LoginUser(adminUserDetails.getUser(),adminUserDetails.getPermissionSet());
        // 生成token
        return tokenService.createToken(loginUser);
    }
    
    /**
     * 门户登录验证
     * @param username 用户名
     * @param password 密码
     * @param captcha 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String uuid)
    {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        ehcacheClient.delete(verifyKey);
        // 用户验证
        Authentication authentication = null;
        try
        {
        	UserDetails userDetails = webUserDetailsService.loadUserByUsername(username);
            if (!MD5Utils.encrypt(username, password).equals(userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        
        WebUserDetails webUserDetails = (WebUserDetails) authentication.getPrincipal();
        Set<String> set=new HashSet<String>();
        LoginWebUser loginUser=new LoginWebUser(webUserDetails.getUser(),set);
        // 生成token
        return tokenWebService.createToken(loginUser);
    }
    
    /**
     * 门户密文验证
     * @param username 用户名
     * @param password 密码
     * @param captcha 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String encryptLogin(String username, String password, String uuid)
    {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        ehcacheClient.delete(verifyKey);
        // 用户验证
        Authentication authentication = null;
        try
        {
        	UserDetails userDetails = webUserDetailsService.loadUserByUsername(username);
            if (!password.equals(userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        
        WebUserDetails webUserDetails = (WebUserDetails) authentication.getPrincipal();
        Set<String> set=new HashSet<String>();
        LoginWebUser loginUser=new LoginWebUser(webUserDetails.getUser(),set);
        // 生成token
        return tokenWebService.createToken(loginUser);
    }
    
    public static void main(String[] args) {
    	System.out.println("$2a$10$1HfvY0HlWMJqr/Gfvmnfm.w1A8n7PYkt9dWFiO5aJnLqhOmnQRnui");
    	System.out.println("pw--SecurityUtils.encryptPassword(user.getPassword()):"+ SecurityUtils.matchesPassword("111111", "$2a$10$1HfvY0HlWMJqr/Gfvmnfm.w1A8n7PYkt9dWFiO5aJnLqhOmnQRnui"));
    	/*String ss=MD5Utils.encrypt("18022382006", "tianyu123456");*/
    /*	System.out.println(ss);*/
	}
}
