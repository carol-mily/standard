package com.example.standard_manage_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard_manage_back.entity.Item;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author carol
 */
public interface IItemService extends IService<Item> {
    List<Item> findByTableId(Integer tableId);

    boolean isNameExisted(Item item);

    boolean isCodeExisted(Item item);

    boolean isItemValid(Item item);

    void deleteByTableId(Integer tableId);
}
