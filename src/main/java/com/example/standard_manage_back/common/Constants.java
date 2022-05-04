package com.example.standard_manage_back.common;


/**
 * 枚举定义，状态码
 */
public interface Constants{
    /* 成功状态码 */
    String CODE_SUCCESS="200";  //成功
    /* 参数错误 */
    String PARAM_IS_INVALID="301";  //参数无效
    String PARAM_IS_BLANK="302";  //参数为空
    String PARAM_TYPE_BIND_ERROR="303";  //参数类型错误
    String PARAM_NOT_COMPLETE="304";    //参数缺失
    String PARAM_HAS_EXISTED="305";    //参数重复
    /* 用户错误 */
    String USER_NOT_LOGGED_IN="401";    //用户未登录，访问的路径需要验证，请登录
    String USER_LOGIN_ERROR="402";  //用户名或密码错误
    String USER_ACCOUNT_FORBIDDEN="403";    //账号已被禁用
    String USER_NOT_EXIST="404";    //用户不存在
    String USER_HAS_EXISTED="405";  //用户已存在

    /* 默认错误-系统错误 */
    String CODE_ERROR="999";  //系统错误

    /*默认头像*/
    String default_avatar="http://localhost:9090/file/48afd8b90ea947688eeb53c25e0a1e86.png";
}
