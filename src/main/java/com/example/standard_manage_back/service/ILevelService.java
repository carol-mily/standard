package com.example.standard_manage_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard_manage_back.entity.Level;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author carol
 */
public interface ILevelService extends IService<Level> {
    List<Level> findAll();

    boolean nameIsExisted(Level level);

    boolean hasChildren(Integer id);

    List<Level> getChildren(Integer id);

    List<Level> getDescendants(Integer id);

    String findLevelName(Integer id);

    boolean createFirst(Integer level,Integer pid);

    List<Integer> findLevel(Integer id);

    Integer findFirst(Integer id);

    Integer getTarget(Integer id);
}
