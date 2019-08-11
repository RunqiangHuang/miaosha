package com.gpnu.miaoshaproject.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 统一返回处理
 */
@Getter
@Setter
public class CommonReturnType {
    //表明返回处理是success还是fail
    private String status;
    //若成功。返回需要的数据
    //若错误 返回通用错误码格式
    private Object data;

    //通用创建方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }
}
