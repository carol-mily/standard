package com.example.standard_manage_back.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.standard_manage_back.entity.User;
import com.example.standard_manage_back.service.IUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component  //将其注册为springboot的一个bean
public class TokenUtils {

    private static IUserService staticUserService;

    @Resource   //将userService引入
    private IUserService userService;

    @PostConstruct  //将userService赋给静态的staticUserService，原因在于getCurrentUser为静态方法，静态方法必须使用静态的对象
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String genToken(String userid, String sign) {
        return JWT.create().withAudience(userid) // 将 user id 保存到 token 里面，作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) //2小时之后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return
     */
    public static User getCurrentUser() {
        try {
            //获取当前请求的request
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String token = request.getHeader("token");    //拿token
            if (!StrUtil.isBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);  //解析
                return staticUserService.getById(userId);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
