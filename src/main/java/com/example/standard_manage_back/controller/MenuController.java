package com.example.standard_manage_back.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.Menu;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.IMenuService;
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
@RequestMapping("/menu")
public class MenuController {

    //导入service包
    @Resource
    private IMenuService menuService;

    //save即做到新增又做到更新
    @PostMapping
    public Result save(@RequestBody Menu menu){    //RequestBody将前台json对象转为后台java对象
        //新增或者更新
        return Result.success(menuService.saveOrUpdate(menu));
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Menu menu) {
        //新增
        try {
            return Result.success("添加成功",menuService.save(menu));
        } catch (DataAccessException e) {
            return Result.error(Constants.PARAM_HAS_EXISTED,"该item已存在");
        }
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Menu menu){
        //更新
        return Result.success("更新成功",menuService.updateById(menu));
    }

    //查询所有数据
    @GetMapping("/findAll")
    public Result findAll(){
        return Result.success("获取成功",menuService.findAll());
    }

    //查询所有数据
    @GetMapping("/findMenu/{status}")
    public Result findMenu(@PathVariable Integer status){
        return Result.success("获取成功",menuService.findMenu(status));
    }

    //根据id查询
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success("获取成功",menuService.getById(id));
    }

    //分页查询
    @GetMapping("/getPage")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<Menu> queryWrapper=new QueryWrapper<>();
        return Result.success("获取成功",menuService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //删除单个
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try{
            menuService.removeById(id);
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //删除多个
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        try{
            menuService.removeByIds(ids);
            return Result.success("批量删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"批量删除失败");   //抛出异常
        }
    }
}

