package com.gpnu.miaoshaproject.controller;

import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.error.EmBusinessError;
import com.gpnu.miaoshaproject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//统一一下。每个controller都会用到
public class BaseController {

    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

    //异常处理
    //ExceptionHandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handleException(HttpServletRequest request, Exception ex){
        Map<String, Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException){
            BusinessException businessException = (BusinessException) ex;
            responseData = new HashMap<>();
            responseData.put("errCode",businessException.getErrorCode());
            responseData.put("errMsg",businessException.getErrMsg());
        }else{
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData,"fail");

    }
}
