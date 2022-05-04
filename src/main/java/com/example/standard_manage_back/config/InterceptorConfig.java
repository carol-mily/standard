package com.example.standard_manage_back.config;

import com.example.standard_manage_back.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**") // 拦截所有请求，通过判断token是否合法决定是否需要登录
                .excludePathPatterns("/user/login","/user/register","/user/forgetPassword","/level/findAll","/standard/getPage","/standard/findById/**","/table/findByStanId/**","/item/findByTableId/**","/type/**","/**/export","/**/export/**","/**/import","/file/**");    //不需要验证的接口
    }

    //将JwtInterceptor注入容器中去
    @Bean
    public JwtInterceptor jwtInterceptor(){
        return new JwtInterceptor();
    }
}
