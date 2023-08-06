package com.java1234vote.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "weixin")
@Data
public class WeixinProperties {
    /**
     *   jscode2sessionUrl:https://api.weixin.qq.com/sns/jscode2session
     *   appid:wx8f8a0d4f16403e8b
     *   secret:4a8a1994c6ef40af53f91f7a0cf23dd1
     */
    private String jscode2sessionUrl;//登陆凭证校验请求地址
    private String appid;//小程序的appid
    private String secret;//小程序密钥
}
