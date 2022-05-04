package com.example.standard_manage_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author carol
 */
@Getter
@Setter
  @TableName("sys_standard")
@ApiModel(value = "Standard对象", description = "")
public class Standard implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("名称")
      private String name;

      @ApiModelProperty("英文名称或编码")
      private String ename;

      @ApiModelProperty("管理员电话")
      private String mphone;

      @ApiModelProperty("状态，0编写，1审核，2发布")
      private Integer state;

      @ApiModelProperty("创建日期")
      private LocalDate creday;

      @ApiModelProperty("发布日期")
      private LocalDate subday;

      @ApiModelProperty("分级id")
      private Integer levelId;

      @ApiModelProperty("描述")
      private String description;

      @TableField(exist = false)
      private String mname;

      @TableField(exist = false)
      private List<Integer> levelList;

      @TableField(exist = false)
      private List<User> editors; //编写

      @TableField(exist = false)
      private String levelName; //分级分类名称，主要给excel生成
}
