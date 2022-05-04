package com.example.standard_manage_back.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.User;
import com.example.standard_manage_back.entity.dto.UserDTO;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.service.IUserService;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author carol
 */
@RestController
@RequestMapping("/user")
public class UserController {

    //导入service包
    @Resource
    private IUserService userService;

    //登录
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){    //RequestBody将前台json对象转为后台java对象
        String phone=userDTO.getPhone();
        String password=userDTO.getPassword();
        if(StrUtil.isBlank(password)||phone==null){
            return Result.error(Constants.PARAM_NOT_COMPLETE,"参数缺失");
        }
        UserDTO dto=userService.login(userDTO);
        return Result.success("登陆成功",dto);
    }

    //注册
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO){    //RequestBody将前台json对象转为后台java对象
        String phone=userDTO.getPhone();
        String password=userDTO.getPassword();
        if(StrUtil.isBlank(password)||phone==null){
            return Result.error(Constants.PARAM_NOT_COMPLETE,"参数缺失");
        }
        userService.register(userDTO);
        return Result.success("注册成功");
    }

    //忘记密码
    @PostMapping("/forgetPassword")
    public Result forgetPassword(@RequestBody UserDTO userDTO){    //RequestBody将前台json对象转为后台java对象
        String phone=userDTO.getPhone();
        String password=userDTO.getPassword();
        if(StrUtil.isBlank(password)||phone==null){
            return Result.error(Constants.PARAM_NOT_COMPLETE,"参数缺失");
        }
        if(userService.forgetPassword(userDTO)){
            return Result.success("修改成功");
        }else{
            return Result.error();
        }
    }

    //save即做到新增又做到更新
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        //新增或者更新
        return Result.success(userService.saveOrUpdate(user));
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        //新增
        try {
            java.util.Date date = new Date();//获得当前时间
            Timestamp time = new Timestamp(date.getTime());//将时间转换成Timestamp类型，这样便可以存入到Mysql数据库中
            user.setCreTime(time);
            user.setAvatar(Constants.default_avatar);   //设置默认头像
            return Result.success("添加成功",userService.save(user));
        } catch (DataAccessException e) {
            return Result.error(Constants.PARAM_HAS_EXISTED,"该用户已存在");
        }
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody User user){
        //更新
        return Result.success("更新成功",userService.updateById(user));
    }

    //查询所有数据
    @GetMapping("/findAll")
    public Result findAll(){
//        List<User> all=userMapper.findAll();
//        return all;
        //mybatis-plus方式
        return Result.success("获取成功",userService.list());
    }

    //根据phone查询
    @GetMapping("/{phone}")
    public Result findOne(@PathVariable String phone) {
        return Result.success("获取成功",userService.getById(phone));
    }

//    //分页查询-手写
//    //接口路径：/user/getPage
//    //@RequestParam 接收 ?pageNum=1&pageSize=10 映射
//    //limit第一个参数=(pageNum-1)*pageSize 第二个参数=pageSize
//    @GetMapping("/getPage")
//    public Map<String, Object> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize,
//                                        @RequestParam String name,
//                                        @RequestParam Integer status){
//        pageNum=(pageNum-1)*pageSize;
//        name='%'+name+'%';  //模糊查询拼接
//        List<User> data=userMapper.selectPage(pageNum,pageSize,name,status);
//        //查询总条数
//        Integer count=userMapper.selectTotal(name,status);
//        Map<String,Object> res = new HashMap<>();
//        res.put("data",data);
//        res.put("count",count);
//        return res;
//    }

    //分页查询---mybatis-plus方式
    //接口路径：/user/getPage
    //@RequestParam 接收 ?pageNum=1&pageSize=10 映射
    //limit第一个参数=(pageNum-1)*pageSize 第二个参数=pageSize
    @GetMapping("/getPage")
    public Result findPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize,
                                @RequestParam(defaultValue = "") String name,
                                @RequestParam Integer status){
        Page<User> page=new Page<>(pageNum,pageSize);
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if(status==2){
            queryWrapper.like("name",name);
            queryWrapper.orderByDesc("cre_time");
        }else{
            queryWrapper.like("name",name);
            queryWrapper.eq("status",status);
            queryWrapper.orderByDesc("cre_time");   //不起作用，特别注意：数据库字段名不能大写
        }

//        //获取当前用户信息（TokenUtils.getCurrentUser()的使用）
//        User currentUser=TokenUtils.getCurrentUser();
//        System.out.println("获取当前用户信息------------username:"+currentUser.getName());

        return Result.success("获取成功",userService.page(page,queryWrapper));
    }

    //删除单个
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
//        return userMapper.deleteByPhone(phone);
        //mybatis-plus方式
        try{
            userService.removeById(id);
            return Result.success("删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"删除失败");   //抛出异常
        }
    }

    //删除多个
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        try{
            userService.removeByIds(ids);
            return Result.success("批量删除成功");
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"批量删除失败");   //抛出异常
        }
    }

    //导出接口
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        //从数据库查出所有数据
        List<User> list =userService.list();
        //在内存中操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("phone","手机号");
        writer.addHeaderAlias("password","密码");
        writer.addHeaderAlias("name","用户名");
        writer.addHeaderAlias("status","身份");
        writer.addHeaderAlias("addr","地址");
        writer.addHeaderAlias("sex","性别");
        writer.addHeaderAlias("birth","出生日期");
        writer.addHeaderAlias("email","邮箱");

        //一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list,true);

        //设置浏览器响应的格式，response为HttpServletResponse对象
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName= URLEncoder.encode("用户信息","UTF-8"); //文件名称
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

        ServletOutputStream out=response.getOutputStream(); //获取输出流
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    //导入接口，注意在excel里面的空格传入数据库时，为空字符串不是null
    @PostMapping("/import")
    public Result imp(MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            List<User> list = reader.readAll(User.class); //使用User接收对象，直接读则读不到中文的头
            System.out.println(list);   //打印
            userService.saveBatch(list);    //批量插入
            return Result.success("上传成功",true);
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_ERROR,"上传失败");   //抛出异常
        }
    }
}

