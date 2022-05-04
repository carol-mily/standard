package com.example.standard_manage_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author carol
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    Page<Message> getReceive(@Param("page") Page<Message> page,@Param("uphone") String uphone,@Param("type") Integer type,@Param("state") Integer state);

    Page<Message> getSend(@Param("page") Page<Message> page,@Param("sphone") String sphone);

    void setReadBatch(@Param("ids")List<Integer> ids,@Param("phone")String phone);

    void setRead(@Param("id")Integer id,@Param("phone") String phone);

    void deleteRec(@Param("id")Integer id,@Param("phone") String phone);

    void deleteRecBatch(@Param("ids") ArrayList<Integer> ids, @Param("phone") String phone);

    void deleteSendBatch(@Param("ids") List<Integer> ids);

    void deleteSend (@Param("id") Integer id);

    void sendMessage(@Param("id")Integer id, @Param("phone") String phone, @Param("time")Timestamp time);
}
