package com.example.standard_manage_back.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.entity.Menu;
import com.example.standard_manage_back.entity.User;
import com.example.standard_manage_back.entity.dto.UserDTO;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.mapper.UserMapper;
import com.example.standard_manage_back.service.IUserService;
import com.example.standard_manage_back.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final Log LOG = Log.get();
    @Resource
    private UserMapper userMapper;

    @Resource
    private MenuServiceImpl menuService;

    @Override
    public UserDTO login(UserDTO userDTO) {
        //数据库查询
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("phone",userDTO.getPhone());
        queryWrapper.eq("password",userDTO.getPassword());
        User one;
        try{
            one=getOne(queryWrapper);  //获取一条记录
        }catch (Exception e){   //若查到多条数据，则返回false
            LOG.error(e);   //打印log
            throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
        }
        if(one!=null){  //防止被”系统错误“覆盖
            BeanUtil.copyProperties(one,userDTO,true);   //将数据库信息copy到userDTO中,true忽略大小写
            //设置token
            String token=TokenUtils.genToken(one.getPhone(),one.getPassword());
            userDTO.setToken(token);
            //得到当前用户的菜单与路由
            List<Menu> menu=menuService.findMenu(userDTO.getStatus());
            userDTO.setMenu(menu);
            return userDTO;
        }else{
            throw new ServiceException(Constants.USER_LOGIN_ERROR,"用户名或密码错误");   //抛出异常
        }
    }

    @Override
    public void register(UserDTO userDTO) {
        User one=getUserInfo(userDTO);  //获取用户信息
        if(one==null){
            one =new User();
            BeanUtil.copyProperties(userDTO,one,true);
            try{
                java.util.Date date = new Date();//获得当前时间
                Timestamp time = new Timestamp(date.getTime());//将时间转换成Timestamp类型，这样便可以存入到Mysql数据库中
                one.setCreTime(time);
                one.setAvatar(Constants.default_avatar);   //设置默认头像
                save(one);  //把copy之后的用户对象存到数据库
            }catch (Exception e){
                LOG.error(e);   //打印log
                throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
            }
        }else{
            throw new ServiceException(Constants.USER_HAS_EXISTED,"用户已存在");
        }
    }

    @Override
    public boolean forgetPassword(UserDTO userDTO){
        User one=getUserInfo(userDTO);  //获取用户信息
        if(one!=null){
            one.setPassword(userDTO.getPassword());
            try{
                updateById(one);  //更新
            }catch (Exception e){
                LOG.error(e);   //打印log
                throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
            }
        }else{
            throw new ServiceException(Constants.USER_HAS_EXISTED,"用户不存在");
        }
        return true;
    }

    //根据phone获取用户信息
    private User getUserInfo(UserDTO userDTO) {
        //数据库查询
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("phone",userDTO.getPhone());
        User one;
        try{
            one=getOne(queryWrapper);  //获取一条记录
        }catch (Exception e){   //若查到多条数据，则返回false
            LOG.error(e);   //打印log
            throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
        }
        return one;
    }

    public String getUserName(String phone){
        User user=getById(phone);
        return user.getName();
    }

    //找到user中所有的phone
    @Override
    public List<String> selectPhones() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(User.class, e -> "phone".equals(e.getColumn()));
        return userMapper.selectObjs(queryWrapper).stream()
                .map(o -> (String) o)
                .collect(Collectors.toList());
    }

    //移除指定字段
    @Override
    public void removePhone(List<String> list, String target){
        for(int i = list.size() - 1; i >= 0; i--){
            String item = list.get(i);
            if(target.equals(item)){
                list.remove(item);
            }
        }
    }
}
