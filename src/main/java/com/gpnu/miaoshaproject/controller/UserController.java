package com.gpnu.miaoshaproject.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.alibaba.druid.util.StringUtils;
import com.gpnu.miaoshaproject.controller.UserVO.UserVO;
import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.error.EmBusinessError;
import com.gpnu.miaoshaproject.model.UserModel;
import com.gpnu.miaoshaproject.response.CommonReturnType;
import com.gpnu.miaoshaproject.service.UserService;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    //获取session
    @Autowired
    private HttpServletRequest httpServletRequest;

    //登录
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType checkLogin(@RequestParam(name = "telphone") String telphone,@RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //判空
        if(StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //将用户输入的密码进行加密并传过去验证
        UserModel userModel = userService.validateLogin(telphone, EncodeNyMd5(password));
        //保存登录状态
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);

    }

    //用户注册接口
    //这里修改了部分代码
    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType register(UserModel user, @RequestParam(name = "otpCode") String otpCode) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpcode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(user.getTelphone());
        if (StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码错误");
        }
        //注册流程
        user.setRegisterMode("byPhone");
        user.setEncrptPassword(EncodeNyMd5(user.getEncrptPassword()));
        userService.register(user);
        return CommonReturnType.create(null);
    }

    //用户获取短信接口
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telephone) {
        //按照一定规则生成验证码
        Random random = new Random();
        int randonInt = random.nextInt(99999);   //生成0~99999的数字
        randonInt += 10000;
        String otpCode = String.valueOf(randonInt);
        //将OTP验证码通对应手机号关联。这里利用session
        httpServletRequest.getSession().setAttribute(telephone, otpCode);
        //发送短信
        System.out.println("手机--->" + telephone + "    验证码---->：" + randonInt);
        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
//            userModel.setEncrptPassword("asds");
        }
        UserVO userVO = converFormModel(userModel);
        return CommonReturnType.create(userVO);
    }

    //将核心领域对象转换成可供用户使用的。
    private UserVO converFormModel(UserModel userModel) {
        if (userModel == null)
            return null;
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }

    //md5加密
    public String EncodeNyMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newStr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }


}
