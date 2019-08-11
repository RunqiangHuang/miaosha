package com.gpnu.miaoshaproject.service;


import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.model.UserModel;

public interface UserService {
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;
}
