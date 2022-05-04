package com.example.standard_manage_back.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.Message;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.IMessageService;
import com.example.standard_manage_back.service.IUserService;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 * @author carol
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    //导入service包
    @Resource
    private IMessageService messageService;
    @Resource
    private IUserService userService;

    //save即做到新增又做到更新
    @PostMapping
    public Result save(@RequestBody Message message){    //RequestBody将前台json对象转为后台java对象
        //新增或者更新
        return Result.success(messageService.saveOrUpdate(message));
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Message message) {
        //新增
        try {
            return Result.success("添加成功",messageService.save(message));
        } catch (DataAccessException e) {
            return Result.error(Constants.PARAM_HAS_EXISTED,"该item已存在");
        }
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Message message){
        //更新
        return Result.success("更新成功",messageService.updateById(message));
    }


    //批量设为已读
    @PostMapping("/setRead")
    public Result setRead(@RequestBody Map map){
        List<Integer> idList=(List<Integer>) map.get("ids");
        try{
            messageService.setReadBatch(idList,(String)map.get("phone"));
            return Result.success("设置成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"设置失败");   //抛出异常
        }
    }

    //查询所有数据
    @GetMapping
    public Result findAll(){
        return Result.success("获取成功",messageService.list());
    }

    //根据id查询
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success("获取成功",messageService.getById(id));
    }

    //分页查询，接收的信息
    @GetMapping("/getReceive")
    public Result getReceive(@RequestParam Integer pageNum,
                             @RequestParam Integer pageSize,
                             @RequestParam String uphone,
                             @RequestParam Integer type,
                             @RequestParam Integer state) {
        return Result.success("获取成功",messageService.getReceive(new Page<>(pageNum, pageSize),uphone,type,state));
    }

    //分页查询，发送的信息
    @GetMapping("/getSend")
    public Result getSend(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam String sphone) {
        return Result.success("获取成功",messageService.getSend(new Page<>(pageNum, pageSize),sphone));
    }

    //删除单个
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try{
            messageService.removeById(id);
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //删除多个
    @PostMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        try{
            messageService.removeByIds(ids);
            return Result.success("批量删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"批量删除失败");   //抛出异常
        }
    }

    //批量删除发送记录
    @PostMapping("/deleteSend")
    public Result deleteSendBatch(@RequestBody List<Integer> ids){
        try{
            messageService.deleteSendBatch(ids);   //msg_receiver表
            messageService.removeByIds(ids);    //删除message表
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //批量删除接收记录
    @PostMapping("/deleteRec")
    public Result deleteRecBatch(@RequestBody Map map){
        //取出List的值
        ArrayList<Integer>  ids = (ArrayList<Integer>) map.get("ids");
        try{
            messageService.deleteRecBatch(ids,(String)map.get("phone"));
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //发送系统消息
    @PostMapping("/sendSysMsg")
    public Result sendSysMsg(@RequestBody Message message){
        try {
            messageService.save(message);   //在message表中添加
            Integer id=message.getId(); //获得id
            List<String> phones=userService.selectPhones(); //获得user中所有的phones
            userService.removePhone(phones,message.getSphone());
            messageService.sendMessage(id,phones);  //修改msg_receiver表
            return Result.success("发送成功");
        } catch (DataAccessException e) {
            return Result.error(Constants.PARAM_HAS_EXISTED,"发送失败");
        }
    }

    @PostMapping("/sendProMsg")
    public Result sendProMsg(@RequestBody Message message){
        try {
            messageService.save(message);   //在message表中添加
            Integer id=message.getId(); //获得id
            List<String> uphones=message.getUphones();
            userService.removePhone(uphones,message.getSphone());
            messageService.sendMessage(id,uphones);  //修改msg_receiver表
            return Result.success("发送成功");
        } catch (DataAccessException e) {
            return Result.error(Constants.PARAM_HAS_EXISTED,"发送失败");
        }
    }
}

