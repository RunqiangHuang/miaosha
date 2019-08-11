package com.gpnu.miaoshaproject.validator;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Getter
@Setter
public class ValidationResult {
    //检验结果有没有错
    private boolean hasErrors = false;
    //存放信息的map
    private Map<String, String> errorMsgMap = new HashMap<>();

    //实现通用的返回错误结果
    public String getErrMsg() {
        //逗号连接在一起
        return StringUtils.join(errorMsgMap.values().toArray(), ",");
    }
    public boolean isHasErrors() {
        return hasErrors;
    }

}
