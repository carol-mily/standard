package com.example.standard_manage_back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard_manage_back.entity.Standard;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author carol
 */
public interface IStandardService extends IService<Standard> {

    Page<Standard> findPage(Page<Standard> objectPage, Integer levelid, String name, Integer state);

    List<Standard> findByMphone(String mphone,String name);

    List<Standard> findByUphone(String uphone, String name);

    Standard findOneById(Integer id);

    List<Standard> findByLevelId(Integer levelId);

    void addEditor(Integer stanId, String userPhone);

    void deleteEditor(Integer stanId, String userPhone);

    boolean changeState(Integer stanId, Integer state);

    void changeMphone(Integer stanId,String mphone);

    void changeLevel(Integer stanId,Integer levelId);

    void deleteEditors(Integer stanId);

//    void deleteById(Integer id);
}
