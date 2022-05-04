package com.example.standard_manage_back.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.Table;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.IItemService;
import com.example.standard_manage_back.service.ITableService;
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
@RequestMapping("/table")
public class TableController {

    //导入service包
    @Resource
    private ITableService tableService;
    @Resource
    private IItemService itemService;

    //save即做到新增又做到更新
    @PostMapping
    public Result save(@RequestBody Table table){    //RequestBody将前台json对象转为后台java对象
        //新增或者更新
        return Result.success(tableService.saveOrUpdate(table));
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Table table) {
        if(tableService.isTableValid(table)){
            try {
                return Result.success("添加成功",tableService.save(table));
            } catch (DataAccessException e) {
                return Result.error(Constants.PARAM_HAS_EXISTED,"该item已存在");
            }
        }else{
            return Result.error();
        }
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Table table){
        if(tableService.isTableValid(table)){
            try {
                //更新
                return Result.success("更新成功",tableService.updateById(table));
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
        return Result.success("获取成功",tableService.list());
    }

    //根据id查询
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success("获取成功",tableService.getById(id));
    }

    //分页查询
    @GetMapping("/getPage")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Table> queryWrapper=new QueryWrapper<>();
        return Result.success("获取成功",tableService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //通过stanId查询
    @GetMapping("/findByStanId"+"/{stanId}")
    public Result findByStanId(@PathVariable Integer stanId) {
        List<Table> tableList=tableService.findByStanId(stanId);
        return Result.success("获取成功",tableList);
    }

    //删除单个
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try{
            itemService.deleteByTableId(id);
            tableService.removeById(id);
//            tableService.deleteById(id);
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //删除多个
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        try{
//            tableService.removeByIds(ids);
            for(Integer id : ids){
//                tableService.deleteById(id);
                itemService.deleteByTableId(id);
                tableService.removeById(id);
            }
            return Result.success("批量删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"批量删除失败");   //抛出异常
        }
    }
}

