package com.example.standard_manage_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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
  @TableName("sys_table")
@ApiModel(value = "Table对象", description = "")
public class Table implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("名称")
      private String name;

      @ApiModelProperty("英文名")
      private String ename;

      @ApiModelProperty("创建日期")
      private LocalDate creday;

      @ApiModelProperty("描述")
      private String description;

      @ApiModelProperty("数据标准id")
      private Integer stanId;


}
