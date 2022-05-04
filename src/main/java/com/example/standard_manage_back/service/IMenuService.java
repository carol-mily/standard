package com.example.standard_manage_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard_manage_back.entity.Menu;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author carol
 */
public interface IMenuService extends IService<Menu> {
    public List<Menu> findAll();
    public List<Menu> findMenu(Integer status);
}
