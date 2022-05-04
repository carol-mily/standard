package com.example.standard_manage_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author carol
 */
@Getter
@Setter
@TableName("sys_level")
@ApiModel(value = "Level对象", description = "")
public class Level implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("所属层级")
    private Integer level;

    @ApiModelProperty("父级id")
    private Integer pid;

    @ApiModelProperty("描述")
    private String description;

    //表示在实体类中有，但在数据库表格中没有
    @TableField(exist = false)
    private List<Level> children;

    @TableField(exist = false)
    private Integer childrenNum;    //下一级数目

    @TableField(exist = false)
    private Integer stanNum;    //数据标准数目
}
