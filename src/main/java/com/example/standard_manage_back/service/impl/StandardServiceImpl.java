package com.example.standard_manage_back.service.impl;

import cn.hutool.log.Log;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.entity.Standard;
import com.example.standard_manage_back.entity.User;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.mapper.StandardMapper;
import com.example.standard_manage_back.service.IStandardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author carol
 */
@Service
public class StandardServiceImpl extends ServiceImpl<StandardMapper, Standard> implements IStandardService {
    private static final Log LOG = Log.get();

    @Resource
    private StandardMapper standardMapper;

    @Override
    public Page<Standard> findPage(Page<Standard> page, Integer levelid, String name, Integer state){
        return standardMapper.findPage(page,name,levelid,state);
    }

    @Override
    public List<Standard> findByMphone(String mphone,String name){
        return standardMapper.findByMphone(mphone,name);
    }

    @Override
    public List<Standard> findByUphone(String uphone,String name){
        return standardMapper.findByUphone(uphone,name);
    }

    @Override
    public Standard findOneById(Integer id){
        return standardMapper.findById(id);
    }

    @Override
    public List<Standard> findByLevelId(Integer levelId){
        QueryWrapper<Standard> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("level_id",levelId);
        return list(queryWrapper);
    }

    //transactional控制事务保证原则性
    @Transactional
    @Override
    public void addEditor(Integer stanId, String userPhone){
        //先判断后增
//        standardMapper.deleteEditor(stanId,userPhone);
        if(standardMapper.isExisted(stanId,userPhone)==0){
            java.util.Date date = new Date();//获得当前时间
            Timestamp time = new Timestamp(date.getTime());//将时间转换成Timestamp类型，这样便可以存入到Mysql数据库中
            standardMapper.addEditor(stanId,userPhone,time);
        }
    }

    @Override
    public void deleteEditor(Integer stanId, String userPhone){
        standardMapper.deleteEditor(stanId,userPhone);
    }

    @Override
    public boolean changeState(Integer stanId, Integer state){
        Standard one= getById(stanId);  //获取数据表中
        if(one!=null){
            if(state==2){
                LocalDate localDate = LocalDate.now();//获得当前时间
                one.setSubday(localDate);
            }else if(state==0){
                one.setSubday(null);
            }
            one.setState(state);
            try{
                updateById(one);  //更新
            }catch (Exception e){
                LOG.error(e);   //打印log
                throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
            }
        }else{
            throw new ServiceException(Constants.USER_HAS_EXISTED,"数据标准不存在");
        }
        return true;
    }

    @Override
    public void changeMphone(Integer stanId,String mphone){
        Standard one= getById(stanId);  //获取数据表中
        if(one!=null){
            one.setMphone(mphone);
            try{
                updateById(one);  //更新
                addEditor(stanId,mphone);   //添加到编写者里
            }catch (Exception e){
                LOG.error(e);   //打印log
                throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
            }
        }else{
            throw new ServiceException(Constants.USER_HAS_EXISTED,"数据标准不存在");
        }
    }

    @Override
    public void changeLevel(Integer stanId,Integer levelId){
        Standard one= getById(stanId);  //获取数据表中
        if(one!=null){
            one.setLevelId(levelId);
            try{
                updateById(one);  //更新
            }catch (Exception e){
                LOG.error(e);   //打印log
                throw new ServiceException(Constants.CODE_ERROR,"系统错误");   //抛出异常
            }
        }else{
            throw new ServiceException(Constants.USER_HAS_EXISTED,"数据标准不存在");
        }
    }

    //删除全部编写者
    @Override
    public void deleteEditors(Integer stanId){
        standardMapper.deleteEditors(stanId);
    }

    //删除stan
//    @Override
//    public void deleteById(Integer id){
//        removeById(id); //删除stan
//        tableService.deleteByStanId(id);    //删除table
//        deleteEditors(id);  //删除编写者
//    }
}
