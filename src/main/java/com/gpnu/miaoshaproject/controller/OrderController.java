package com.gpnu.miaoshaproject.controller;

import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.error.EmBusinessError;
import com.gpnu.miaoshaproject.model.OrderModel;
import com.gpnu.miaoshaproject.model.UserModel;
import com.gpnu.miaoshaproject.response.CommonReturnType;
import com.gpnu.miaoshaproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 封装下单请求
     * @param itemId item的id
     * @param amount 购买的量
     * @return 统一结果
     */
    @RequestMapping(value = "/createorder", method = RequestMethod.POST,consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId",required = false)Integer promoId) throws BusinessException {
        //获取登录信息
        Boolean isLogin = (Boolean) this.httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin.booleanValue()){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }
        UserModel userModel = (UserModel) this.httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderModel orderModel = orderService.createOrder(userModel.getId(),itemId,promoId,amount);
        return CommonReturnType.create(null);
    }
}
