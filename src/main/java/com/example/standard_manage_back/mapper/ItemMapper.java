package com.example.standard_manage_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.standard_manage_back.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author carol
 */
@Mapper
public interface ItemMapper extends BaseMapper<Item> {

    List<Item> findByTableId(@Param("tableId") Integer tableId);
}
