package com.example.standard_manage_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

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
@TableName("sys_item")
@ApiModel(value = "Item对象", description = "")
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("代码")
    private String code;

    @ApiModelProperty("类型id")
    private Integer typeId;

    @ApiModelProperty("长度")
    private Integer length;

    @ApiModelProperty("小数位数")
    private Integer decim;

    @ApiModelProperty("约束")
    private String cstraint;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("tableId")
    private Integer tableId;

    @TableField(exist = false)
    private String typeName;

    @TableField(exist = false)
    private Integer maxLen;

    @TableField(exist = false)
    private Integer minLen;
}
