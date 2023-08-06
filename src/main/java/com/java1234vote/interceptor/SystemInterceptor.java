package com.java1234vote.interceptor;

import com.java1234vote.util.JwtUtils;
import com.java1234vote.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


    /**
     * 鉴权拦截器
     */
    public class SystemInterceptor implements HandlerInterceptor {
        /**
         * 在处理请求之前进行拦截，进行鉴权操作
         * @param request HttpServletRequest对象，包含当前请求的信息
         * @param response HttpServletResponse对象，用于返回响应信息
         * @param handler 处理当前请求的处理器对象
         * @return 返回一个布尔值，表示是否允许请求继续执行
         * @throws Exception 如果鉴权失败或签名不存在，抛出异常
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String path = request.getRequestURI();
            System.out.println("path:" + path); // 打印请求路径
            if(handler instanceof HandlerMethod){
                // 判断请求处理器是否为 HandlerMethod 类型
                // 如果是，则获取请求头中名为 token 的值
                String token = request.getHeader("token"); // 获取请求头中的token
                System.out.println("token:" + token); // 打印token
                if(StringUtil.isEmpty(token)){ // 如果token为空
                    System.out.println("token为空");
                    throw new RuntimeException("签名不存在！"); // 抛出异常
                } else {
                    // 存在token，进行鉴权
                    Claims claims = JwtUtils.validateJWT(token).getClaims(); // 验证token的有效性，并获取其中的claims信息
                    if (claims != null) {
                        System.out.println("鉴权成功"); // 鉴权成功
                        return true;
                    } else {
                        System.out.println("鉴权失败");
                        throw new RuntimeException("鉴权失败"); // 抛出异常
                    }
                }
            } else {
                return true;
            }
        }

}
