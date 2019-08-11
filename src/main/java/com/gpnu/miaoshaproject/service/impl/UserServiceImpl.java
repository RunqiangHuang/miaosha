package com.gpnu.miaoshaproject.service.impl;

import com.gpnu.miaoshaproject.dao.UserDOMapper;
import com.gpnu.miaoshaproject.dao.UserPasswordDOMapper;
import com.gpnu.miaoshaproject.dataobject.UserDO;
import com.gpnu.miaoshaproject.dataobject.UserPasswordDO;
import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.error.EmBusinessError;
import com.gpnu.miaoshaproject.model.UserModel;
import com.gpnu.miaoshaproject.service.UserService;
import com.gpnu.miaoshaproject.validator.ValidationResult;
import com.gpnu.miaoshaproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO user = userDOMapper.selectByPrimaryKey(id);
        //先看用户是否存在
        if (user == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(user.getId());
        UserModel userModel = convertFromDataObject(user, userPasswordDO);
        return userModel;
    }

    @Transactional
    @Override
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if (StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender() == null
//                || userModel.getAge() == null
//                || StringUtils.isEmpty(userModel.getTelphone())) {
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }

        //返回异常
        ValidationResult result = validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //实现 model -> dataobject方法
        UserDO userDo = convertFromModel(userModel);
        //这里手机号设置了不能重复。如果重复会有异常
        try {
            userDOMapper.insertSelective(userDo);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号码已注册");
        }
        userModel.setId(userDo.getId()); // 获取自增 id
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        //先查出该用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if (userDO == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        //查找密码
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);

        System.out.println(userModel.getEncrptPassword());
        //检查是否相等
        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        return userModel;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel usermodel) {
        if (usermodel == null)
            return null;
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(usermodel.getEncrptPassword());
        userPasswordDO.setUserId(usermodel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null)
            return null;
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    //类型封装
    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null)
            return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);
        if (userPasswordDO == null)
            return null;
        userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        return userModel;
    }
}
