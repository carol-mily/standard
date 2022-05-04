package com.example.standard_manage_back.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.entity.Message;
import com.example.standard_manage_back.mapper.MessageMapper;
import com.example.standard_manage_back.service.IMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author carol
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    @Resource
    MessageMapper messageMapper;

    @Override
    public Page<Message> getReceive(Page<Message> page, String uphone, Integer type, Integer state) {
        return messageMapper.getReceive(page, uphone, type, state);
    }

    @Override
    public Page<Message> getSend(Page<Message> page, String sphone) {
        return messageMapper.getSend(page, sphone);
    }

    //transactional控制事务保证原则性
    @Transactional
    @Override
    public void setReadBatch(List<Integer> ids, String phone) {
        for (Integer id : ids) {
            messageMapper.setRead(id, phone);
        }
        //问题：setReadBatch只在list数目为1时有效
//        messageMapper.setReadBatch(ids,phone);
    }

    @Override
    public void setRead(Integer id, String phone) {
        messageMapper.setRead(id, phone);
    }

    @Override
    public void deleteSendBatch(List<Integer> ids){
        for (Integer id : ids) {
            messageMapper.deleteSend(id);
        }
    }

    @Transactional
    @Override
    public void deleteRecBatch(ArrayList<Integer> ids, String phone) {
        for (Integer id : ids) {
            messageMapper.deleteRec(id, phone);
        }
    }

    @Override
    public void deleteRec(Integer id, String phone) {
        messageMapper.deleteRec(id, phone);
    }

    @Transactional
    @Override
    public void sendMessage(Integer id, List<String> phones){
        java.util.Date date = new Date();//获得当前时间
        Timestamp time = new Timestamp(date.getTime());//将时间转换成Timestamp类型，这样便可以存入到Mysql数据库中
        for (String phone : phones) {
            messageMapper.deleteRec(id, phone); //先删后增
            messageMapper.sendMessage(id, phone, time);
        }
    }
}
