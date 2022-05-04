package com.example.standard_manage_back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author carol
 */
@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "User对象", description = "")
@ToString   //若想将其打印，则添加；相关类似于在下面加一个同string方法
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    //告诉mybatis-plus主键
    @TableId
    private String phone;

    private String name;

    private String password;

    @ApiModelProperty("管理员或用户")
    private int status;

    private String addr;

    private Integer sex;

    private LocalDate birth;

    private String email;

    private String avatar;

    private Timestamp creTime;
}
