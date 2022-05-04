package com.example.standard_manage_back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard_manage_back.entity.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author carol
 */
public interface IMessageService extends IService<Message> {

    Page<Message> getReceive(Page<Message> page, String uphone, Integer type, Integer state);

    Page<Message> getSend(Page<Message> page, String sphone);

    void setReadBatch(List<Integer> ids,String phone);

    void setRead(Integer id, String phone);

    void deleteRec(Integer id,String phone);

    void deleteRecBatch(ArrayList<Integer> ids, String phone);

    void deleteSendBatch(List<Integer> ids);

    void sendMessage(Integer id, List<String> phones);
}
