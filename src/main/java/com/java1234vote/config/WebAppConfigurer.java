package com.java1234vote.config;

import com.java1234vote.interceptor.SystemInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
    //@Bean注解用于声明一个Bean，这里声明了一个名为systemInterceptor的Bean，并返回一个SystemInterceptor实例。
    // addInterceptors方法用于添加拦截器。在这里，我们将systemInterceptor添加为拦截器，并设置它作用于所有请求（"/**"），
    // 但排除了一些特定的路径（"/user/wxlogin"和"/image/**"）。这意味着除了这些路径外，所有的请求都会经过systemInterceptor拦截器。
    @Bean
    public SystemInterceptor systemInterceptor(){
        return new SystemInterceptor();
    }
    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String []path=new String[]{"/user/wxLogin","/image/**"};
        registry.addInterceptor(systemInterceptor())
                .addPathPatterns("/**") // 作用于所有请求
                .excludePathPatterns(path); // 排除指定路径
    }


    // 配置跨域请求
    // addCorsMappings方法用于配置跨域请求。
    // 这里使用registry对象来添加跨域映射，允许所有来源（allowedOrigins("*")），允许携带凭证（allowCredentials(true)），
    // 允许的请求方法包括GET、HEAD、POST、PUT、DELETE和OPTIONS（allowedMethods方法），并设置最大缓存时间为3600秒（maxAge(3600)）。
    // 这段代码的作用是配置拦截器和跨域请求的规则，以便在Web应用程序中进行统一的请求处理和跨域访问控制。
    @Override

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 允许所有来源
                .allowCredentials(true) // 允许携带凭证
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE","OPTIONS") // 允许的请求方法
                .maxAge(3600); // 最大缓存时间
    }

    //图片请求路径映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/userAvatar/**").addResourceLocations("file:D:\\local_image\\userAvatar\\");
        registry.addResourceHandler("/image/coverImgs/**").addResourceLocations("file:D:\\local_image\\coverImgs\\");
        registry.addResourceHandler("/image/voteItemImgs/**").addResourceLocations("file:D:\\local_image\\voteItemImages\\");
    }
}

