package com.example;


import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.userdetails.GrantedAuthorityFromAssertionAttributesUserDetailsService;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;


@Configuration
@EnableWebSecurity
public class CasConfig extends WebSecurityConfigurerAdapter {


    /**
     * 总流程
     * filter 拦截login login页面, 登录后 用provider尝试验ticket 获取 token
     * 成功调用 successfulxxx()
     */


    /**
     * Filter->AuthenticationManager->Provider->attemptAuthentication
     */

    @Autowired
    private AuthenticationManager authenticationManager;


    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager);
        return casAuthenticationFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();
    }

    /**
     * 服务器回调页面
     * 被CasAuthenticationEntryPoint用来 继续 请求授权
     *
     */


    /**
     * 认证提供的provider , CasAuthenticationToken
     */


    /**
     * 组装验证服务地址
     * http://www.cas.server.com:8080/cas/serviceValidate?
     * ticket=ST-176-p5vZRPXqr-VG2hWQsGmRNNfDjhYDESKTOP-I2C93H9
     * &
     * service=http%3A%2F%2Fapp1.com%3A8997%2Flogin%2Fcas
     * <p>
     * 这里的service和登录的可能要一致
     *
     * @return
     */

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator("http://www.cas.server.com:8080/cas");
    }








    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService("http://app1.com:8997/login/cas"); //默认就是/login/cas


        //如果设置了代理那么就继续把ticket参数附上
        //serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }


    /**
     * 终点:
     * <p>
     * 用来重定向的
     *
     * @return
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setServiceProperties(serviceProperties());  //
        entryPoint.setLoginUrl("http://www.cas.server.com:8080/cas/login");

        return entryPoint;
    }

    /**
     * 单点退出
     * @return
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter signOutFilter = new SingleSignOutFilter();
        signOutFilter.setIgnoreInitConfiguration(true);
        signOutFilter.setCasServerUrlPrefix("http://www.cas.server.com:8080/cas");
        return signOutFilter;
    }



    /**
     * HttpSecurity 的普通配置
     *
     * @param http
     * @throws Exception
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.requestMatchers().antMatchers("/**")//这么多进行过滤
                .and()
                .authorizeRequests()
                .antMatchers("/logout").permitAll()
                //.antMatchers("/login/cas").permitAll() //不用设置,会被 casAuthenticationFilter 过滤器拦截处理
                .anyRequest().authenticated()
                .and().csrf().disable()
                .logout().disable();

        /**
         * 未授权的异常被处理到exceptionHandling重定向登录
         * list.add
         */
        http.exceptionHandling()
                .authenticationEntryPoint(casAuthenticationEntryPoint());

        /**
         *  过滤器
         */

        http.addFilter(casAuthenticationFilter())
                .addFilterAt(logoutFilter(), LogoutFilter.class)
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);


    }

    /**
     * 配置验证的Provider
     */

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(
            AuthenticationUserDetailsService userDetailsService,
            ServiceProperties serviceProperties, Cas20ServiceTicketValidator ticketValidator) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setKey("casProvider"); //生成Token的key 随便设置

        provider.setServiceProperties(serviceProperties); //provider 或者 filter 或者 entryporint 用来用的属性
        provider.setTicketValidator(ticketValidator); // 设置 ticket 服务器验证地址 生成和请求的地方
        provider.setAuthenticationUserDetailsService(userDetailsService);

        return provider;
    }

    @Autowired
    private CasAuthenticationProvider casAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider);

    }


    /**
     *
     * 替代原来的登出Filter
     * @return
     */

    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter filter =
                new LogoutFilter("http://www.cas.server.com:8080/cas/logout?service=http://app1.com:8997/logout"
                        , new SecurityContextLogoutHandler());//一个页面
        //替换过滤器进行登出的地址
        filter.setFilterProcessesUrl("/logout/cas");
        return filter;
    }
}
