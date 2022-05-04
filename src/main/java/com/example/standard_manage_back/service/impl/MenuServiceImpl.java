package com.example.standard_manage_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.entity.Menu;
import com.example.standard_manage_back.mapper.MenuMapper;
import com.example.standard_manage_back.service.IMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author carol
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Override
    //根据数据库信息，渲染Menu
    public List<Menu> findAll() {
        //查询所有数据
        List<Menu> list=list();
        //找出第一级menu
        List<Menu> newList=list.stream().filter(m->m.getPid()==null).collect(Collectors.toList());
        //找出第一级的children
        for(Menu menu:newList){
            menu.setChildren(list.stream().filter(m->menu.getId().equals(m.getPid())).collect(Collectors.toList()));
        }
        return newList;
    }

    @Override
    //根据status信息，获取Menu
    public List<Menu> findMenu(Integer status){
        //查询所有数据
        List<Menu> list=list();
        //找出第一级menu
        List<Menu> newList=list.stream().filter(m->(m.getPid()==null&&(status.equals(m.getStatus())||m.getStatus()==2))).collect(Collectors.toList());
        //找出第一级的children
        for(Menu menu:newList){
            menu.setChildren(list.stream().filter(m->(menu.getId().equals(m.getPid())&&(status.equals(m.getStatus())||m.getStatus()==2))).collect(Collectors.toList()));
        }
        return newList;
    }
}
