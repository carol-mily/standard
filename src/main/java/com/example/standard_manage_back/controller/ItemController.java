package com.example.standard_manage_back.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.Item;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.IItemService;
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
@RequestMapping("/item")
public class ItemController {

    //导入service包
    @Resource
    private IItemService itemService;

    //save即做到新增又做到更新
    @PostMapping
    public Result save(@RequestBody Item item){    //RequestBody将前台json对象转为后台java对象
        //新增或者更新
        return Result.success(itemService.saveOrUpdate(item));
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Item item) {
        if(itemService.isItemValid(item)){
            try {
                return Result.success("添加成功",itemService.save(item));
            } catch (DataAccessException e) {
                return Result.error(Constants.PARAM_HAS_EXISTED,"该item已存在");
            }
        }else{
            return Result.error();
        }
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Item item){
        if(itemService.isItemValid(item)){
            try {
                //更新
                return Result.success("更新成功",itemService.updateById(item));
            } catch (DataAccessException e) {
                return Result.error();
            }
        }else{
            return Result.error();
        }
    }

    //查询所有数据
    @GetMapping
    public Result findAll(){
        return Result.success("获取成功",itemService.list());
    }

    //根据id查询
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success("获取成功",itemService.getById(id));
    }

    //分页查询
    @GetMapping("/getPage")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<Item> queryWrapper=new QueryWrapper<>();
        return Result.success("获取成功",itemService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //通过stanId查询
    @GetMapping("/findByTableId"+"/{tableId}")
    public Result findByTableId(@PathVariable Integer tableId) {
        List<Item> itemList=itemService.findByTableId(tableId);
        return Result.success("获取成功",itemList);
    }

    //删除单个
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try{
            itemService.removeById(id);
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //删除多个
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        try{
            itemService.removeByIds(ids);
            return Result.success("批量删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"批量删除失败");   //抛出异常
        }
    }
}

