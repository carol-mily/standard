package com.example.standard_manage_back.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.entity.Table;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.mapper.TableMapper;
import com.example.standard_manage_back.service.ITableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author carol
 */
@Service
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements ITableService {
    private static final Log LOG = Log.get();
    @Resource
    private TableMapper tableMapper;
    private ItemServiceImpl itemService;

    @Override
    public List<Table> findByStanId(Integer stanId){
        QueryWrapper<Table> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("stan_id",stanId);
        List<Table> list=list(queryWrapper);
        return list;
    }

    @Override
    public boolean isNameExisted(Table table){
        //数据库查询
        QueryWrapper<Table> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("stan_id",table.getStanId());
        queryWrapper.eq("name",table.getName());
        List<Table> tableList;
        try{
            tableList=list(queryWrapper);
            if(tableList.size()==0){
                return false;
            }else{
                if(tableList.stream().anyMatch(item->table.getId()==item.getId())){
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

    @Override
    public boolean isEnameExisted(Table table){
        //数据库查询
        QueryWrapper<Table> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("stan_id",table.getStanId());
        queryWrapper.eq("ename",table.getEname());
        List<Table> tableList;
        try{
            tableList=list(queryWrapper);
            if(tableList.size()==0){
                return false;
            }else{
                if(tableList.stream().anyMatch(item->table.getId()==item.getId())){
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

    @Override
    public boolean isTableValid(Table table){
        String name=table.getName();
        String ename=table.getEname();
        Integer stanId=table.getStanId();
        if(StrUtil.isBlank(name)||StrUtil.isBlank(ename)||stanId==null){
            throw new ServiceException(Constants.PARAM_NOT_COMPLETE,"参数缺失");
        }
        if(isNameExisted(table)){
            throw new ServiceException(Constants.PARAM_HAS_EXISTED,"该名称已存在");
        }
        if(isEnameExisted(table)){
            throw new ServiceException(Constants.PARAM_HAS_EXISTED,"该英文名称已存在");
        }
        return true;
    }

//    @Override
//    public void deleteById(Integer id){
//        itemService.deleteByTableId(id);
//        removeById(id);
//    }

//    @Override
//    public void deleteByStanId(Integer stanId){
//        //查询
//        QueryWrapper<Table> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("stan_id",stanId);
//        List<Table> table = tableMapper.selectList(queryWrapper);
//        for(Table t:table){ //遍历删除item
//            deleteById(t.getId());
//        }
//        Map<String,Object> columnMap =new HashMap<>();
//        columnMap.put("stan_id",stanId);
//        tableMapper.deleteByMap(columnMap);
//    }

    @Override
    public void deleteById(Integer id) {
        removeById(id);
    }

    @Override
    public List<Table> getTable(Integer stanId){
        //查询
        QueryWrapper<Table> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("stan_id",stanId);
        List<Table> table = tableMapper.selectList(queryWrapper);
        return table;
    }
}
