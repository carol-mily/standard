package com.example.standard_manage_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
  @TableName("sys_type")
@ApiModel(value = "Type对象", description = "")
public class Type implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("类型名称")
      private String name;

      @ApiModelProperty("最大长度")
      private Integer maxLen;

      @ApiModelProperty("最小长度")
      private Integer minLen;


}
