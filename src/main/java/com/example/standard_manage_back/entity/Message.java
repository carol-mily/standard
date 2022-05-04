package com.example.standard_manage_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
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
  @TableName("sys_message")
@ApiModel(value = "Message对象", description = "")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("标题")
      private String title;

      @ApiModelProperty("类型，0代表系统，1代表项目")
      private Integer type;

      @ApiModelProperty("发送时间")
      private LocalDate subday;

      @ApiModelProperty("发送者sender的手机号")
      private String sphone;

      @ApiModelProperty("正文")
      private String text;

      @ApiModelProperty("相关项目id")
      private String stanId;

      @ApiModelProperty("项目消息类型：1添加，2转让，3申请，4审核，5删除，6撤销")
      private String proType;

      @TableField(exist = false)
      private List<String> uphones; //接收者手机号码

      @TableField(exist = false)
      private String sname; //发送者姓名

      @TableField(exist = false)
      private Integer state; //找到的状态
}
