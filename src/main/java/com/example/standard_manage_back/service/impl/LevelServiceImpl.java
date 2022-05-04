package com.example.standard_manage_back.service.impl;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.entity.Level;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.mapper.LevelMapper;
import com.example.standard_manage_back.service.ILevelService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level> implements ILevelService {
    private static final Log LOG = Log.get();

    @Override
    //根据数据库信息，渲染Level
    public List<Level> findAll() {
        //查询所有数据
        List<Level> list=list();
        //找出第一级level
        List<Level> newLevel=list.stream().filter(l->l.getLevel()==1).collect(Collectors.toList());
        //找出第一级的children
        for(Level level1:newLevel){
            level1.setChildren(list.stream().filter(l->level1.getId().equals(l.getPid())).collect(Collectors.toList()));
        }
        //找出第二级的children
        for(Level level1:newLevel){
            for(Level level2:level1.getChildren()){
                level2.setChildren(list.stream().filter(l->level2.getId().equals(l.getPid())).collect(Collectors.toList()));
            }
        }
        return newLevel;
    }

    /**
     * 该名字是否在该级中已存在
     */
    @Override
    public boolean nameIsExisted(Level level){
        //数据库查询
        QueryWrapper<Level> queryWrapper=new QueryWrapper<>();
        if(level.getLevel()==1){
            queryWrapper.eq("level",1);
            queryWrapper.eq("name",level.getName());
        }else{
            queryWrapper.eq("pid",level.getPid());
            queryWrapper.eq("name",level.getName());
        }
        List<Level> levelList;
        try{
            levelList=list(queryWrapper);
            if(levelList.size()==0){
                return false;
            }else{
                if(levelList.stream().anyMatch(item->level.getId()==item.getId())){
                    return false;
                }else{
                    return true;
                }
            }
        }catch (Exception e){   //若查到多条数据，则返回false
            LOG.error(e);   //打印log
            throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
        }
    }

    /**
     * id是否存在子分级
     */
    @Override
    public boolean hasChildren(Integer id){
        QueryWrapper<Level> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("pid",id);
        List<Level> levelList=list(queryWrapper);   //查询
        if(levelList.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 找到id下的所有子分级
     */
    @Override
    public List<Level> getChildren(Integer id){
        QueryWrapper<Level> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("pid",id);
        List<Level> list=list(queryWrapper);   //查询
        return list;
    }

    /**
     * 找到id下的所有子孙分级
     */
    @Override
    public List<Level> getDescendants(Integer id){
        List<Level> list=getChildren(id);    //获得子分级
        List<Level> newList=new ArrayList<Level>(list);
        if(list!=null){
            for(Level item:list){   //找孙子分级
                if(item.getLevel()!=3){
                    List<Level> children=getChildren(item.getId());
                    for(Level i:children){
                        newList.add(i);
                    }
                }
            }
        }
       return newList;
    }

    @Override
    public String findLevelName(Integer id){
        Level level=getById(id);
        return level.getName();
    }

    /**
     * 在pid下创建默认分级
     */
    @Override
    public boolean createFirst(Integer level,Integer pid){
        Level one=new Level();
        one.setName("默认");
        one.setLevel(level);
        one.setPid(pid);
        one.setDescription("系统默认分级");
        try{
            save(one);
            return true;
        }catch (Exception e){
            LOG.error(e);   //打印log
            throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
        }
    }

    /**
     * 根据id获得对应的levelList
     * @param id
     * @return
     */
    @Override
    public List<Integer> findLevel(Integer id){
        List<Integer> levelList = new ArrayList<Integer>();
        Level l1=getById(id);   //找到当前level
        levelList.add(id);    //存入levelList
        if(l1.getLevel()!=1){
            Level l2=getById(l1.getPid());
            levelList.add(l2.getId());    //存入levelList
            if(l2.getLevel()!=1){
                Level l3=getById(l2.getPid());
                levelList.add(l3.getId());    //存入levelList
            }
        }
        return levelList;
    }

    /**
     * 找到当前level对应的“默认”
     * @param id
     * @return
     */
    @Override
    public Integer findFirst(Integer id){
        //找到pid
        Level level=getById(id);
        //数据库查询,找到默认
        QueryWrapper<Level> queryWrapper=new QueryWrapper<>();
        if(level.getLevel()!=1){
            Integer pid=level.getPid();
            queryWrapper.eq("pid",pid);
            queryWrapper.eq("name","默认");   //“默认”是由系统自建的
        }else{  //当level处于第一级时
            queryWrapper.eq("level",1);
            queryWrapper.eq("name","默认");   //“默认”是由系统自建的
        }
        Level firstLevel=getOne(queryWrapper);
        return firstLevel.getId();
    }

    /**
     * 删除时，数据标准移动的目标分级
     * @param id
     * @return
     */
    @Override
    public Integer getTarget(Integer id){
        Level level=getById(id);
        if(level.getName().equals("默认")){
            if(level.getLevel()!=1){    //删除“默认”，返回pid
                return level.getPid();
            }else{  //分级1“默认”不能删除
                throw new ServiceException(Constants.CODE_ERROR,"该分级不能删除");
            }
        }else{  //非“默认”，返回该级“默认”id
            return findFirst(id);
        }
    }
}
