package com.example.standard_manage_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard_manage_back.entity.Table;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author carol
 */
public interface ITableService extends IService<Table> {

    List<Table> findByStanId(Integer stanId);

    boolean isNameExisted(Table table);

    boolean isEnameExisted(Table table);

    boolean isTableValid(Table table);

//    void deleteById(Integer id);

//    void deleteByStanId(Integer stanId);

    void deleteById(Integer id);

    List<Table> getTable(Integer stanId);
}
