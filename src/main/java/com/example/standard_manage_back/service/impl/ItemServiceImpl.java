package com.example.standard_manage_back.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.common.Constants;
import com.example.standard_manage_back.entity.Item;
import com.example.standard_manage_back.exception.ServiceException;
import com.example.standard_manage_back.mapper.ItemMapper;
import com.example.standard_manage_back.service.IItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author carol
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {
    private static final Log LOG = Log.get();
    @Resource
    private ItemMapper itemMapper;

    @Override
    public List<Item> findByTableId(Integer tableId){
        return itemMapper.findByTableId(tableId);
    }

    @Override
    public boolean isNameExisted(Item item){
        //数据库查询
        QueryWrapper<Item> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("table_id",item.getTableId());
        queryWrapper.eq("name",item.getName());
        List<Item> itemList;
        try{
            itemList=list(queryWrapper);
            if(itemList.size()==0){
                return false;
            }else{
                if(itemList.stream().anyMatch(i->item.getId()==i.getId())){
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
    public boolean isCodeExisted(Item item){
        QueryWrapper<Item> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("table_id",item.getTableId());
        queryWrapper.eq("code",item.getCode());
        List<Item> itemList;
        try{
            itemList=list(queryWrapper);
            if(itemList.size()==0){
                return false;
            }else{
                if(itemList.stream().anyMatch(i->item.getId()==i.getId())){
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
    public boolean isItemValid(Item item){
        String name=item.getName();
        String code=item.getCode();
        Integer typeId=item.getTypeId();
        Integer length=item.getLength();
        Integer tableId=item.getTableId();
        if(StrUtil.isBlank(name)||StrUtil.isBlank(code)||typeId==null||length==null||tableId==null){
            throw new ServiceException(Constants.PARAM_NOT_COMPLETE,"参数缺失");
        }
        if(isNameExisted(item)){
            throw new ServiceException(Constants.PARAM_HAS_EXISTED,"该字段名称已存在");
        }
        if(isCodeExisted(item)){
            throw new ServiceException(Constants.PARAM_HAS_EXISTED,"该字段代码已存在");
        }
        return true;
    }

    //删除tableId下的所有Item
    @Override
    public void deleteByTableId(Integer tableId){
        Map<String,Object> columnMap =new HashMap<>();
        columnMap.put("table_id",tableId);
        itemMapper.deleteByMap(columnMap);
    }
}
