package com.ruoyi.framework.config;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ruoyi.framework.security.data.AdminUserDetails;
import com.ruoyi.framework.security.data.WebUserDetails;
import com.ruoyi.framework.security.filter.JwtAuthenticationTokenFilter;
import com.ruoyi.framework.security.handle.AuthenticationEntryPointImpl;
import com.ruoyi.framework.security.handle.ForbiddenEntryPointImpl;
import com.ruoyi.framework.security.handle.LogoutSuccessHandlerImpl;
import com.ruoyi.framework.security.service.SysPermissionService;
import com.ruoyi.project.dq.mapper.AppUserMapper;
import com.ruoyi.project.system.domain.SysUser;
import com.ruoyi.project.system.mapper.SysUserMapper;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiHttpSecurityConfig  {
	
	 

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private SysPermissionService permissionService;
    
    /**
     * token认证过滤器
     */
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * 退出处理类
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;
    
    /**
     * 认证失败处理类
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;
    
    /**
     * 授权过期
     */
    @Autowired
    private ForbiddenEntryPointImpl forbiddenHandler;
    
    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    
    @Configuration
    @Order(1)
    public class UserWebSecurityConfig extends WebSecurityConfigurerAdapter {
    
        protected void configure(HttpSecurity httpSecurity) throws Exception {
        	 httpSecurity
             // CRSF禁用，因为不使用session
             .csrf().disable()
             // 基于token，所以不需要session
             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
             .antMatcher("/api/**")
             // 过滤请求
             .authorizeRequests()
             // 对于登录login允许匿名访问
             .antMatchers("/api/web/system/captchaImage").anonymous()
             .antMatchers("/api/web/login").anonymous()
             .antMatchers("/api/web/logout").anonymous()
             .antMatchers("/api/web/register").anonymous()
             .antMatchers("/api/web/yh/weizhi").anonymous()
             .antMatchers("/api/web/yh/getqrcode").anonymous()
             .antMatchers("/api/web/yh/checkqrcode").anonymous()
             .antMatchers("/api/fzalipay/notify_pay").anonymous()
             // 除上面外的所有请求全部需要鉴权认证
             .anyRequest().authenticated()
             .and()
             .headers().frameOptions().disable();
		     // 添加JWT filter
		     httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
		   //添加自定义未授权和未登录结果返回
	            httpSecurity.exceptionHandling()
	                    .accessDeniedHandler(forbiddenHandler)
	                    .authenticationEntryPoint(unauthorizedHandler);
        }
 
        /**
         * 身份认证接口
         */
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(webUserDetailsService());
        }
    }
    
    
    @Configuration
    @Order(2)
    public class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {

        /**
         * anyRequest          |   匹配所有请求路径
         * access              |   SpringEl表达式结果为true时可以访问
         * anonymous           |   匿名可以访问
         * denyAll             |   用户不能访问
         * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
         * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
         * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
         * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
         * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
         * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
         * permitAll           |   用户可以任意访问
         * rememberMe          |   允许通过remember-me登录的用户访问
         * authenticated       |   用户登录后可访问
         */
        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception
        {
            httpSecurity
                    // CRSF禁用，因为不使用session
                    .csrf().disable()
                    // 基于token，所以不需要session
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .antMatcher("/sys/**")
            		.antMatcher("/system/**")
            		.antMatcher("/tool/**")
            		.antMatcher("/gen/**")
            		.antMatcher("/common/**")
            		.antMatcher("/dq/**")
                    // 过滤请求
                    .authorizeRequests()
                    // 除上面外的所有请求全部需要鉴权认证
                    .anyRequest().authenticated()
                    .and()
                    .headers().frameOptions().disable();
            // 添加JWT filter
            httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
            
            //添加自定义未授权和未登录结果返回
            httpSecurity.exceptionHandling()
                    .accessDeniedHandler(forbiddenHandler)
                    .authenticationEntryPoint(unauthorizedHandler);
        }

        
        /**
         * 身份认证接口
         */
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception
        {
            auth.userDetailsService(adminUserDetailsService());
        }
    }
    
    @Configuration
    @Order(3)
    public class OtherSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity httpSecurity) throws Exception {
        	httpSecurity.csrf()// 由于使用的是JWT，我们这里不需要csrf
                    .disable()
                    .sessionManagement()// 基于token，所以不需要session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    // 对于登录login 验证码captchaImage 允许匿名访问
                    .antMatchers("/login").anonymous()
                    .antMatchers("/profile/**").anonymous()
                    .antMatchers("/common/download**").anonymous()
                    .antMatchers("/common/download/resource**").anonymous()
                    .antMatchers("/webjars/**").anonymous()
                    .antMatchers("/druid/**").anonymous()
                    .antMatchers("/captchaImage").anonymous()
                    .antMatchers(
                            HttpMethod.GET,
                            "/",
                            "/*.html",
                            "/favicon.ico",
                            "/**/*.html",
                            "/static/**",
                            "/**/*.css",
                            "/**/*.js",
                            "/swagger-resources/**",
                            "/v2/api-docs/**"
                    ).permitAll()
                    // 除上面外的所有请求全部需要鉴权认证
                    .anyRequest().authenticated()
                    .and()
                    .headers().frameOptions().disable();
            httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
            // 添加JWT filter
            httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
            //添加自定义未授权和未登录结果返回
            httpSecurity.exceptionHandling()
                    .accessDeniedHandler(forbiddenHandler)
                    .authenticationEntryPoint(unauthorizedHandler);
        }
    }

    @Bean(name = "adminUserDetailsService")
    public UserDetailsService adminUserDetailsService(){
        //获取登录用户信息
        return username -> {
        	SysUser admin = sysUserMapper.selectUserByUserName(username);
            if (admin != null){
            	Set<String> permissionList=permissionService.getMenuPermission(admin);
                return new AdminUserDetails(admin,permissionList);
            }
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }
    
    
    @Bean(name = "webUserDetailsService")
    public UserDetailsService webUserDetailsService(){
        //获取登录用户信息
        return username -> {
        	Map<String,Object> user=appUserMapper.getByUsername(username);
            if (user != null){
                return new WebUserDetails(user);
            }
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }

    /**
     * 允许跨域调用的过滤器
     * @return
     */
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**",config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return new CorsFilter(source);
    }

}
