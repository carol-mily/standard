package com.example.standard_manage_back.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.Level;
import com.example.standard_manage_back.entity.Standard;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.ILevelService;
import com.example.standard_manage_back.service.IStandardService;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author carol
 */
@RestController
@RequestMapping("/level")
public class LevelController {

    //导入service包
    @Resource
    private ILevelService levelService;
    @Resource
    private IStandardService standardService;

    //save即做到新增又做到更新
    @PostMapping
    public Result save(@RequestBody Level level){    //RequestBody将前台json对象转为后台java对象
        //新增或者更新
        return Result.success(levelService.saveOrUpdate(level));
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Level level) {
        String name=level.getName();
        if(StrUtil.isBlank(name)||level.getLevel()==null){
            return Result.error(Constants.PARAM_NOT_COMPLETE,"参数缺失");
        }
        //判断在该父分类下其名称是否重复
        if(!levelService.nameIsExisted(level)){
            if(!levelService.hasChildren(level.getPid())&&level.getLevel()!=1){  //pid下是不存在孩子且分级不为1
                Integer pid=level.getPid();
                levelService.createFirst(level.getLevel(),pid);
                Integer firstId=levelService.getChildren(pid).get(0).getId();
                List<Standard> stanList=standardService.findByLevelId(pid);
                for(Standard item:stanList){
                    standardService.changeLevel(item.getId(),firstId);
                }
            }
            try {
                //新增
                return Result.success("添加成功",levelService.save(level));
            } catch (DataAccessException e) {
                return Result.error(Constants.CODE_ERROR,"系统错误");
            }
        }else{
            throw new ServiceException(Constants.PARAM_HAS_EXISTED,"该分级已存在");   //抛出异常
        }
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Level level){
        String name=level.getName();
        if(StrUtil.isBlank(name)||level.getId()==null){
            return Result.error(Constants.PARAM_NOT_COMPLETE,"参数缺失");
        }
        //判断在该父分类下其名称是否重复
        if(!levelService.nameIsExisted(level)){
            try {
                //更新
                return Result.success("更新成功",levelService.updateById(level));
            } catch (DataAccessException e) {
                return Result.error(Constants.CODE_ERROR,"系统错误");
            }
        }else{
            throw new ServiceException(Constants.PARAM_HAS_EXISTED,"该分级已存在");   //抛出异常
        }
    }

    //查询所有数据
    @GetMapping("/findAll")
    public Result findAll(){
        return Result.success("获取成功",levelService.findAll());
    }

    //根据id查询
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success("获取成功",levelService.getById(id));
    }

    //分页查询
    @GetMapping("/findPage")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name,
                           @RequestParam Integer level,
                           @RequestParam(required = false) Integer pid) {
        Page<Level> page=new Page<>(pageNum,pageSize);
        QueryWrapper<Level> queryWrapper=new QueryWrapper<>();
        if(level==1){
            queryWrapper.eq("level",level);
            queryWrapper.like("name",name);
        }else{
            queryWrapper.eq("level",level);
            queryWrapper.eq("pid",pid);
            queryWrapper.like("name",name);
        }
        page=levelService.page(page,queryWrapper);
        List<Level>  levelList=page.getRecords();
        for(Level l:levelList){
            //得到子分级数目
            Integer childrenNum=levelService.getChildren(l.getId()).size();
            l.setChildrenNum(childrenNum);
            //得到数据标准数目
            Integer stanNum=standardService.findByLevelId(l.getId()).size();   //找到id所对应的standard数目
            List<Level> descendants=levelService.getDescendants(l.getId()); //获取id下的子孙level
            for(Level i:descendants){
                Integer descId=i.getId();   //子孙level id
                List<Standard> stanList=standardService.findByLevelId(descId);   //找到子孙所对应的standard
                stanNum=stanNum+stanList.size();
            }
            l.setStanNum(stanNum);
        }
        page.setRecords(levelList);
        return Result.success("获取成功",page);
    }

    //删除单个
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try{
            Level level=levelService.getById(id);
            Integer target=levelService.getTarget(id);  //获得移动的level
            List<Level> levelList=levelService.getDescendants(id); //获取id下的子孙level
            levelList.add(level);   //将自己加入，以改变自己下面的stan
            for(Level l:levelList){
                Integer descId=l.getId();   //子孙level id
                List<Standard> stanList=standardService.findByLevelId(descId);   //找到子孙所对应的standard
                for(Standard s:stanList){
                    standardService.changeLevel(s.getId(),target);  //改变level_id
                }
                levelService.removeById(descId);    //删除子孙分级
            }
            levelService.removeById(id);    //删除level
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //删除多个（不需要）
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        try{
            levelService.removeByIds(ids);
            return Result.success("批量删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"批量删除失败");   //抛出异常
        }
    }
}

