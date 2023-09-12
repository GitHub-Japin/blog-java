package com.Blog.config;

import com.Blog.sercurity.jwt.JwtFilter;
import com.Blog.sercurity.shiro.AccountRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/*shiro配置类*/
@Configuration
public class ShiroConfig {

    @Bean//创建realm对象，需自定义类：1
    public AccountRealm accountRealm() {
        return new AccountRealm();
    }

    @Bean //DefaultWebSecurityManager：2
    public DefaultWebSecurityManager defaultWebSecurityManager(AccountRealm accountRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);
        return securityManager;
    }

    @Bean//ShiroFilterFactoryBean：3
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
                                                         JwtFilter jwtFilter) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        //配置拦截路径Filter 定义JwtFilter
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("jwt", jwtFilter);
        factoryBean.setFilters(filterMap);
        //配置拦截器拦截的路径
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/oauth/**","anon");
        map.put("/**", "jwt");
        factoryBean.setFilterChainDefinitionMap(map);
        return factoryBean;
    }

    //JwtFilter需要在ShiroFilterFactoryBean之后加载
    //顺序不能改 否则SecurityManager报空
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    //开启注解功能
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
