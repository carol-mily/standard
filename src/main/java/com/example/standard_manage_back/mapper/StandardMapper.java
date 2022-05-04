package com.example.standard_manage_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.entity.Standard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author carol
 */
@Mapper
public interface StandardMapper extends BaseMapper<Standard> {

    Page<Standard> findPage(Page<Standard> page, @Param("name") String name,@Param("levelid")Integer levelid,@Param("state")Integer state);

    List<Standard> findByMphone(@Param("mphone")String mphone,@Param("name") String name);

    List<Standard> findByUphone(@Param("uphone")String uphone,@Param("name") String name);

    Standard findById(@Param("id")Integer id);

    void addEditor(@Param("stanId")Integer stanId, @Param("userPhone")String userPhone, @Param("time")Timestamp time);

    void deleteEditor(@Param("stanId")Integer stanId, @Param("userPhone")String userPhone);

    void deleteEditors(@Param("stanId")Integer stanId);

    Integer isExisted(@Param("stanId")Integer stanId, @Param("userPhone") String userPhone);
}
