package com.example.standard_manage_back.entity.dto;

import com.example.standard_manage_back.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * 接收前端登录请求的参数，不影响到User
 */

@Data
public class UserDTO {
    private String phone;
    private String name;
    private String password;
    private Integer status;
    private String token;
    private String avata; //头像
    private List<Menu> menu;  //菜单与路由
}
