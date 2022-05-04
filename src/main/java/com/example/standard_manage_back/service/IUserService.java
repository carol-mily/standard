package com.example.standard_manage_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard_manage_back.entity.User;
import com.example.standard_manage_back.entity.dto.UserDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author carol
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    void register(UserDTO userDTO);

    boolean forgetPassword(UserDTO userDTO);

    String getUserName(String phone);

    List<String> selectPhones();

    void removePhone(List<String> list, String target);
}
