package com.example.standard_manage_back.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.entity.User;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器，拦截token
 */
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String token=request.getHeader("token");    //获取前端发过来的token
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        // 执行认证
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(Constants.USER_NOT_LOGGED_IN,"无token，请重新登录");    //抛出自定义异常
        }
        // 获取 token 中的 user id(载荷)
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException(Constants.USER_NOT_LOGGED_IN,"token验证失败，请重新登录");    //抛出自定义异常
        }
        //根据userId查询数据库
        User user = userService.getById(userId);
        if (user == null) {
            throw new ServiceException(Constants.USER_NOT_LOGGED_IN,"用户不存在，请重新登录");
        }
        //用户密码加签验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);  //验证token
        } catch (JWTVerificationException e) {
            throw new ServiceException(Constants.USER_NOT_LOGGED_IN,"token验证失败，请重新登录");    //抛出自定义异常
        }
        return true;
    }
}
