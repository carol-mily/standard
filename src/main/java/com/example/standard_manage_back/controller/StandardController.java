package com.example.standard_manage_back.controller;


import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.*;
import com.example.standard_manage_back.entity.dto.SheetDTO;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.*;
import com.example.standard_manage_back.utils.HuExcelUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author carol
 */
@RestController
@RequestMapping("/standard")
public class StandardController {

    //导入service包
    @Resource
    private IStandardService standardService;
    @Resource
    private ILevelService levelService;
    @Resource
    private ITableService tableService;
    @Resource
    private IItemService itemService;
    @Resource
    private IUserService userService;

    //save即做到新增又做到更新
    @PostMapping
    public Result save(@RequestBody Standard standard){    //RequestBody将前台json对象转为后台java对象
        //新增或者更新
        return Result.success(standardService.saveOrUpdate(standard));
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Standard standard) {
        //新增
        try {
            standardService.save(standard);
            try{
                standardService.addEditor(standard.getId(),standard.getMphone());   //将管理员添加为编写者
                return Result.success("添加成功");
            }catch (Exception e){
                throw new ServiceException(Constants.CODE_ERROR,"编写者添加失败");   //抛出异常
            }
        } catch (DataAccessException e) {
            return Result.error(Constants.PARAM_HAS_EXISTED,"该item已存在");
        }
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Standard standard){
        //更新
        return Result.success("更新成功",standardService.updateById(standard));
    }

