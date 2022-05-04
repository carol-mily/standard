package com.example.standard_manage_back.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一返回包装类
 * 对传出的数据进行统一包装
 */

@Data   //setter、getter
@NoArgsConstructor  //无参构造
@AllArgsConstructor //有参构造
public class Result {
    private String code;
    private String message;
    private Object data;

    public static Result success(){
        return new Result(Constants.CODE_SUCCESS,"成功",null);
    }

    public static Result success(Object data){
        return new Result(Constants.CODE_SUCCESS,"成功",data);
    }

    public static Result success(String message){
        return new Result(Constants.CODE_SUCCESS,message,null);
    }

    public static Result success(String message,Object data){
        return new Result(Constants.CODE_SUCCESS,message,data);
    }

    public static Result error(String code,String message){
        return new Result(code,message,null);
    }

    public static Result error(){
        return new Result(Constants.CODE_ERROR,"系统错误",null);
    }
}