    //更改状态
    @PostMapping("/changeState")
    public Result changeState(@RequestBody Map map){
        Integer stanId=(Integer) map.get("stanId");
        Integer state=(Integer) map.get("state");
        try{
            return Result.success("操作成功",standardService.changeState(stanId, state));
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"操作失败");   //抛出异常
        }
    }

    //更改管理者电话
    @PostMapping("/changeMphone")
    public Result changeMphone(@RequestBody Map map){
        Integer stanId=Integer.parseInt(map.get("stanId").toString());
        String mphone=map.get("mphone").toString();
        standardService.changeMphone(stanId, mphone);
        return Result.success("操作成功");
    }

    //查询所有数据
    @GetMapping
    public Result findAll(){
        return Result.success("获取成功",standardService.list());
    }

    //根据id查询
    @GetMapping("/findById/{id}")
    public Result findOne(@PathVariable Integer id) {
        Standard stan=standardService.findOneById(id);
        List<Integer>levelList=levelService.findLevel(stan.getLevelId()); //找到levelid的levelList
        Collections.reverse(levelList); //逆转
        stan.setLevelList(levelList);
        return Result.success("获取成功",stan);
    }

    //分页查询
    @GetMapping("/getPage")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam (required = false) Integer levelid,
                           @RequestParam String name,
                           @RequestParam (required = false) Integer state) {
        //关联查询
        Page<Standard> page=standardService.findPage(new Page<>(pageNum,pageSize),levelid,name,state);
        for(Standard item:page.getRecords()){   //将levelList赋给item
            List<Integer>levelList=levelService.findLevel(item.getLevelId()); //找到levelid的levelList
            Collections.reverse(levelList); //逆转
            item.setLevelList(levelList);
        }
        return  Result.success("查询成功",page);
//        QueryWrapper<Standard> queryWrapper=new QueryWrapper<>();
//        if(state==3){
//            queryWrapper.eq("level_id",levelid);
//            queryWrapper.like("name",name);
//        }else{
//            queryWrapper.eq("level_id",levelid);
//            queryWrapper.eq("state",state);
//            queryWrapper.like("name",name);
//        }
//        return Result.success("获取成功",standardService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //通过负责人电话找到stan
    @GetMapping("/findByMphone")
    public Result findByMname(@RequestParam String mphone,
                              @RequestParam String name) {
        //关联查询
        List<Standard> stanList=standardService.findByMphone(mphone,name);
        for(Standard item:stanList){   //将levelList赋给item
            List<Integer>levelList=levelService.findLevel(item.getLevelId()); //找到levelid的levelList
            Collections.reverse(levelList); //逆转
            item.setLevelList(levelList);
        }
        return  Result.success("查询成功",stanList);
    }

    //通过编写者电话找到stan
    @GetMapping("/findByUphone")
    public Result findByUname(@RequestParam String uphone,
                              @RequestParam String name) {
        //关联查询
        List<Standard> stanList=standardService.findByUphone(uphone,name);
        for(Standard item:stanList){   //将levelList赋给item
            List<Integer>levelList=levelService.findLevel(item.getLevelId()); //找到levelid的levelList
            Collections.reverse(levelList); //逆转
            item.setLevelList(levelList);
        }
        return  Result.success("查询成功",stanList);
    }

    //删除单个
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try{
            List<Table>table=tableService.getTable(id);
            for(Table t:table){
                Integer tableId=t.getId();
                itemService.deleteByTableId(tableId);   //由于外键的原因必须先删item，再删除table，最后删除stan
                tableService.deleteById(tableId);
            }
            standardService.deleteEditors(id);  //删除编写者
            standardService.removeById(id);
//            standardService.deleteById(id);
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //删除多个
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        try{
//            standardService.removeByIds(ids);
            for(Integer id:ids){
//                standardService.deleteById(id);
                List<Table>table=tableService.getTable(id);
                for(Table t:table){
                    Integer tableId=t.getId();
                    itemService.deleteByTableId(tableId);   //由于外键的原因必须先删item，再删除table，最后删除stan
                    tableService.deleteById(tableId);
                }
                standardService.deleteEditors(id);  //删除编写者
                standardService.removeById(id);
            }
            return Result.success("批量删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"批量删除失败");   //抛出异常
        }
    }

    //添加编写者
    @PostMapping("/addEditor/{stanId}/{userPhone}")
    public Result addEditor(@PathVariable Integer stanId,
                            @PathVariable String userPhone){
        if(standardService.getById(stanId)!=null){
            try{
                standardService.addEditor(stanId,userPhone);
                return Result.success("添加成功");
            }catch (Exception e){
                throw new ServiceException(Constants.CODE_ERROR,"添加失败");   //抛出异常
            }
        }else{
            throw new ServiceException(Constants.PARAM_IS_BLANK,"数据标准不存在");   //抛出异常
        }
    }

    //删除编写者
    @PostMapping("/deleteEditor/{stanId}/{userPhone}")
    public Result deleteEditor(@PathVariable Integer stanId,
                            @PathVariable String userPhone){
        try{
            standardService.deleteEditor(stanId,userPhone);
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //找到当前分级名称
    public String findLevelName(Standard stan){
        List<Integer> levelList=levelService.findLevel(stan.getLevelId());
        String levelName=new String();
        for(Integer i:levelList){
            levelName=levelName+levelService.findLevelName(i)+'/';
        }
        levelName=levelName.substring(0,levelName.length()-1);  //去除最后一个/
        return levelName;
    }

//    //导出接口
//    @GetMapping("/export/{stanId}")
//    public void export(@PathVariable Integer stanId,
//                       HttpServletResponse response) throws Exception{
//        //从数据库查出standard
//        Standard standard =standardService.getById(stanId);
//        standard.setLevelName(findLevelName(standard));  //得到分级分类名字
//        standard.setMname(userService.getUserName(standard.getMphone()));
//        List<Standard> list = new ArrayList<>();
//        list.add(standard);
//        //在内存中操作，写出到浏览器
//        ExcelWriter writer = ExcelUtil.getWriter(true);
//        //自定义标题别名
//        writer.addHeaderAlias("name","名称");
//        writer.addHeaderAlias("ename","英文名称");
//        writer.addHeaderAlias("mname","负责人名字");
//        writer.addHeaderAlias("mphone","负责人电话");
//        writer.addHeaderAlias("creday","创建日期");
//        writer.addHeaderAlias("subday","发布日期");
//        writer.addHeaderAlias("levelName","分级分类");
//        writer.addHeaderAlias("description","描述");
//        //一次性写出list内的对象到excel，使用默认样式，强制输出标题
//        writer.write(list,true);
//
//        //设置浏览器响应的格式，response为HttpServletResponse对象
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
//        String fileName= URLEncoder.encode(standard.getName(),"UTF-8"); //文件名称
//        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");
//
//        ServletOutputStream out=response.getOutputStream(); //获取输出流
//        writer.flush(out, true);
//        out.close();
//        writer.close();
//    }

    //导出接口
    @GetMapping("/export/{stanId}")
    public void exportAll(@PathVariable Integer stanId,
                          HttpServletResponse response) throws Exception{
        //从数据库查出standard
        Standard standard =standardService.getById(stanId);
        standard.setLevelName(findLevelName(standard));  //得到分级分类名字
        standard.setMname(userService.getUserName(standard.getMphone()));
        List<SheetDTO> sheetList = new ArrayList<SheetDTO>();
        //添加基本信息
        List<Standard> infoDatas = new ArrayList<>();
        infoDatas.add(standard);
        SheetDTO sheet1 = new SheetDTO();
        //自定义标题别名
        Map<String,String> map1=new HashMap<>();
        map1.put("name","名称");
        map1.put("ename","英文名称");
        map1.put("mname","负责人名字");
        map1.put("mphone","负责人电话");
        map1.put("creday","创建日期");
        map1.put("subday","发布日期");
        map1.put("levelName","分级分类");
        map1.put("description","描述");
        sheet1.setFieldAndAlias(map1);
        sheet1.setSheetName("基础信息");
        sheet1.setCollection(infoDatas);
        sheetList.add(sheet1);
        //添加表的信息
        for(Table table:tableService.getTable(stanId)){
            List<Item> itemDatas=itemService.findByTableId(table.getId());
            //添加基本信息
            SheetDTO sheet = new SheetDTO();
            //自定义标题别名
            Map<String,String> map=new HashMap<>();
            map.put("name","字段名称");
            map.put("code","字段编码");
            map.put("typeName","类型");
            map.put("length","长度");
            map.put("decim","小数位数");
            map.put("cstraint","约束");
            map.put("remarks","备注");
            sheet.setFieldAndAlias(map);
            sheet.setSheetName(table.getName());
            sheet.setCollection(itemDatas);
            sheetList.add(sheet);
        }
        HuExcelUtils.exportExcel(response, sheetList, standard.getName());
    }
}

